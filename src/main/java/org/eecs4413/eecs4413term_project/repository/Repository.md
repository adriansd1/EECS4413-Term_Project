# Repository Folder

This folder contains **data access logic** — usually interfaces that talk to your database.

Repositories:
- Use the `@Repository` annotation (optional for JPA interfaces).
- Extend Spring Data interfaces like `JpaRepository` or `CrudRepository`.
- Should NOT contain business logic.

**Example:**
```java
public interface UserRepository extends JpaRepository<User, Long> {
    // You can add custom query methods if needed
}
