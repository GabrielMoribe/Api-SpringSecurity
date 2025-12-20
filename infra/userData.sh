#!/bin/bash

sudo su
yum update -y
yum install -y docker
service docker start
usermod -a -G docker ec2-user

curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

cat <<EOF > /home/ec2-user/docker-compose.yml
services:
  db:
    image: postgres:16
    environment:
      - POSTGRES_DB=testSpringSecurity
      - POSTGRES_USER=${db_username}
      - POSTGRES_PASSWORD=${db_password}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: always

  app:
    image: gabrielmoribe/springsecurity-postgresql-app:latest
    ports:
      - "80:8080"
    environment:
      - DB_URL=jdbc:postgresql://db:5432/testSpringSecurity
      - DB_USERNAME=${db_username}
      - DB_PASSWORD=${db_password}
      - API_SECURITY_TOKEN_SECRET=${api_security_token_secret}
      - EMAIL_USERNAME=${email_username}
      - EMAIL_PASSWORD=${email_password}
    depends_on:
      - db
    restart: always

volumes:
  postgres_data:
EOF

chown ec2-user:ec2-user /home/ec2-user/docker-compose.yml

cd /home/ec2-user
docker-compose up -d