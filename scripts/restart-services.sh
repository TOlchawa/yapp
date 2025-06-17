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

sshpass -p "$SSH_PASSWORD" ssh -o StrictHostKeyChecking=no "$SSH_USER@$SSH_HOST" <<ENDSSH
cd "$SERVER_HOME"
./"$SERVER_SCRIPT"
cd "$FRONTEND_HOME"
./"$FRONTEND_SCRIPT"
ENDSSH
