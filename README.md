# SpringSecurity-PostgreSQL

API REST desenvolvida com Spring Boot para gerenciamento de corretores de planos de saude, incluindo autenticacao JWT, gestao de clientes, cotacoes e assinaturas.

## Indice

- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Configuracao do Ambiente](#configuração-do-ambiente)
- [Execucao](#execução)
- [Autenticacao](#autenticação)
- [Endpoints da API](#endpoints-da-api)
- [Testes](#testes)
- [Deploy](#deploy)

## Tecnologias

- Java 21
- Spring Boot 3.5.8
- Spring Security
- PostgreSQL 16
- Redis 7
- Flyway (migracoes de banco de dados)
- JWT (autenticacao)
- Docker e Docker Compose
- Terraform (infraestrutura AWS)
- GitHub Actions (CI/CD)

## Arquitetura

O projeto segue uma arquitetura em camadas:

```
src/main/java/com/example/SpringSecurity/PostgreSQL/
├── config/          # Configuracoes (Security, JWT, Email, Database)
├── controller/      # Endpoints REST
├── domain/
│   ├── dto/         # Request e Response objects
│   ├── entity/      # Entidades JPA
│   └── enums/       # Enumeracoes
├── exceptions/      # Tratamento de excecoes
├── repository/      # Repositorios JPA
└── service/         # Logica de negocio
```

### Entidades Principais

- **User**: Usuarios do sistema (corretores) com roles ADMIN e USER
- **Client**: Clientes vinculados a um corretor
- **HealthPlan**: Planos de saude com fatores de preco por faixa etaria
- **Quotation**: Cotacoes de planos para clientes
- **RefreshToken**: Tokens de renovacao de sessao

## Configuracao do Ambiente

### Pre-requisitos

- Java 21
- Docker e Docker Compose
- Maven 3.9+

### Variaveis de Ambiente

Crie um arquivo `.env` na raiz do projeto:

```properties
DB_URL=jdbc:postgresql://localhost:5432/testSpringSecurity
DB_USERNAME=postgres
DB_PASSWORD=sua_senha
REDIS_HOST=localhost
REDIS_PORT=6379
API_SECURITY_TOKEN_SECRET=sua_chave_secreta_jwt
EMAIL_USERNAME=seu_email_mailtrap
EMAIL_PASSWORD=sua_senha_mailtrap

```

## Execucao

### Com Docker Compose

```sh
docker-compose up --build -d
```

Este comando inicia:
- PostgreSQL na porta 5432
- Redis na porta 6379
- Aplicacao Spring Boot na porta 8080

### Desenvolvimento Local

```sh
# Iniciar apenas banco e cache
docker-compose up -d postgres redis

# Executar aplicacao
./mvnw spring-boot:run
```

### Usuarios Padrao

O sistema cria automaticamente dois usuarios ao iniciar (exceto em ambiente de teste):

| Email | Senha | Role |
|-------|-------|------|
| admin@email.com | admin123 | ADMIN |
| user@email.com | user123 | USER |

## Autenticacao

A API utiliza autenticacao JWT com refresh tokens.

### Fluxo de Autenticacao

1. Registro de usuario em `/auth/register`
2. Verificacao de email com codigo de 6 digitos em `/auth/verify`
3. Login em `/auth/login` retorna access token e refresh token
4. Access token deve ser enviado no header `Authorization: Bearer {token}`
5. Renovacao de token em `/auth/refresh-token`

### Roles e Permissoes

- **USER**: Acesso a endpoints de perfil, clientes, cotacoes e assinaturas
- **ADMIN**: Acesso total, incluindo gestao de usuarios e planos de saude

## Endpoints da API

### Autenticacao (`/auth`)

| Metodo | Endpoint | Descricao | Autenticacao |
|--------|----------|-----------|--------------|
| POST | /register | Registrar novo usuario | Nao |
| POST | /login | Autenticar usuario | Nao |
| POST | /verify | Verificar conta com codigo | Nao |
| POST | /verify-account/resend | Reenviar codigo de verificacao | Nao |
| POST | /forgot-password | Solicitar reset de senha | Nao |
| POST | /reset-password | Redefinir senha | Nao |
| POST | /refresh-token | Renovar access token | Nao |
| POST | /logout | Encerrar sessao | Sim |

### Usuarios (`/users`)

| Metodo | Endpoint | Descricao | Role |
|--------|----------|-----------|------|
| GET | /profile | Obter perfil do usuario | USER |
| PUT | /profile | Atualizar perfil | USER |
| POST | /profile/change-email | Solicitar alteracao de email | USER |
| GET | /profile/change-email/confirm | Confirmar alteracao de email | Nao |
| DELETE | /profile/delete | Excluir conta | USER |

### Administracao (`/admin`)

| Metodo | Endpoint | Descricao | Role |
|--------|----------|-----------|------|
| GET | /allusers | Listar todos os usuarios (paginado) | ADMIN |
| DELETE | /users/{id} | Excluir usuario | ADMIN |
| PUT | /users/{id}/disable | Desabilitar usuario | ADMIN |

### Clientes (`/clients`)

| Metodo | Endpoint | Descricao | Role |
|--------|----------|-----------|------|
| POST | / | Criar cliente | USER |
| GET | / | Listar clientes do corretor | USER |
| GET | /{id} | Obter cliente por ID | USER |
| PUT | /{id} | Atualizar cliente | USER |
| DELETE | /{id} | Excluir cliente | USER |

### Planos de Saude (`/healthplans`)

| Metodo | Endpoint | Descricao | Role |
|--------|----------|-----------|------|
| GET | / | Listar todos os planos | USER |
| POST | / | Criar plano | ADMIN |
| PUT | /{id} | Atualizar plano | ADMIN |
| DELETE | /{id} | Excluir plano | ADMIN |

### Cotacoes (`/quotations`)

| Metodo | Endpoint | Descricao | Role |
|--------|----------|-----------|------|
| POST | / | Criar cotacao | USER |
| GET | / | Listar cotacoes do corretor | USER |
| GET | /{id} | Obter cotacao por ID | USER |
| PUT | /{id} | Atualizar cotacao | USER |
| DELETE | /{id} | Excluir cotacao | USER |

### Assinaturas (`/subscriptions`)

| Metodo | Endpoint | Descricao | Role |
|--------|----------|-----------|------|
| POST | /create | Criar assinatura | USER |
| GET | /status | Verificar status da assinatura | USER |
| POST | /cancel | Cancelar assinatura | USER |
| POST | /webhook | Webhook de notificacoes | Nao |
| GET | /callback | Callback de retorno | Nao |

### Formato de Resposta

Todas as respostas seguem o padrao `ApiResponse`:

```json
{
  "success": true,
  "message": "Operacao realizada com sucesso",
  "data": { },
  "timestamp": "2024-01-01T12:00:00"
}
```

## Testes

O projeto utiliza Testcontainers para testes de integracao com PostgreSQL real.

### Executar Testes

```sh
./mvnw test -Dspring.profiles.active=test
```

### Estrutura de Testes

```
src/test/java/
├── integrationTests/
│   ├── controller/    # Testes de endpoints
│   └── repository/    # Testes de repositorios
```

## Deploy

### CI/CD com GitHub Actions

O projeto possui pipelines configurados em `.github/workflows`:

- **maven.yaml**: Build e push de imagem Docker no push para `master`
- **develop.yml**: Deploy em ambiente de desenvolvimento
- **prod.yml**: Deploy em ambiente de producao
- **terraform.yml**: Workflow reutilizavel para provisionamento de infraestrutura

### Infraestrutura AWS

A infraestrutura e gerenciada via Terraform em `infra/`:

- EC2 para execucao da aplicacao
- S3 para armazenamento de state files
- Security Groups para controle de acesso
- RDS PostgreSQL (configuravel por ambiente)

### Deploy Manual

```sh
# Build da imagem
docker build -t springsecurity-postgresql-app .

# Push para registry
docker push seu-registry/springsecurity-postgresql-app:latest
```

### Configuracao por Ambiente

Arquivos de configuracao por ambiente em `infra/envs/`:

- `dev/terraform.tfvars`: Configuracoes de desenvolvimento
- `prod/terraform.tfvars`: Configuracoes de producao

O arquivo `infra/destroy_config.json` controla a destruicao de recursos por ambiente.

## Migracoes de Banco de Dados

As migracoes sao gerenciadas pelo Flyway e localizadas em `src/main/resources/db/migration/`.

