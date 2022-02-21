package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;

public class User {
    @BsonProperty(value = "username")
    private String username;
    @BsonProperty(value = "password")
    private String password;
    @BsonProperty(value = "is_admin")
    private int is_admin;
    @BsonProperty(value = "reviews")
    private List<Review> reviews;

    public User() {

    }

    public User(String username, String password, int is_admin) {
        this.username = username;
        this.password = password;
        this.is_admin = is_admin;
    }

    public User(String username, String password, List<Review> reviews, int is_admin){
        this.username = username;
        this.password = password;
        this.reviews = reviews;
        this.is_admin = is_admin;

    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIs_admin(){ return this.is_admin;}

    public void setIs_admin(int role) {
        this.is_admin = role;
    }

    public List<Review> getReviews(){return reviews;}

    public void setReviews(List<Review> reviews){this.reviews = reviews;}
}
