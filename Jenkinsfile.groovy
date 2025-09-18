pipeline {
    agent none

    stages {
        stage('Build & Push Image') {
            agent {
                kubernetes {
                    label 'kaniko-build'
                    yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: kaniko
    image: gcr.io/kaniko-project/executor:latest
    command:
    - cat
    tty: true
    volumeMounts:
    - name: docker-config
      mountPath: /kaniko/.docker/
      readOnly: true
  volumes:
  - name: docker-config
    secret:
      secretName: dockerhub-secret
"""
                }
            }
            steps {
                container('kaniko') {
                    sh '''
                    /kaniko/executor \
                      --context=git://github.com/svelaga2704/CI-CD-Pipeline-jenkins_Kubernetes_Helm_Automation.git#main \
                      --dockerfile=Dockerfile.dockerfile \
                      --destination=docker.io/svelaga2704/ci-cd-demo:latest \
                      --verbosity=debug
                    '''
                }
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
