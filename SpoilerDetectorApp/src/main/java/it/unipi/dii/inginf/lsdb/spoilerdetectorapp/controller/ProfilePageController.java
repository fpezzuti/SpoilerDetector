package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller;

import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.config.ConfigParams;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Movie;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Review;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Session;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.User;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;

public class ProfilePageController {

    @FXML private VBox elementsVBox;
    @FXML private ImageView learnItLabel;
    @FXML private VBox userInfoVBox;
    @FXML private Label usernameLabel;
    @FXML private BorderPane profileContentBorderPane;
    @FXML private BorderPane userBorderPane;
    @FXML private AnchorPane pageAnchorPane;
    @FXML private ImageView deleteUserImageView;
    @FXML private Button editButton;
    @FXML private VBox reviewsVBox;

    private User profileUser;
    private int limit;
    private int pageNumber = 0;

    public void initialize() {
        limit = ConfigParams.getLocalConfig().getLimitNumber();
        learnItLabel.setOnMouseClicked(clickEvent -> Utils.changeScene(Utils.DISCOVERY_PAGE, clickEvent));
    }

    private void loadProfileInformation(){
        usernameLabel.setText(profileUser.getUsername());
        profileUser.setReviews(MongoDBDriver.getInstance().getAllUserReviews(profileUser, 0, limit));
    }

    private void loadMore(){
        int skip = pageNumber*limit;
        pageNumber++;

        int toIndex = skip + limit;
        if(profileUser.getReviews() == null)
            return;
        if (toIndex >= profileUser.getReviews().size()) {
            toIndex = profileUser.getReviews().size();
        }

        if (skip >= toIndex)
            return;

        List<Review> toAdd = profileUser.getReviews().subList(skip, toIndex);

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
        Movie movie = MongoDBDriver.getInstance().getMovieByTitle(review.getTitle());

        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource(Utils.REVIEW_SNAPSHOT));
            borderPane = loader.load();
            ReviewSnapshotPageController reviewController = loader.getController();
            review.setUsername(profileUser.getUsername());
            reviewController.setMovie(movie);
            reviewController.setReview(review, reviewsVBox, true);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return borderPane;
    }

    public void setProfileUser(User user){
        User loggedUser = Session.getLocalSession().getLoggedUser();
        profileUser = MongoDBDriver.getInstance().getUserByUsername(user.getUsername());
        boolean isProfileMine = loggedUser.getUsername().equals(profileUser.getUsername());

        if(isProfileMine){ // personal profile
            editButton.setOnMouseClicked(clickEvent -> Utils.changeScene(Utils.PERSONAL_PAGE, clickEvent));
        }
        else{ // another profile
            editButton.setVisible(false);
            if(loggedUser.getIs_admin()==1) {
                deleteUserImageView.setVisible(true);
                deleteUserImageView.setOnMouseClicked(clickEvent -> deleteUser(clickEvent));
            }
        }

        loadProfileInformation();
        loadMore();
    }

    public void deleteUser(MouseEvent clickEvent){
        MongoDBDriver.getInstance().deleteUser(profileUser);
        Utils.changeScene(Utils.DISCOVERY_PAGE, clickEvent);
    }

    public void modifyAdminPasswordHandler(PasswordField oldPasswordField, TextField newPasswordTextField,
                                           TextField repeatPasswordTextField){

        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordTextField.getText();
        String repeatPassword = repeatPasswordTextField.getText();

        //if(! QUERY MONGO PER VERIFICA OLD PASSWORD CORRETTA)
            //Utils.showErrorAlert("Old password is wrong!!");

        if(newPassword.equals(repeatPassword))
            //QUERY DI MODIFICA PASSWORD UTENTE
            Utils.showInfoAlert("Password modified!");

        oldPasswordField.setText("");
        newPasswordTextField.setText("");
        repeatPasswordTextField.setText("");
    }

    public void deleteUserHandler(User profileUser, MouseEvent clickEvent) {
       if (MongoDBDriver.getInstance().deleteUser(profileUser)) {
            Utils.showInfoAlert("User deleted successfully");
            Utils.changeScene(Utils.DISCOVERY_PAGE, clickEvent);
        }
        else
            Utils.showErrorAlert("Something has gone wrong in deleting user");
    }

}
