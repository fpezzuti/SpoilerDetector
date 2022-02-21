package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class Review {
    @BsonId
    private ObjectId id;
    @BsonProperty(value = "review_date")
    private String date;
    @BsonProperty(value = "movie_id")
    private ObjectId movie_id;
    @BsonProperty(value = "username")
    private String username;
    @BsonProperty(value = "is_spoiler")
    private boolean spoiler;
    @BsonProperty(value ="review_text")
    private String review_text;
    @BsonProperty(value="rating")
    private int rating;
    @BsonProperty(value="title")
    private String title;

    public Review(String date, boolean spoiler, String review_text, int rating, String title) {
        this.date = date;
        this.spoiler = spoiler;
        if(review_text!=null)
            this.review_text = review_text;
        this.rating = rating;
        if(title!=null)
            this.title = title;
    }

    public Review(String date, ObjectId movie_id, String username, boolean spoiler, String review_text, int rating, String title) {
        this.date = date;
        this.movie_id = movie_id;
        this.username = username;
        this.spoiler = spoiler;
        if(review_text!=null)
            this.review_text = review_text;
        this.rating = rating;
        if(title!=null)
            this.title = title;
    }

    public Review(String date, ObjectId movie_id, boolean spoiler, String review_text, int rating, String title) {
        this.date = date;
        this.movie_id = movie_id;
        this.spoiler = spoiler;
        if(review_text!=null)
            this.review_text = review_text;
        this.rating = rating;
        if(title!=null)
            this.title = title;
    }

    public Review(ObjectId id, String date, ObjectId movie_id, String username, boolean spoiler, String review_text, int rating, String title) {
        this.id = id;
        this.date = date;
        this.movie_id = movie_id;
        this.username = username;
        this.spoiler = spoiler;
        this.review_text = review_text;
        this.rating = rating;
        this.title = title;
    }

    public Review() {
    }


    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ObjectId getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(ObjectId movie_id) {
        this.movie_id = movie_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getSpoiler() {
        return spoiler;
    }

    public void setSpoiler(boolean spoiler) {
        this.spoiler = spoiler;
    }

    public String getReview_text() {
        return review_text;
    }

    public void setReview_text(String review_text) {
        this.review_text = review_text;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
