package org.eecs4413.eecs4413term_project.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true) 
    private String address; 
    
    private boolean isAuthenticated;
    public User() {
    }

    public User(String name, String userName, String password) {
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.isAuthenticated = false;
    }

    public void authenticate() {
        this.isAuthenticated = true;
        System.out.println("✅ " + name + " has been authenticated successfully!");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public boolean hasMadePurchase() {
        return isAuthenticated;
    }

    public boolean hasReceivedReceipt() {
        return isAuthenticated;
    }

    // Simulate successful sign-up/authentication
    public void authenticate() {
        this.isAuthenticated = true;
        System.out.println("✅ " + name + " has been authenticated successfully!");
    }
}