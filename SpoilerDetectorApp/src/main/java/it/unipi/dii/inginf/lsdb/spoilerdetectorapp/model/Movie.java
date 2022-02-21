package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.List;

public class Movie {
    @BsonId
    private ObjectId id;
    @BsonProperty(value = "title")
    private String title;
    @BsonProperty(value = "summary")
    private String summary;
    @BsonProperty(value = "duration")
    private String duration;
    @BsonProperty(value = "genre")
    private List<String> genre;
    @BsonProperty(value = "release_date")
    private String release_date;
    @BsonProperty(value = "synopsis")
    private String synopsis;
    @BsonProperty(value="cover_url")
    private String coverImage;
    private List<Review> reviews;
    public Movie() {
    }

    public Movie(ObjectId id, String title, String summary, String duration, List<String> genre, String release_date, String synopsis, String coverImage, List<Review> reviews) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.duration = duration;
        this.genre = genre;
        this.release_date = release_date;
        this.synopsis = synopsis;
        this.coverImage = coverImage;
        this.reviews = reviews;
    }

    public Movie(ObjectId id, String title, String summary, String duration, List<String> genre, String release_date, String synopsis, String coverImage){
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.duration = duration;
        this.genre = genre;
        this.release_date = release_date;
        this.synopsis = synopsis;
        this.coverImage = coverImage;
    }



    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getCoverImage(){return coverImage;}

    public void setCoverImage(String coverImage){this.coverImage = coverImage;}

    public List<Review> getReviews(){return reviews;}

    public void setReviews(List<Review> reviews){this.reviews = reviews;}

}
