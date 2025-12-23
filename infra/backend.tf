terraform {
  backend "s3" {
    bucket         = "gabrielmoribe-us-east-1-terraform-statefile"
    key            = "terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "gabrielmoribe-sa-east-1-terraform-lock"
    encrypt        = true
  }
}