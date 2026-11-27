
# Library Management API

A RESTful Library Management API built with Java and Spring Boot. The application manages books, members, and borrowing operations with transactional stock management and validation rules.

## Technologies

- Java 17
- Spring Boot 3.3.2
- Spring Data JPA
- PostgreSQL
- Liquibase
- Gradle
- JUnit 5
- Mockito
- Swagger / OpenAPI

## Features

- CRUD operations for books and members
- Book borrowing and returning
- Book availability validation
- Maximum of 3 active loans per member
- Automatic book stock updates
- Transactional borrowing and returning
- Pessimistic locking for concurrent borrowing operations
- Exception handling
- Internationalization (Azerbaijani and English)
- Database schema management with Liquibase
- Unit and integration testing

## Requirements

- Java 17
- PostgreSQL
- Git

## Database Configuration

Create a PostgreSQL database named:

`library_management_db`

Set the `DB_PASSWORD` environment variable to your PostgreSQL password.

The application uses the PostgreSQL user `postgres` by default.

## Running the Application

Clone the repository:

```bash
git clone https://github.com/sahinbesirzade33/library-management-api.git
```

Navigate to the project directory:

```bash
cd library-management-api
```

Set the `DB_PASSWORD` environment variable according to your operating system.

Run the application:

```bash
./gradlew bootRun
```

On Windows PowerShell:

```powershell
.\gradlew bootRun
```

The application runs on:

`http://localhost:8080`

## API Endpoints

### Books

- `POST /api/books` - Create a book
- `GET /api/books` - Get all books
- `GET /api/books/{id}` - Get a book by ID
- `PUT /api/books/{id}` - Update a book
- `DELETE /api/books/{id}` - Delete a book

### Members

- `POST /api/members` - Create a member
- `GET /api/members` - Get all members
- `GET /api/members/{id}` - Get a member by ID
- `PUT /api/members/{id}` - Update a member
- `DELETE /api/members/{id}` - Delete a member

### Loans

- `POST /api/loans` - Borrow a book
- `GET /api/loans` - Get all loans
- `PUT /api/loans/{id}/return` - Return a book

## Business Rules

- A book cannot be borrowed if no copies are available.
- A member cannot have more than 3 active loans.
- Borrowing a book decreases the available stock.
- Returning a book increases the available stock.
- Only active loans can be returned.
- A loan cannot be returned more than once.
- Borrowing and returning operations are transactional.

## API Documentation

Swagger UI:

`http://localhost:8080/swagger-ui/index.html`

OpenAPI specification:

`http://localhost:8080/v3/api-docs`

## Internationalization

The API supports Azerbaijani and English responses based on the `Accept-Language` request header.

Supported languages:

- Azerbaijani (`az`)
- English (`en`)

## Running Tests

Run all tests:

```bash
./gradlew clean test
```

On Windows PowerShell:

```powershell
.\gradlew clean test
```

## License

This project was developed as a learning and portfolio project.
