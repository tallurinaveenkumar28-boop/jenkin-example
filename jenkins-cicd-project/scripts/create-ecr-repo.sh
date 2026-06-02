#!/usr/bin/env bash
# Creates the ECR repository if it doesn't already exist
set -euo pipefail

REPO_NAME="${1:-springboot-app}"
REGION="${2:-us-east-1}"

aws ecr describe-repositories --repository-names "$REPO_NAME" --region "$REGION" 2>/dev/null || \
  aws ecr create-repository \
    --repository-name "$REPO_NAME" \
    --region "$REGION" \
    --image-scanning-configuration scanOnPush=true \
    --encryption-configuration encryptionType=AES256

echo "ECR repo '$REPO_NAME' is ready in $REGION."
