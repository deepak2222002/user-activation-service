pipeline {

    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t user-activation-service .'
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    docker stop user-activation-service || true
                    docker rm user-activation-service || true

                    docker run -d \
                    --name user-activation-service \
                    --network backend_default \
                    --restart unless-stopped \
                    -p 8094:8443 \
                    -e LOGIN_URL=https://192.168.31.184:8090/auth/loginpage \
                    -e KAFKA_BOOTSTRAP_SERVERS="kafka:9092" \
                    user-activation-service
                '''
            }
        }

    }
}