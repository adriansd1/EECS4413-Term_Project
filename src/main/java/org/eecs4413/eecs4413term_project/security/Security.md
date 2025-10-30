# Security Folder

This folder contains **Security/configuration classes** for the Spring Boot application.

Configurations:
- Use the `@Configuration` annotation.
- Define Beans, CORS, security, or custom settings.
- Modify how Spring behaves globally.

**Example:**
```java
@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("*");
            }
        };
    }
}
