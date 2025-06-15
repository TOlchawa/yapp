#!/bin/bash
# Simple script to install Maven using apt-get.
# Run this if Maven is missing.
set -e

if command -v mvn >/dev/null 2>&1; then
  echo "Maven already installed"
  exit 0
fi

sudo apt-get update && sudo apt-get install -y maven
