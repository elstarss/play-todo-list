# Todo API — First Steps with Play & Scala
This project is a simple **Todo List REST API** built using the **Play Framework**   
It was created as part of learning the basics of backend development in Scala and exploring how Play handles routing, controllers, JSON, and testing.
Through making the app I have covered core patterns used in API development: handling requests, validating JSON, responding with appropriate HTTP status codes, and writing meaningful tests.

---

## Features

- **GET /todo** – Retrieve the full list of todo items
- **GET /todo/:id** – Retrieve a single item by ID
- **POST /todo** – Add a new todo item
    - Supports optional `completed` field
- **PATCH /todo/:id** – Update fields on an existing item
- **DELETE /todo** – Delete all items
    - Idempotent: returns `204 NoContent` even if list is already empty

The app stores todo items using an in-memory `ListBuffer` for simplicity.

---

## Running the Project
Make sure you have sbt installed, then run:
sbt run

The server will start at:
http://localhost:9000

 ## Testing
Play provides a test toolkit that lets you simulate requests through your real routes.
Run all tests:
sbt test

# Tests cover:
- GET, POST, PATCH, DELETE endpoints
- JSON validation
- Correct HTTP status codes
- Edge cases (not found items, empty list, invalid JSON)

---

## Project Goals

The aim of this project were to learn and practice:
- How Play handles routing, controllers, and parsing requests
- How to work with Play JSON and case-class validation
- How to build RESTful endpoints in Scala
- How to use Option, pattern matching, and functional idioms in real code
- How to include testing in the play framework 
