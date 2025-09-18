pipeline {
    agent none

    stages {
        stage('Build & Push Image') {
            agent {
                kubernetes {
                    label 'kaniko-build'
                    defaultContainer 'kaniko'
                    yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: kaniko
    image: gcr.io/kaniko-project/executor:latest
    args:
    - "--context=git://github.com/svelaga2704/CI-CD-Pipeline-jenkins_Kubernetes_Helm_Automation.git#main"
    - "--dockerfile=Dockerfile.dockerfile"
    - "--destination=docker.io/svelaga2704/ci-cd-demo:latest"
    - "--verbosity=debug"
    volumeMounts:
    - name: docker-config
      mountPath: /kaniko/.docker/
      readOnly: true
  volumes:
  - name: docker-config
    secret:
      secretName: dockerhub-secret
      items:
      - key: .dockerconfigjson
        path: config.json
"""
                }
            }
            steps {
                container('kaniko') {
                    echo "✅ Kaniko build started inside pod"
                }
            }
        }

        stage('Deploy to Kubernetes') {
            agent {
                kubernetes {
                    label 'kubectl-deployer'
                    defaultContainer 'kubectl'
                    yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: kubectl
    image: lachlanevenson/k8s-kubectl:v1.28.0
    command:
    - cat
    tty: true
"""
                }
            }
            steps {
                container('kubectl') {
                    sh '''
                      echo "🚀 Deploying to Kubernetes..."
                      # Force imagePullPolicy to Always so new images are pulled
                      sed -i 's/imagePullPolicy: .*/imagePullPolicy: Always/' k8s/deployment.yaml || true
                      
                      kubectl apply -f k8s/deployment.yaml -n ci
                      kubectl rollout status deployment/ci-cd-app -n ci
                    '''
                }
            }
        }
    }
}
