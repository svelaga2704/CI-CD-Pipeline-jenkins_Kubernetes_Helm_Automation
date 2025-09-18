# CI/CD Pipeline with Jenkins, Kubernetes, Helm & Kaniko

## 📌 Project Overview
This project demonstrates a **CI/CD pipeline** where:
- Source code is pushed to **GitHub**.
- **Jenkins** picks up changes via a pipeline job.
- **Kaniko** builds a Docker image inside Kubernetes (no Docker daemon required).
- The built image is pushed to **Docker Hub**.
- **kubectl** applies a Kubernetes deployment to roll out the new version.

This setup simulates a real-world automated CI/CD pipeline for containerized applications.

---

## ⚙️ Tools & Technologies
- **GitHub** – source control
- **Jenkins** – CI/CD orchestrator
- **Kubernetes** – container orchestration
- **Kaniko** – container image builder inside Kubernetes
- **Docker Hub** – container registry
- **kubectl** – deployment tool
- **Helm** (optional) – can be added for advanced deployment templating

---

## 🏗️ Pipeline Flow
1. **Code Commit**
   - Developer pushes changes to GitHub repository.

2. **Build Stage**
   - Jenkins triggers pipeline.
   - A Kaniko pod is created inside Kubernetes.
   - Kaniko builds Docker image from `Dockerfile.dockerfile`.
   - Image is pushed to Docker Hub → `docker.io/svelaga2704/ci-cd-demo:latest`.

3. **Deploy Stage**
   - Jenkins spins up a pod with `kubectl`.
   - `deployment.yaml` is applied to the cluster.
   - Updated application image is deployed.

---

## 📂 Repository Structure
.
├── app.py # Sample Python app
├── requirements.txt # Dependencies
├── Dockerfile.dockerfile # Docker build file
├── Jenkinsfile.groovy # Jenkins pipeline definition
├── k8s/
│ ├── deployment.yaml # Kubernetes Deployment spec
│ └── kaniko.yaml # Kaniko Pod template (standalone)
├── rbac-jenkins.yaml # RBAC for Jenkins service account
├── dockerhub-secret.yaml # Docker Hub registry secret
└── README.md # Project documentation

yaml
Copy code

---

## 🔑 Secrets & Configurations
- **Docker Hub Secret**  
  Created in namespace `ci`:
  ```bash
  kubectl create secret docker-registry dockerhub-secret \
    --docker-username=<DOCKER_USERNAME> \
    --docker-password=<DOCKER_PAT> \
    --docker-email=<DOCKER_EMAIL> \
    -n ci
This secret is mounted into Kaniko pod at /kaniko/.docker/config.json.

## 🚀 Running the Pipeline
Push any code changes to GitHub:

bash
Copy code
git add .
git commit -m "update app"
git push origin main
Jenkins automatically:

Creates Kaniko pod.

Builds and pushes image.

Deploys application using Kubernetes.

Verify image on Docker Hub:

bash
Copy code
https://hub.docker.com/repository/docker/svelaga2704/ci-cd-demo
Verify deployment:

bash
Copy code
kubectl get pods -n ci
kubectl get deployments -n ci
## ✅ Current Status
✔ GitHub → Jenkins integration working.

✔ Kaniko builds image successfully.

✔ Docker Hub push successful.

❌ Deploy stage initially failed due to wrong kubectl image.

🔧 Fix: use a valid kubectl image (e.g., bitnami/kubectl:latest).

## 📖 Lessons Learned
Kaniko requires dockerconfigjson secrets for pushing to private registries.

Using incorrect Docker Hub token scopes (read-only vs read/write) causes UNAUTHORIZED errors.

Jenkins Kubernetes plugin requires valid kubectl image tags; some tags (latest, 1.28.0) may not exist.

Always verify image availability on Docker Hub before referencing it in pipeline.

## Workflow On how it works

Developer → pushes code to GitHub Repo.
Jenkins (running in Kubernetes) detects changes and triggers the pipeline.
Jenkins starts a Kaniko Pod (Build) to build the Docker image securely inside Kubernetes.
Kaniko pushes the image to Docker Hub.
Jenkins then runs a Kubectl Pod (Deploy) that applies the deployment YAMLs/Helm charts.
The app is deployed in the Kubernetes Cluster.
Finally, the End User accesses the application.

This connects all the pieces: Kubernetes (to run Jenkins & workloads), Kaniko (secure container builds without Docker daemon), and Helm (to manage Kubernetes deployments).

## 🏁 Next Steps
Replace kubectl image in Jenkinsfile with a valid one:

groovy
Copy code
image: 'bitnami/kubectl:latest'
(Optional) Add Helm charts for better deployment management.

(Optional) Set up Ingress for exposing the app externally.

👨‍💻 Author
Sai Praneeth Velaga
This README documents **everything we did step by step**: GitHub → Jenkins → Kaniko → Docker Hub → Kubernetes.  

