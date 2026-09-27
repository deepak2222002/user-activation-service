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
                withCredentials([usernamePassword(
                    credentialsId: 'db-remoteuser',
                    usernameVariable: 'DB_USERNAME',
                    passwordVariable: 'DB_PASSWORD'
                )]) {
                    sh '''
                    docker stop user-activation-service || true
                    docker rm user-activation-service || true

                    docker run -d \
                    --name user-activation-service \
                    --network backend_default \
                    --restart unless-stopped \
                    -p 8094:8443 \
                    -e DB_URL="jdbc:sqlserver://sqlserver:1433;databaseName=jobportal;trustServerCertificate=true" \
                    -e DB_USERNAME="$DB_USERNAME" \
                    -e DB_PASSWORD="$DB_PASSWORD" \
                    -e APP_LOGIN_URL=https://192.168.31.184:8090/auth/loginpage \
                    -e KAFKA_BOOTSTRAP_SERVERS="kafka:9092" \
                    user-activation-service
                '''
                }
            }
        }

    }
}