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

If your project uses Spring Boot:

```bash
./gradlew bootRun
# or
java -jar build/libs/<project>-0.1.0.jar
```
---

## Run in IDE (IntelliJ / Eclipse)

- Import the project as a Gradle project.
- IntelliJ: File -> Open -> select repository root. IntelliJ will import Gradle settings automatically.
- Make sure Project SDK is set to the correct JDK (21).
- Use the IDE run configurations to run the main class or Gradle tasks like `run` / `bootRun`.

---

## Tests

Run tests using curl commands in terminal:

## Configuration

If the application uses environment variables, create a `.env` file in the project root (do NOT commit secrets). Example:

```
# .env
PORT=8080
DATABASE_URL=jdbc:postgresql://localhost:5432/mydb
DATABASE_USER=myuser
DATABASE_PASS=mypassword
```

Document any required environment variables here and in your project code.

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
