#!/bin/bash
set -e

BUCKET_NAME="${1:-dev-sa-east-1-app}"
VAR_FILE="${2:-terraform.tfvars}"

echo "Iniciando setup da infraestrutura..."

# 1. Obtém a região configurada no provider Terraform
TERRAFORM_REGION=$(grep -A5 'provider "aws"' provider.tf | grep 'region' | sed 's/.*=\s*"\(.*\)"/\1/' | tr -d ' ')

if [ -z "$TERRAFORM_REGION" ]; then
  echo "Erro: Não foi possível detectar a região no provider.tf"
  exit 1
fi

echo "Região do Terraform: $TERRAFORM_REGION"

# 2. Verifica se o bucket já existe na AWS
echo "Verificando se bucket '$BUCKET_NAME' existe..."

BUCKET_INFO=$(aws s3api head-bucket --bucket "$BUCKET_NAME" 2>&1) || BUCKET_EXISTS=false

if [ "$BUCKET_EXISTS" != "false" ]; then
  # 3. Obtém a região do bucket existente
  BUCKET_REGION=$(aws s3api get-bucket-location --bucket "$BUCKET_NAME" --query 'LocationConstraint' --output text 2>/dev/null)
  
  # AWS retorna "None" para us-east-1
  if [ "$BUCKET_REGION" == "None" ] || [ -z "$BUCKET_REGION" ]; then
    BUCKET_REGION="us-east-1"
  fi

  echo "Região do bucket existente: $BUCKET_REGION"

  # 4. Compara as regiões
  if [ "$BUCKET_REGION" != "$TERRAFORM_REGION" ]; then
    echo "   ERRO: Conflito de regiões detectado!"
    echo "   ├── Bucket '$BUCKET_NAME' está em: $BUCKET_REGION"
    echo "   └── Terraform configurado para:    $TERRAFORM_REGION"
    exit 1
  fi

  # 5. Inicializa o Terraform
  echo "Inicializando Terraform..."
  terraform init

  # 6. Verifica se já está no state do Terraform
  if terraform state show aws_s3_bucket.s3_bucket 2>/dev/null; then
    echo "Bucket já está no state do Terraform."
  else
    echo "Importando bucket para o state..."
    terraform import aws_s3_bucket.s3_bucket "$BUCKET_NAME"
  fi
else
  echo "Bucket '$BUCKET_NAME' não existe. Será criado pelo Terraform."
  
  # Inicializa o Terraform
  echo "Inicializando Terraform..."
  terraform init
fi

# 7. Aplica o Terraform
echo "Aplicando infraestrutura..."
terraform apply -var-file="$VAR_FILE"

echo "Setup concluído!"