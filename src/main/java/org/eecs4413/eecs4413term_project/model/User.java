package org.eecs4413.eecs4413term_project.model;


public class User {
    final String name;
    private boolean isAuthenticated;
    private String address;

    public User(String name, boolean isAuthenticated, String address) {
        this.name = name;
        this.isAuthenticated = isAuthenticated;
        this.address = address;
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

    // Simulate successful sign-up/authentication
    public void authenticate() {
        this.isAuthenticated = true;
        System.out.println("✅ " + name + " has been authenticated successfully!");
    }
}