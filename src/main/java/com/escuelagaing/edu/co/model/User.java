
package com.escuelagaing.edu.co.model;

public class User {

    private String id; // ID del usuario en Microsoft Entra
    private String email; // Correo electrónico del usuario
    private String name; 
    private String displayName; 
    private String givenName; 
    private String familyName; 

    
    // Constructor
    public User() {
    }


    public User(String id, String email, String name, String displayName, String givenName, String familyName) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.displayName = displayName;
        this.givenName = givenName;
        this.familyName = familyName;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }


}
