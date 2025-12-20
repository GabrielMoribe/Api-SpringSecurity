variable "db_username" {
  description = "Username do banco de dados"
  type        = string
}

variable "db_password" {
  description = "Senha do banco de dados"
  type        = string
  sensitive   = true
}

variable "api_security_token_secret" {
  description = "Secret para geração de tokens JWT"
  type        = string
  sensitive   = true
}

variable "email_username" {
  description = "Email para envio de notificações"
  type        = string
}

variable "email_password" {
  description = "Senha do email"
  type        = string
  sensitive   = true
}
