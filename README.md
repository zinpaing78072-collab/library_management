# Library Management System

A JavaFX desktop application for managing a library's book catalogue, built with
Maven, Java 21 and MySQL (MariaDB).

## Features

- Browse all books in a sortable table
- Filter the table by author, category and publisher
- Add, edit and clear book records
- Upload a cover image for each book
- Members and book-loan transactions stored in the database

## Architecture

The UI is split across three FXML components, each with its own controller.
They never talk to each other directly — they coordinate through `BookSession`,
a small shared-state class created in `Main` and injected into every controller.

| Layer | Package | Responsibility |
|---|---|---|
| Model | `com.example.model` | Plain data objects (`Book`, `Author`, `Member`, …) |
| Repository | `com.example.repository` | JDBC access; `impl` classes hold the SQL |
| Service | `com.example.service` | Validation and orchestration between UI and repositories |
| Controller | `com.example.controller` | JavaFX controllers (`BookController`, `BookTableController`, `BookFormController`, `BookSession`) |

The book list is an `ObservableList` owned by `BookSession`. Controllers mutate it
in place, and the `TableView` repaints itself — there is no manual refresh call.

## Requirements

- JDK 21
- Maven
- MySQL 8+ or MariaDB running on port 3306

## Database setup

The app expects a database named `LibraryManagementSystemDB` containing eight
tables: `Author`, `Publisher`, `Category`, `Book`, `BookCopies`, `Member`,
`Users` and `BookTransactions`.

`seed.sql` populates it with sample data. It is **idempotent** — every insert is
guarded with `WHERE NOT EXISTS`, and foreign keys are resolved by natural key
(author/publisher/category name, book title) rather than by hardcoded
auto-increment id, so it is safe to run more than once.

```bash
/Applications/XAMPP/xamppfiles/bin/mysql -u root < seed.sql
```

The connection settings are in `DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/LibraryManagementSystemDB";
private static final String USER = "root";
private static final String PASSWORD = "";
```

## Running

Make sure the database server is running first, then:

```bash
mvn clean javafx:run
```

> **Note:** if you use XAMPP, MySQL only starts when XAMPP is launched. Starting
> the app without a running database fails at startup — the table loads before
> the form, so the whole screen aborts with a connection error.

## Security

This is coursework, so a few shortcuts were taken deliberately and are **not**
suitable for production:

- `Users.Password` is stored and compared in plain text (`UserRepositoryImpl.login`
  runs `WHERE Name = ? AND Password = ?`).
- Database credentials are hardcoded in `DatabaseConnection.java`.
- Sample accounts are published in `seed.sql` (`admin` / `admin123`).
