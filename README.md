# EECS4413-Term_Project

Short description
A brief one-line description of what this project does. Replace this with a short summary specific to your term project.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Quickstart — Clone & Build](#quickstart--clone--build)
- [Build (Gradle)](#build-gradle)
- [Run](#run)
- [Run in IDE (IntelliJ / Eclipse)](#run-in-ide-intellij--eclipse)
- [Configuration](#configuration)
- [Contact](#contact)

---

## Prerequisites

- Java JDK 21 (LTS recommended). Ensure JAVA_HOME is set to the JDK installation.
  - macOS / Linux example:
    ```bash
    export JAVA_HOME=$(/usr/libexec/java_home -v 21)  # macOS (adjust version as needed)
    ```
  - Windows: set JAVA_HOME in Environment Variables to e.g. C:\Program Files\Java\jdk-21

- Gradle wrapper (the repo includes a Gradle wrapper: ./gradlew or gradlew.bat). You do not need to install Gradle globally — use the wrapper.

- Git (to clone the repository)

## Quickstart — Clone & Build

Clone the repository and enter the project directory:

```bash
git clone https://github.com/adriansd1/EECS4413-Term_Project.git
cd EECS4413-Term_Project
```

Use the Gradle wrapper included in the repo to run common tasks. On macOS / Linux / WSL:

```bash
./gradlew clean build
```

On Windows (PowerShell / cmd):

```powershell
gradlew.bat clean build
```

This will compile the code, run tests, and produce artifacts in `build/`.

---

## Build (Gradle)

Recommended: always use the project Gradle wrapper (./gradlew) to ensure consistent Gradle version.

Common commands:

- Compile and build the project:

```bash
./gradlew build
```

- Run a specific task (replace `:module:task` as needed):

```bash
./gradlew :app:run
```

If your project produces a runnable JAR (e.g., `application` or Spring Boot), it will appear in `build/libs/`.

---

## Run

Since project uses Spring Boot:

```bash
./gradlew bootRun
```
---

## Run in IDE (IntelliJ / Eclipse)

- Import the project as a Gradle project.
- IntelliJ: File -> Open -> select repository root. IntelliJ will import Gradle settings automatically.
- Make sure Project SDK is set to the correct JDK (21).
- Use the IDE run configurations to run the main class or Gradle tasks like `run` / `bootRun`.

---

## API Endpoints

### Authentication (UC1.1 - Sign Up, UC1.2 - Sign In)

**Base URL:** `http://localhost:8080`

#### 1. Sign Up (Register New User)
```bash
POST /api/auth/signup
```

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "Password123",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "shippingAddress": "123 Main St, Toronto, ON M1A 1A1"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "username": "johndoe",
  "message": "User registered successfully"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "Password123",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "shippingAddress": "123 Main St, Toronto, ON M1A 1A1"
  }'
```

#### 2. Sign In (Login)
```bash
POST /api/auth/signin
```

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "Password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "username": "johndoe",
  "message": "Login successful"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "Password123"
  }'
```

---

## Tests

Run tests using curl commands in terminal:

## Configuration

### Required Environment Variables

The application requires the following environment variables to be set:

```bash
# Database Configuration
DB_PASSWORD=your_postgresql_password

# JWT Security
JWT_SECRET=your_jwt_secret_key_minimum_64_characters_long_for_HS256_algorithm

# Example (Development only - DO NOT use in production):
DB_PASSWORD=yourpassword
JWT_SECRET=93drzAGWtTHIsN8xxPjDxZcZLztdMuZxnJN9lhkshbyajUxZRvmvFgKni0LfeT8Y
```

### Setting Environment Variables

**Windows (PowerShell):**
```powershell
$env:DB_PASSWORD='yourpassword'
$env:JWT_SECRET='93drzAGWtTHIsN8xxPjDxZcZLztdMuZxnJN9lhkshbyajUxZRvmvFgKni0LfeT8Y'
./gradlew bootRun
```

**macOS / Linux:**
```bash
export DB_PASSWORD='yourpassword'
export JWT_SECRET='93drzAGWtTHIsN8xxPjDxZcZLztdMuZxnJN9lhkshbyajUxZRvmvFgKni0LfeT8Y'
./gradlew bootRun
```

### Database Setup

1. Install PostgreSQL and create a database:
```sql
CREATE DATABASE auction404;
```

2. Run the database schema script:
```bash
psql -U postgres -d auction404 -f database-schema.sql
```

3. The schema includes sample test users:
   - Username: `testuser`, Password: `password123`
   - Username: `seller1`, Password: `password123`
   - Username: `bidder1`, Password: `password123`

---

## Troubleshooting

- Build fails with "Java version" errors: confirm JAVA_HOME points to a compatible JDK (21) and the Gradle JVM matches.
- Gradle daemon issues: try `./gradlew --stop` then rebuild.
- Dependency resolution issues: run `./gradlew --refresh-dependencies`.
- Tests failing: run the failing tests locally with IntelliJ or `./gradlew test --tests 'com.example.YourTest'`.


Replace with the actual license used by the project.

---

## Contact

Project maintainer: Adrian
Email: adriansd@my.yorku.ca

Replace with real contact information.
