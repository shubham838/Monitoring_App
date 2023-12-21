pipeline {
    agent any

    environment {
        
        
        ARTIFACTORY_CREDENTIALS = credentials('your-artifactory-credentials-id')
        DOCKER_IMAGE = 'your-artifactory-repo/your-docker-image'
        DOCKER_TAG = 'latest'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    
                    checkout(
                            [$class: 'GitSCM', branches: [[name: '*/master']],
                            doGenerateSubmoduleConfigurations: false,
                            extensions: [[$class: 'CloneOption', depth: 1, noTags: false, reference: '', shallow: true]],
                            submoduleCfg: [], 
                            userRemoteConfigs: [[url: 'https://bitbucket.org/your-username/your-repo.git']]]
                      )
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                }
            }
        }

        stage('Push Docker Image to JFrog Artifactory') {
            steps {
                script {
                    
                    docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").tag("${DOCKER_IMAGE}:${DOCKER_TAG}", "${ARTIFACTORY_URL}/${DOCKER_IMAGE}:${DOCKER_TAG}")

                   
                    docker.withRegistry("${ARTIFACTORY_URL}", ARTIFACTORY_CREDENTIALS) {
                        // Push Docker image to Artifactory
                        docker.image("${ARTIFACTORY_URL}/${DOCKER_IMAGE}:${DOCKER_TAG}").push()
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline succeeded!'

            // harness cd
        }
        failure {
            echo 'Pipeline failed!'

            
        }
    }
}
