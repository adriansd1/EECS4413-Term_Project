package org.eecs4413.eecs4413term_project.model;


public class User {
    final String name;
    private boolean isAuthenticated;

    public User(String name, boolean isAuthenticated) {
        this.name = name;
        this.isAuthenticated = isAuthenticated;
    }

    public String getName() {
        return name;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public boolean hasMadePurchase() {
        return isAuthenticated;
    }

    // Simulate successful sign-up/authentication
    public void authenticate() {
        this.isAuthenticated = true;
        System.out.println("✅ " + name + " has been authenticated successfully!");
    }
}