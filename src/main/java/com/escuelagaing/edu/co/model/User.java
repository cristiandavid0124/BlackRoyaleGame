
package com.escuelagaing.edu.co.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "User")
public class User {

    @Id
    private String email;

    @Field(name= "name")
    private String name; 

    @Field(name = "nickName")
    private String nickName; 


    
    // Constructor
    public User() {
    }


    public User(String email, String name, String nickName) {
        this.email = email;
        this.name = name;
        this.nickName = nickName;
    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


}
