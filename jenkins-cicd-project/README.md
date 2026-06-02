# Jenkins CI/CD Project

A production-ready Jenkins setup with Configuration as Code (JCasC), shared pipeline libraries, and complete CI/CD pipelines for a Spring Boot app deploying to AWS EKS.

## Stack
- **Jenkins 2.440** (LTS) via Docker
- **JCasC** — full config managed as YAML (no manual UI setup)
- **Kubernetes plugin** — dynamic pod-based agents
- **Shared Library** — reusable steps (Slack notify, Docker push, Helm deploy, Trivy scan)
- **Pipelines** — Build, Deploy, Terraform, Cleanup

## Project Structure
```
jenkins-cicd-project/
├── docker-compose.yml              # Run Jenkins + agent locally
├── Jenkinsfile                     # Full end-to-end pipeline
├── jenkins/
│   ├── Dockerfile                  # Jenkins image with Docker, kubectl, Helm, Terraform, AWS CLI
│   ├── plugins.txt                 # All plugins installed at build time
│   ├── casc/
│   │   ├── jenkins.yaml            # JCasC — security, clouds, credentials, global config
│   │   └── jobs.yaml               # JCasC — auto-creates all pipeline jobs
│   └── init.groovy.d/
│       └── security.groovy         # Startup security hardening
├── pipelines/
│   ├── springboot-app/
│   │   ├── Jenkinsfile.build       # CI: compile → test → SonarQube → package
│   │   └── Jenkinsfile.deploy      # CD: Helm diff → approval → deploy → smoke test
│   ├── infra/
│   │   └── Jenkinsfile.terraform   # Terraform plan/apply/destroy with approval gate
│   └── cleanup/
│       └── Jenkinsfile.cleanup     # Weekly Docker + ECR + workspace cleanup
├── shared-library/
│   ├── vars/
│   │   ├── notifySlack.groovy      # Slack notifications
│   │   ├── dockerBuildPush.groovy  # ECR login + build + push
│   │   ├── helmDeploy.groovy       # EKS kubeconfig + Helm upgrade
│   │   └── trivyScan.groovy        # Container vulnerability scanning
│   └── src/com/example/
│       └── PipelineUtils.groovy    # Utility class (image tagging, branch checks)
├── scripts/
│   ├── setup-jenkins.sh            # One-command bootstrap
│   └── create-ecr-repo.sh          # Create ECR repo with scanning enabled
└── configs/
    └── .env.example                # All environment variables with descriptions
```

## Quick Start

### 1. Configure environment
```bash
cp configs/.env.example .env
# Edit .env with your AWS credentials, Slack token, etc.
```

### 2. Launch Jenkins
```bash
chmod +x scripts/setup-jenkins.sh
./scripts/setup-jenkins.sh
```
Jenkins will be available at **http://localhost:8080** (admin / your password from `.env`).

### 3. Create ECR repository
```bash
./scripts/create-ecr-repo.sh springboot-app us-east-1
```

### 4. Jobs auto-created via JCasC
After startup, these jobs are ready in the Jenkins UI:
- `SpringBoot-App/CI-Build` — triggered on every push
- `SpringBoot-App/CD-Deploy` — parameterized deploy to dev/staging/prod
- `SpringBoot-App/Full-Pipeline` — end-to-end build + deploy
- `Infrastructure/Terraform-Plan` — plan/apply/destroy EKS

## Pipeline Flow
```
Push to main
    │
    ▼
[CI-Build]  Compile → Unit Tests → Coverage → SonarQube → Package
    │
    ▼
[Docker]    Build image → Trivy scan → Push to ECR
    │
    ▼
[Deploy Dev]   Helm diff → deploy → smoke test
    │
    ▼
[Deploy Staging]
    │
    ▼
[Manual Approval] (prod only)
    │
    ▼
[Deploy Prod]  --atomic Helm deploy → rollout verify → Slack notify
```

## GitHub Secrets Required
| Secret | Description |
|--------|-------------|
| `AWS_ACCESS_KEY_ID` | IAM user access key |
| `AWS_SECRET_ACCESS_KEY` | IAM user secret |
| `SLACK_TOKEN` | Slack bot token |

## Rollback
Helm deploys use `--atomic` — automatic rollback on failure. Manual rollback:
```bash
helm rollback springboot-app -n default
```
