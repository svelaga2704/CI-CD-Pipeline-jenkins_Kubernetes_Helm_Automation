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
                echo "✅ Kaniko build started inside pod"
            }
        }

        stage('Deploy to Kubernetes') {
            agent any
            steps {
                sh '''
                  kubectl apply -f k8s/deployment.yaml -n ci
                '''
            }
        }
    }
}
