
package com.escuelagaing.edu.co.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "User")
public class User {

    @Id
    private String id;
    private String email; // Correo electrónico del usuario
    private String name; 


    
    // Constructor
    public User() {
    }


    public User(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
  
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

   

}
