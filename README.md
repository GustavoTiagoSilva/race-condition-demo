# Race Conditions - Demo

This project is a software application designed to perform simple bank transfers between accounts. It demonstrates the identification and resolution of **race conditions** in high-concurrency systems.

## Project Objective

The goal of this project is to introduce the concept of **race conditions** and demonstrate how to mitigate them using **Pessimistic Locking**.

A **Race Condition** occurs when multiple executions compete for access to modify the same data, leading to inconsistent behaviors. Here, we use Pessimistic Lock to ensure data consistency during bank transfer operations.

## Prerequisites

1. **Start the Database**
   - Run the following command to start a PostgreSQL database via Docker:
     ```bash
     docker-compose up -d
     ```
   - **Note:** The database is recreated with each restart.

2. **Start the Application**
   - Once you start the application in your preferred IDE, the tables will be automatically created in the database.
   - The project includes an `import.sql` script to populate the database with initial test data.

## Project Entities

The system includes two main entities:

1. **AccountEntity**
   - `id`: Unique identifier (UUID).
   - `accountNumber`: Account number.
   - `accountAgency`: Account agency.
   - `balance`: Current account balance.
   - `user`: Reference to the account owner (`UserEntity`).

2. **UserEntity**
   - `id`: Unique identifier (UUID).
   - `firstName`: User's first name.
   - `lastName`: User's last name.
   - `email`: User's email.
   - `cpf`: User's CPF (ID number).
   - `account`: Reference to the user’s account (`AccountEntity`).

## Endpoints

### Perform Transfer

- **Route:** `/api/v1/accounts`
- **Method:** POST
- **Description:** Executes a bank transfer between two accounts.
- **Request Body (JSON):**
  ```json
  {
    "amount": "Double",
    "sourceAccountId": "UUID",
    "targetAccountId": "UUID"
  }

## Features

- Implementation of Pessimistic Locking to handle simultaneous access to accounts during transfer operations.

## How to Run the Project

1. Clone this repository.
2. Run the command docker-compose up -d to start the database.
3. Run the project in your preferred IDE or with the following command:
```bash
      ./mvnw spring-boot:run
```
4. The service will be available at localhost:8080.


