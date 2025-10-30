# Controller Folder

This folder contains **REST controllers**, which handle incoming HTTP requests from the frontend (e.g. React) and return responses.

Controllers:
- Use `@RestController` and `@RequestMapping` annotations.
- Should contain **no business logic** — only request handling.
- Call the **service layer** to perform operations.
- Return JSON (or other formats) to the frontend.

**Example:**
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    public UserController(UserService service) { this.service = service; }

    @GetMapping
    public List<User> getAll() {
        return service.getAllUsers();
    }
}
