# API Banco Digital

API REST para um banco digital simplificado, com transferências entre contas, extrato de movimentações e notificações assíncronas.

---

## Como rodar o projeto

### Pré-requisitos

- Java 21+
- Maven 3.8+

### Executar

```bash
# Na raiz do projeto
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

### Executar os testes

```bash
mvn test
```

### URLs úteis

| Recurso         | URL                                          |
|-----------------|----------------------------------------------|
| Swagger UI      | http://localhost:8080/swagger-ui.html        |
| OpenAPI JSON    | http://localhost:8080/api-docs               |
| H2 Console      | http://localhost:8080/h2-console             |

**Credenciais H2:** JDBC URL `jdbc:h2:mem:banco_digital`, usuário `sa`, senha em branco.

---

## Dados pré-carregados

Ao subir, o sistema carrega 4 clientes com suas respectivas contas disponíveis no Swagger para teste.

---

## Endpoints principais

### Clientes
| Método | Endpoint                  | Descrição                        |
|--------|---------------------------|----------------------------------|
| GET    | `/api/v1/clientes`        | Lista todos os clientes          |
| GET    | `/api/v1/clientes/{id}`   | Busca cliente por ID             |
| POST   | `/api/v1/clientes`        | Cadastra novo cliente + conta    |

### Contas
| Método | Endpoint                           | Descrição                    |
|--------|------------------------------------|------------------------------|
| GET    | `/api/v1/contas/{id}`              | Busca conta por ID           |
| GET    | `/api/v1/contas/cliente/{id}`      | Lista contas de um cliente   |
| PATCH  | `/api/v1/contas/{id}/desativar`    | Desativa uma conta           |

### Transferências
| Método | Endpoint                                    | Descrição                        |
|--------|---------------------------------------------|----------------------------------|
| POST   | `/api/v1/transferencias`                    | Realiza uma transferência        |
| GET    | `/api/v1/transferencias/{numeroConta}`               | Busca apenas saídas da conta       |
| GET    | `/api/v1/transferencias/extrato/{numeroConta}`  | Extrato completo da conta        |

---

## Decisões de Design e Arquitetura

### Arquitetura em Camadas (inspirada na Hexagonal)

O projeto é organizado em três camadas principais:

```
api/          → Controllers, DTOs, Exception Handler (camada de entrada)
application/  → Services com lógica de aplicação (orquestra domínio)
domain/       → Entidades, regras de negócio, exceções (núcleo)
infrastructure/ → JPA, Repositories, Notifications, Config (saída)
```

Essa separação permite trocar a camada de infraestrutura (ex: H2 → PostgreSQL) sem tocar nas regras de negócio.

### Regras de Negócio no Domínio

Os métodos `debitar()` e `creditar()` vivem na entidade `Conta`, não no service. Isso segue o princípio do Rich Domain Model: o objeto sabe se defender — lança `SaldoInsuficienteException` quando o saldo é insuficiente, sem precisar de validação externa.

### Notificações Assíncronas

Após uma transferência bem-sucedida, o sistema dispara uma notificação via webhook externo de forma **assíncrona** (`@Async`). Isso garante que:

1. O cliente recebe a resposta da API imediatamente, sem aguardar a chamada externa.
2. Falha na notificação **não reverte** a transferência — é apenas logada.

Um `ThreadPoolTaskExecutor` dedicado (`notificationExecutor`) isola esse tráfego externo do pool principal da aplicação.

### Banco de Dados

H2 in-memory foi escolhido para facilitar a execução sem setup externo. A troca por PostgreSQL requer apenas alterar o `application.yml` e adicionar o driver — nenhuma mudança no código de domínio ou application.

### Tratamento de Erros

`GlobalExceptionHandler` centraliza todos os erros e retorna respostas padronizadas (`ErroResponse`) com status HTTP semânticos:
- `400` — dados inválidos, conta inativa, transferência para a mesma conta
- `404` — entidade não encontrada
- `422` — saldo insuficiente (Unprocessable Entity é mais semântico que 400 aqui)
- `500` — erros inesperados