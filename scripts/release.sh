#!/bin/bash
# Automate changelog generation, version bump, git tag and GitHub release.

set -euo pipefail

# Find the last git tag. If none, start from zero.
LAST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || true)
if [ -z "$LAST_TAG" ]; then
  RANGE=""
  BASE_VERSION="0.0.0"
else
  RANGE="$LAST_TAG..HEAD"
  BASE_VERSION="${LAST_TAG#v}"
fi

# Collect commit messages since the last tag.
CHANGELOG=$(git log $RANGE --pretty=format:"- %s (%h)" )

# Write changelog to file
NOTES_FILE="release-notes.tmp"
echo "$CHANGELOG" > "$NOTES_FILE"

# Determine version bump type
MAJOR=0; MINOR=0; PATCH=0
IFS='.' read MAJOR MINOR PATCH <<< "$BASE_VERSION"

BUMP="patch"
if echo "$CHANGELOG" | grep -qiE 'BREAKING'; then
  BUMP="major"
elif echo "$CHANGELOG" | grep -qiE '^- feat'; then
  BUMP="minor"
fi

case $BUMP in
  major)
    MAJOR=$((MAJOR + 1)); MINOR=0; PATCH=0;;
  minor)
    MINOR=$((MINOR + 1)); PATCH=0;;
  patch)
    PATCH=$((PATCH + 1));;
esac

NEW_VERSION="v${MAJOR}.${MINOR}.${PATCH}"

# Create git tag
git tag -a "$NEW_VERSION" -F "$NOTES_FILE"

# Push tag
if git remote | grep -q origin; then
  git push origin "$NEW_VERSION"
fi

# Create GitHub release if gh CLI is available
if command -v gh >/dev/null 2>&1; then
  gh release create "$NEW_VERSION" --notes-file "$NOTES_FILE"
fi

# Show summary
cat <<EOF_SUMMARY
Created $NEW_VERSION with the following notes:
$CHANGELOG
EOF_SUMMARY



