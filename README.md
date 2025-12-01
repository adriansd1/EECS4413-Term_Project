# EECS4413-Term_Project

Short description
This project serves as an auction website, allowing users to bid and sell an item with ease. It also has a chatbot feature that allows users to perform these functionality simply through a prompt.

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

### Purchase & Receipts (UC5 - Purchases, UC6 - Receipts)

### 1. Making a valid purchase (Make <USER_ID> the id of a successfully authenticated user)
```bash
curl -i -X POST "$BASE_URL/api/purchases/makePurchase" \
  -H "Content-Type: application/json" \
  -d '{
    "item": "Notebook",
    "amount": 2,
    "price": 9.99,
    "cardNumber": "4111111111111111",
    "cardExpiry": "12/25",
    "cardCvv": "123",
    "userId": "<USER_ID>"
  }'
```

**Response (200 OK):**
```message
Purchase successful: Purchase{purchaseId=dac57272-e6ba-4638-a111-c8d696387b7b, item='Notebook', amount=2, userName='VicyC', price=9.99, shippingAddress='1000 Winners Avenue', cardTail=1111, purchasedAt=2025-11-02T15:55:43.075996}% 
```

### 2. Making a valid receipt for a valid purchase (Make <OWNER_ID> the id of a successfully authenticated user and <PURCHASE_ID> the id of a valid purchase)
```bash
curl -i -X POST "$BASE_URL/api/receipts/createReceipt" \
  -H "Content-Type: application/json" \
  -d '{
    "purchaseId": "<PURCHASE_UUID>",
    "owner_id": "<OWNER_ID>",
    "shippingDays": 5
  }'

```

**Mock values of owner_id and purchaseId**
```bash
owner_id = 4
purchaseId = dac57272-e6ba-4638-a111-c8d696387b7b
```

**Response (200 OK)**
```
Receipt created: Receipt{receiptId=d468cca6-ac33-4774-9a47-3cc3012310f0, purchaseId=dac57272-e6ba-4638-a111-c8d696387b7b, winnerName='Victor Chester', ownerName='Arthur Smith', auctionItem='Notebook', finalPrice=19.98, shippingDays=5}%  
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
