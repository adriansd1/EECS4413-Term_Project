# Model Folder

This folder contains **data model classes**, including:
- `@Entity` classes (database-mapped tables)
- DTOs (Data Transfer Objects)
- Plain data-holding objects (POJOs)

Models define the **structure of your data** in Java.

**Example Entity:**
```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
}
