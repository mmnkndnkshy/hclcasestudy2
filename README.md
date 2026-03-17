# Fulfilment Code Assignment

## Overview

This project implements a fulfilment system covering **Location**, **Store**, and **Warehouse** modules with required business validations and transactional consistency.

The solution focuses on clean architecture principles, separation of concerns, and maintainable code design.

## Features

### Location

* Resolve location using identifier
* In-memory location data source
* Validation for supported locations

### Store

* CRUD operations for Store
* Transactional handling using Quarkus
* Integration with legacy system via `LegacyStoreManagerGateway`
* Proper error handling and response mapping

### Warehouse

* Create new warehouse
* Retrieve warehouse by business unit code
* Archive warehouse
* Replace active warehouse

## Business Rules Implemented

* Location must exist before warehouse creation
* Maximum warehouses per location enforced
* Total capacity must not exceed location limits
* Stock must not exceed capacity
* Only one active warehouse per business unit
* Archived warehouses cannot be modified
  
## Architecture

The project follows a **Ports and Adapters (Hexagonal Architecture)** approach:

* **Domain Layer** → Business models & use cases
* **Ports** → Interfaces defining operations
* **Adapters** → REST API & Database implementations

This ensures:

* Better testability
* Clear separation of concerns
* Flexibility to change infrastructure

## Tech Stack

* Java 17
* Quarkus
* Hibernate ORM with Panache
* REST APIs (JAX-RS)
* OpenAPI (for Warehouse APIs)
* Maven

## How to Run

```bash
mvn clean install
mvn quarkus: dev
```

Application will start at:

```
http://localhost:8080
```

## Testing

Run tests using:

```bash
mvn test
```
Includes:

* Unit tests for domain logic
* Integration tests for API endpoints

## Project Structure

```
src/main/java/com/fulfilment/application/monolith/
  ├── location
  ├── stores
  ├── warehouses
      ├── adapters
      ├── domain
      ├── ports
      ├── usecases
```

## Notes

* Warehouse APIs are implemented using **OpenAPI-generated interfaces**
* Focus was given to **business logic correctness and clean design**
* Some parts use **in-memory/static data** for simplicity

## Author

Manikandan Muthuchamy
