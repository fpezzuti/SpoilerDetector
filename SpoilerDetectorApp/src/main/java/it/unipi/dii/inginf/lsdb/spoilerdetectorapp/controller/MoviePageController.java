package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller;

import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.classifier.RequestClassifier;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.config.ConfigParams;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Movie;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Review;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Session;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.User;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MoviePageController {

    @FXML private ImageView spoilerDetectorImageView;
    @FXML private VBox allContentVBox;
    @FXML private Label titleLabel;
    @FXML private ImageView movieImageImageView;
    @FXML private TextArea summaryTextArea;
    @FXML private Label durationLabel;
    @FXML private Label genreLabel;
    @FXML private Label releaseDateLabel;
    @FXML private VBox newReviewVBox;
    @FXML private HBox ratingHBox;
    @FXML private ImageView deleteImageView;
    @FXML private TextArea commentTextArea;
    @FXML private Label lastModifiedLabel;
    @FXML private Button saveReviewButton;
    @FXML private ImageView moreReviewsImageView;
    @FXML private VBox reviewsVBox;
    @FXML private ImageView deleteMovieImageView;

    private Movie movie;
    private Review myReview;

    private int pageNumber = 0;
    private int limit;

    private MongoDBDriver mongoDBDriver;

    public void initialize() {
        limit = ConfigParams.getLocalConfig().getLimitNumber();
        mongoDBDriver = MongoDBDriver.getInstance();

        spoilerDetectorImageView.setOnMouseClicked(clickEvent -> backToHome(clickEvent));
        deleteImageView.setCursor(Cursor.HAND);
    }

    public void backToHome(MouseEvent clickEvent){
        Utils.changeScene(Utils.DISCOVERY_PAGE, clickEvent);
    }

    public void setMovie(Movie movie) {
        User loggedUser = Session.getLocalSession().getLoggedUser();
        this.movie = mongoDBDriver.getMovieByTitle(movie.getTitle());
        moreReviewsImageView.setOnMouseClicked(clickEvent -> loadMore());

        loadMovieInformation();

        if (loggedUser.getIs_admin()==1) {
            deleteMovieImageView.setVisible(true);
            deleteMovieImageView.setOnMouseClicked(clickEvent -> deleteMovie(clickEvent));
            allContentVBox.getChildren().remove(newReviewVBox);
        }
        else{ // standard user

            myReview = MongoDBDriver.getInstance().getMovieReviewByUser(movie, loggedUser);
            if(myReview!=null)
                loadMyReview();
            else
                loadNewReview();
        }

        loadMore();
    }

    public void deleteMovie(MouseEvent clickEvent){
        MongoDBDriver.getInstance().deleteMovie(movie);
        Utils.changeScene(Utils.DISCOVERY_PAGE, clickEvent);
    }

    public void loadMyReview(){
        newReviewVBox.setVisible(true);
        commentTextArea.setEditable(false);
        saveReviewButton.setText("Edit");
        saveReviewButton.setOnMouseClicked(clickEvent -> saveReviewButtonHandler());
        saveReviewButton.setCursor(Cursor.HAND);

        deleteImageView.setOnMouseClicked(clickEvent -> deleteButtonHandler());
        commentTextArea.setText(myReview.getReview_text());
        lastModifiedLabel.setText("Last-modified: "+ myReview.getDate());
        Utils.fillStars(myReview.getRating(), ratingHBox);
        handleRatingStars(false);
    }

    public void loadNewReview(){
        saveReviewButton.setText("Save");
        saveReviewButton.setOnMouseClicked(clickEvent2 -> saveReviewButtonHandler());
        saveReviewButton.setCursor(Cursor.HAND);
        commentTextArea.setEditable(true);
        handleRatingStars(true);
        deleteImageView.setVisible(false);
    }

    public void deleteButtonHandler(){
        if(MongoDBDriver.getInstance().deleteReview(movie, myReview)){
            saveReviewButton.setText("Save");
            myReview = null;
            commentTextArea.setEditable(true);
            commentTextArea.setText("");
            Utils.fillStars(1, ratingHBox);
            handleRatingStars(true);
            deleteImageView.setVisible(false);
        }
    }

    private void loadMore(){
        int skip = pageNumber*limit;
        pageNumber++;

        int toIndex = skip + limit;
        if(movie.getReviews() == null)
            return;
        if (toIndex >= movie.getReviews().size()) {
            toIndex = movie.getReviews().size();
        }

        if (skip >= toIndex)
            return;

        List<Review> toAdd = movie.getReviews().subList(skip, toIndex);
        if(myReview != null) {
            int i;
            boolean found = false;
            for (i = 0; i < toAdd.size(); i++) {
                if (toAdd.get(i).getUsername().equals(Session.getLocalSession().getLoggedUser())) {
                    found = true;
                    break;
                }
            }
            if(found)
                toAdd.remove(i);
        }
        if(toAdd.size() != 0)
            createReviewsElements(toAdd, reviewsVBox);
    }


    /**
     * Creates the GUI elements for the reviews
     * @param reviewsList the list of reviews to be shown
     * @param container the container of the list of reviews
     */

    private void createReviewsElements(List<Review> reviewsList, VBox container){
        BorderPane reviewBorderPane;
        for(Review r: reviewsList){
            reviewBorderPane = loadSingleReview(r);
            container.getChildren().add(reviewBorderPane);
        }
    }

    private BorderPane loadSingleReview(Review review) {
        BorderPane borderPane = null;

        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource(Utils.REVIEW_SNAPSHOT));
            borderPane = loader.load();
            ReviewSnapshotPageController reviewController = loader.getController();
            reviewController.setReview(review, reviewsVBox, false);
            reviewController.setMovie(movie);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return borderPane;
    }

    public void saveReviewButtonHandler(){
        if(saveReviewButton.getText().equals("Save")) { // save operation
            int rating = getRatingFromStars();
            String author = Session.getLocalSession().getLoggedUser().getUsername();

            Date currentTimestamp = new Date();
            DateFormat formatter = new SimpleDateFormat("dd MMMMM yyyy", Locale.ENGLISH);
            String today = formatter.format(currentTimestamp);

            String comment = commentTextArea.getText().equals("") ? null : commentTextArea.getText();

            boolean spoiler;
            try {
                 spoiler = RequestClassifier.sendRequest(comment);
                 System.out.println("Check if review is spoiler: "+spoiler);
            }
            catch (Exception e){
                System.out.println("Error in receiving the response from classifier");
                spoiler = true;
            }

            if(myReview==null) { // add review
                myReview = new Review(today, movie.getId(), author, spoiler, comment, rating, movie.getTitle());

                if (MongoDBDriver.getInstance().addReview(movie, myReview)) {
                    Utils.showInfoAlert("Added new review with success!");

                } else {
                    Utils.showErrorAlert("Error in adding the review");
                }
                deleteImageView.setVisible(true);
            }
            else{ // edit review

                myReview.setReview_text(comment);
                myReview.setRating(rating);
                myReview.setDate(today);

                if(mongoDBDriver.editReview(movie, myReview)){
                    Utils.showInfoAlert("Review edited with success!");
                }
                else{
                    Utils.showErrorAlert("Error in editing the review");
                }
            }
            lastModifiedLabel.setText("Last-modified: " + today);
            commentTextArea.setEditable(false);
            deleteImageView.setOnMouseClicked(clickEvent -> deleteButtonHandler());
            handleRatingStars(false);

            saveReviewButton.setText("Edit");

        }
        else{ // click on edit button -> edit the review should be permitted
            commentTextArea.setEditable(true);

            handleRatingStars(true);

            saveReviewButton.setText("Save");
        }
    }

    private int getRatingFromStars(){
        int rating = 0;
        Image starOn = new Image(String.valueOf(MoviePageController.class.getResource(Utils.STAR_ON)));

        for(Node star: ratingHBox.getChildren()){
            ImageView starImageView = (ImageView)star;
            if(starImageView.getImage().getUrl().equals(starOn.getUrl()))
                rating++;
        }
        return rating;
    }

    private void handleRatingStars(boolean type){
        // true -> activate
        // false -> disabled
        if(type){ // activate
            for(Node star: ratingHBox.getChildren()) {
                int index = ratingHBox.getChildren().indexOf(star);
                star.setOnMouseClicked(mouseEvent -> starOnMouseClickedHandler(index));
                star.setOnMouseEntered(mouseEvent -> starOnMouseOverHandler(star));
                star.setOnMouseExited(mouseEvent -> starOnMouseExitedHandler());
                star.setCursor(Cursor.HAND);
            }
        }
        else{ // disabled
            for(Node star: ratingHBox.getChildren()) {
                star.setOnMouseEntered(mouseEvent -> {});
                star.setOnMouseExited(mouseEvent -> {});
                star.setOnMouseClicked(mouseEvent -> {});
            }
        }
    }

    private void starOnMouseOverHandler(Node star){

        int starIndex = ratingHBox.getChildren().indexOf(star);
        Utils.fillStars(starIndex+1, ratingHBox);
    }

    private void starOnMouseExitedHandler(){
        Utils.fillStars(1, ratingHBox);
    }

    private void starOnMouseClickedHandler(int index){
        Utils.fillStars(index + 1, ratingHBox);
        for(Node star: ratingHBox.getChildren()) {
            int s = ratingHBox.getChildren().indexOf(star);
            if(s<=index){
                star.setOnMouseExited(mouseEvent -> {});
                star.setOnMouseEntered(mouseEvent -> {});
            }
            else{
                star.setOnMouseExited(mouseEvent -> Utils.fillStars(index+1, ratingHBox));
                star.setOnMouseEntered(mouseEvent -> starOnMouseOverHandler(star));
            }

        }
    }

    private void loadMovieInformation(){
        titleLabel.setText(movie.getTitle());
        durationLabel.setText(movie.getDuration());
        if(movie.getGenre().size()>0){
            StringBuilder genres = new StringBuilder();
            for(String genre: movie.getGenre()){
                genres.append(genre);
                if(movie.getGenre().indexOf(genre) < movie.getGenre().size()-1) // there is another genre after the current
                    genres.append(", ");
            }
            genreLabel.setText(genres.toString());
        }

        if(movie.getCoverImage()!=null)
            movieImageImageView.setImage(new Image(movie.getCoverImage()));
        else
            movieImageImageView.setImage(new Image(String.valueOf(ReviewSnapshotPageController.class.getResource("/img/movieDefault.png"))));

        releaseDateLabel.setText(movie.getRelease_date());
        summaryTextArea.setText(movie.getSummary());
    }

}
