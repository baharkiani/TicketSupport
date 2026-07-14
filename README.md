# Ticket Support API

A simple RESTful Ticket Support API built with Spring Boot for managing support tickets.

## Features

- Create a new ticket
- Get all tickets
- Get ticket by ID
- Update ticket status
- Input validation
- Global exception handling
- Swagger (OpenAPI) documentation
- H2 in-memory database
- AOP execution time logging

---

## Tech Stack

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- Spring Validation
- Spring AOP
- H2 Database
- SpringDoc OpenAPI (Swagger)
- Maven

---

## Project Structure

```
src
 ├── controller
 ├──annotation
 ├── service
 ├── repository
 ├── entity
 ├── dto
 ├── exception
 ├── config
 ├── aspect
 └── TicketSupportApplication.java
```

---

## API Endpoints

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | /api/tickets | Create a new ticket |
| GET | /api/tickets | Get all tickets |
| GET | /api/tickets/{id} | Get ticket by id |
| PATCH | /api/tickets/{id}/update | Update ticket status |

---

## Ticket Status

- OPEN
- IN_PROGRESS
- CLOSED

---

## Running the Project

Clone the repository:

```bash
git clone https://github.com/baharkiani/TicketSupport.git
```

Run the application:

```bash
mvn spring-boot:run
```

---

## Swagger

After starting the application:

```
http://localhost:8082/swagger-ui/index.html
```

---

## H2 Database

H2 Console:

```
http://localhost:8082/h2-console
```
---

## Validation

The application validates incoming requests using Bean Validation.

Examples:

- Title cannot be blank
- Description cannot be blank
- Title must be between 2 and 100 characters
- Description must be between 2 and 1000 characters

---

## Exception Handling

Global exception handling is implemented using:

- `@ControllerAdvice`

Returns meaningful JSON error responses.

---

## AOP

A custom annotation is provided for execution time logging.

@LogExecutionTime

---
