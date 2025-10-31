# Security Configuration

## Important: Do NOT commit sensitive credentials to Git!

This project uses environment variables for sensitive configuration.

### Setup Instructions:

1. Copy .env.example to .env:
   ```
   cp .env.example .env
   ```

2. Edit .env and add your actual values:
   - DB_PASSWORD: Your PostgreSQL password
   - JWT_SECRET: A secure random string (at least 32 characters)

3. The .env file is in .gitignore and will NOT be committed.

### Running the Application:

Make sure to set environment variables before running:

**On Windows (PowerShell):**
```powershell
$env:DB_PASSWORD='your_password'; $env:JWT_SECRET='your_secret'; mvn spring-boot:run
```

**On Linux/Mac:**
```bash
export DB_PASSWORD='your_password'
export JWT_SECRET='your_secret'
mvn spring-boot:run
```
