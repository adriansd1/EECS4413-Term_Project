# Service Folder

This folder contains **service classes**, which hold your application's **business logic**.

Services:
- Use the `@Service` annotation.
- Receive requests from controllers.
- Communicate with repositories.
- Implement your app’s main logic (calculations, rules, transformations, etc).

**Example:**
```java
@Service
public class UserService {
    private final UserRepository repo;
    public UserService(UserRepository repo) { this.repo = repo; }

    public List<User> getAllUsers() {
        return repo.findAll();
    }
}
