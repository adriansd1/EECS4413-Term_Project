# Exception Folder

This folder contains **custom exceptions** and **global exception handlers**.

Use this folder to define meaningful, reusable error handling logic.

**Example Custom Exception:**
```java
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String msg) {
        super(msg);
    }
}
