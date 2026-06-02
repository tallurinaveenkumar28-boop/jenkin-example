#!/usr/bin/env bash
# ──────────────────────────────────────────────────────────────
# setup-jenkins.sh — Bootstrap Jenkins from scratch
# ──────────────────────────────────────────────────────────────
set -euo pipefail

echo "=============================="
echo " Jenkins Bootstrap Script"
echo "=============================="

# Load .env if present
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
fi

# Validate required vars
: "${AWS_ACCESS_KEY_ID:?Set AWS_ACCESS_KEY_ID in .env}"
: "${AWS_SECRET_ACCESS_KEY:?Set AWS_SECRET_ACCESS_KEY in .env}"

# Generate SSH key pair for Jenkins agent if missing
if [ ! -f jenkins/agent_ssh/id_rsa ]; then
  mkdir -p jenkins/agent_ssh
  ssh-keygen -t rsa -b 4096 -f jenkins/agent_ssh/id_rsa -N ""
  export JENKINS_AGENT_SSH_PUBKEY=$(cat jenkins/agent_ssh/id_rsa.pub)
  echo "SSH keys generated."
fi

# Pull & build
docker compose build --no-cache
docker compose up -d

echo ""
echo "Waiting for Jenkins to start..."
until curl -sf http://localhost:8080/login > /dev/null 2>&1; do
  printf '.'
  sleep 5
done

echo ""
echo "=============================="
echo " Jenkins is up at http://localhost:8080"
echo " Username: admin"
echo " Password: ${JENKINS_ADMIN_PASSWORD:-admin123}"
echo "=============================="
