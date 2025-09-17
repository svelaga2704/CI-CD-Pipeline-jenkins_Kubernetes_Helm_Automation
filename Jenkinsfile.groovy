pipeline {
    agent {
        kubernetes {
            yamlFile 'k8s/kaniko.yaml'
        }
    }
    environment {
        IMAGE_NAME = "svelaga2704/ci-cd-demo"
        IMAGE_TAG = "latest"
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/svelaga2704/CI-CD-Pipeline-jenkins_Kubernetes_Helm_Automation.git'
            }
        }
        stage('Build & Push with Kaniko') {
            steps {
                container('kaniko') {
                    sh '''
                        /kaniko/executor \
                        --dockerfile=Dockerfile.dockerfile \
                        --context=`pwd` \
                        --destination=$IMAGE_NAME:$IMAGE_TAG
                    '''
                }
            }
        }
        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                kubectl apply -f k8s/deployment.yaml -n ci
                '''
            }
        }
    }
}
