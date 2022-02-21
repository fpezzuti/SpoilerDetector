package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller;

import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Movie;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Review;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Session;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.User;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ReviewSnapshotPageController {
    @FXML private ImageView profilePicImageView;
    @FXML private Label usernameLabel;
    @FXML private Label reviewContentLabel;
    @FXML private HBox ratingHBox;
    @FXML private HBox spoilerHBox;
    @FXML private Label lastModifiedLabel;
    @FXML private BorderPane thisBorderPane;
    @FXML private ImageView deleteImageView;

    private VBox container;

    private Movie movie;
    private Review review;

    public void initialize() {

    }

    public void deleteReview() {
        MongoDBDriver.getInstance().deleteReview(movie, review);
        container.getChildren().remove(thisBorderPane);
    }

    private void loadReviewInformation(boolean type){
        // true: review on profile page
        // false: review on movie page
        if(type){
            usernameLabel.setText(movie.getTitle());
            usernameLabel.setOnMouseClicked(clickEvent -> visitMoviePage(clickEvent, movie));
            profilePicImageView.setOnMouseClicked(clickEvent -> visitMoviePage(clickEvent, movie));
            if(movie.getCoverImage()!=null)
                profilePicImageView.setImage(new Image(movie.getCoverImage()));
            else
                profilePicImageView.setImage(new Image(String.valueOf(ReviewSnapshotPageController.class.getResource("/img/movieDefault.png"))));
        }
        else{
            User author = MongoDBDriver.getInstance().getUserByUsername(review.getUsername());
            usernameLabel.setText(review.getUsername());
            usernameLabel.setOnMouseClicked(clickEvent -> visitAuthorProfile(clickEvent, author));
            profilePicImageView.setOnMouseClicked(clickEvent -> visitAuthorProfile(clickEvent, author));
        }

        Utils.fillStars(review.getRating(), ratingHBox);

       if(!review.getSpoiler()) {
           spoilerHBox.setVisible(false);
           if(review.getReview_text() != null)
               reviewContentLabel.setText(review.getReview_text());
           else
               reviewContentLabel.setText("");
       }
       else{
           spoilerHBox.setOnMouseClicked(this::showSpoiler);
           reviewContentLabel.setText("SPOILER");
       }

        lastModifiedLabel.setText("Last-modified: "+ review.getDate());
    }

    public void setReview(Review review, VBox container, boolean type){
        User loggedUser = Session.getLocalSession().getLoggedUser();
        this.review = review;
        this.container = container;
        loadReviewInformation(type);

        if (loggedUser.getIs_admin()==1 || loggedUser.getUsername().equals(review.getUsername())) {
            deleteImageView.setVisible(true);
            deleteImageView.setOnMouseClicked(clickEvent -> deleteReview());
            deleteImageView.setCursor(Cursor.HAND);
        }
        else
            deleteImageView.setVisible(false);
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void visitAuthorProfile(MouseEvent mouseEvent, User author){
        ProfilePageController profilePageController = (ProfilePageController) Utils.changeScene(Utils.PROFILE_PAGE, mouseEvent);
        profilePageController.setProfileUser(author);
    }

    public void visitMoviePage(MouseEvent mouseEvent, Movie movie){
        MoviePageController moviePageController = (MoviePageController) Utils.changeScene(Utils.MOVIE_PAGE, mouseEvent);
        moviePageController.setMovie(movie);
    }

    public void showSpoiler(MouseEvent clickEvent){
        Utils.showSpoilerAlert(spoilerHBox, reviewContentLabel, review.getReview_text());
    }


}
