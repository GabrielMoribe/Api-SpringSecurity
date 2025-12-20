terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.99.1"
    }
  }
}
provider "aws" {
  region = "us-east-1"
}

resource "aws_security_group" "ec2_sg" {
  name        = "test_ec2_security_group"
  description = "Allow SSH and HTTP inbound traffic"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 65535
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_key_pair" "keypair" {
  key_name = "aws_key_pair"
  public_key = file("~/.ssh/id_ed25519.pub")
}

resource "aws_instance" "server" {
  ami           = "ami-068c0051b15cdb816"
  instance_type = "t3.micro"
  user_data = templatefile("userData.sh", {
    db_username               = var.db_username
    db_password               = var.db_password
    api_security_token_secret = var.api_security_token_secret
    email_username            = var.email_username
    email_password            = var.email_password
  })
  key_name = aws_key_pair.keypair.key_name
  vpc_security_group_ids = [aws_security_group.ec2_sg.id]
}

output "ec2_public_ip" {
  value = aws_instance.server.public_ip
}