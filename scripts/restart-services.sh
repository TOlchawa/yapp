#!/bin/bash
# Restart backend and frontend using remote scripts.
set -euo pipefail

: "${SERVER_HOME?Environment variable SERVER_HOME is required}"
: "${FRONTEND_HOME?Environment variable FRONTEND_HOME is required}"
: "${SERVER_SCRIPT?Environment variable SERVER_SCRIPT is required}"
: "${FRONTEND_SCRIPT?Environment variable FRONTEND_SCRIPT is required}"
: "${SSH_USER?Environment variable SSH_USER is required}"
: "${SSH_PASSWORD?Environment variable SSH_PASSWORD is required}"
: "${SSH_HOST?Environment variable SSH_HOST is required}"

# Check if sshpass is available
if ! command -v sshpass >/dev/null 2>&1; then
  echo "sshpass command not found. Install sshpass and try again." >&2
  exit 1
fi

# Number of the current attempt, passed from the workflow.
ATTEMPT="${ATTEMPT:-unknown}"
# Base wait time is 30 seconds. Add a random 10-30 seconds.
RANDOM_WAIT=$((RANDOM % 21 + 10))
LOCK_TIMEOUT=$((30 + RANDOM_WAIT))

sshpass -p "$SSH_PASSWORD" ssh -o StrictHostKeyChecking=no "$SSH_USER@$SSH_HOST" <<ENDSSH
# Prevent running multiple restarts at the same time. If the lock is taken,
# print a notice but continue with the restart anyway.
(
  if flock -n 9; then
    : # Lock acquired
  else
    echo "Another restart may be in progress on attempt ${ATTEMPT}. Continuing."
  fi
  cd "$SERVER_HOME"
  ./"$SERVER_SCRIPT"
  cd "$FRONTEND_HOME"
  ./"$FRONTEND_SCRIPT"
) 9>/tmp/restart-services.lock
ENDSSH
