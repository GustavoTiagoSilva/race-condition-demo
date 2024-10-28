# Race Conditions - Demo

Este projeto é um exemplo de aplicação de software para realizar transferências bancárias simples entre contas, desenvolvido com o objetivo de demonstrar a identificação e solução de **condições de corrida** (race conditions) em sistemas de alta concorrência.

## Objetivo do Projeto

O propósito deste projeto é introduzir o conceito de **condições de corrida** e ilustrar como mitigá-las utilizando a estratégia de **Pessimistic Locking**.

**Race Condition** ocorre quando múltiplas execuções concorrem pelo acesso e modificação dos mesmos dados, levando a comportamentos inconsistentes. Aqui, utilizamos o Pessimistic Lock para garantir a consistência dos dados durante operações de transferência bancária.

## Pré-requisitos para Executar

1. **Subir o Banco de Dados**
    - Execute o comando abaixo para subir um banco de dados PostgreSQL via Docker:
      ```bash
      docker-compose up -d
      ```
    - **Observação:** O banco de dados é recriado a cada reinicialização.

2. **Iniciar a Aplicação**
    - A partir do momento que iniciar a aplicação na sua IDE favorita, as tabelas serão automaticamente criadas no banco de dados.
    - O projeto contém um script `import.sql` que insere dados fictícios no banco para testes iniciais.

## Entidades do Projeto

O sistema possui duas entidades principais:

1. **AccountEntity**
    - `id`: Identificador único (UUID).
    - `accountNumber`: Número da conta.
    - `accountAgency`: Agência da conta.
    - `balance`: Saldo atual da conta.
    - `user`: Referência ao proprietário da conta (`UserEntity`).

2. **UserEntity**
    - `id`: Identificador único (UUID).
    - `firstName`: Nome do usuário.
    - `lastName`: Sobrenome do usuário.
    - `email`: E-mail do usuário.
    - `cpf`: CPF do usuário.
    - `account`: Referência à conta do usuário (`AccountEntity`).

## Endpoints

### Realizar Transferência

- **Rota:** `/api/v1/accounts`
- **Método:** POST
- **Descrição:** Realiza uma transferência bancária entre duas contas.
- **Corpo da Requisição (JSON):**
  ```json
  {
    "amount": "Double",
    "sourceAccountId": "UUID",
    "targetAccountId": "UUID"
  }


## Funcionalidades

- Implementação de Pessimistic Lock para lidar com acessos simultâneos a contas durante a execução de transferências.

## Como Rodar o Projeto

1. Clone este repositório.
2. Execute o comando docker-compose up -d para iniciar o banco de dados.
3. Execute o projeto em sua IDE favorita ou com o comando:
```bash
      ./mvnw spring-boot:run
```
4. O serviço estará disponível em localhost:8080.


