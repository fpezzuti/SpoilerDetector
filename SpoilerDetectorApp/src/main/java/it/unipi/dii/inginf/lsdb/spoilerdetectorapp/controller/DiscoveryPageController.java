package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller;

import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.config.ConfigParams;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Movie;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Session;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.User;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.utils.Utils;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;

public class DiscoveryPageController {
    @FXML private TextField searchTextField;
    @FXML private Button searchButton;
    @FXML private Label usernameLabel;
    @FXML private ImageView profilePicImageView;
    @FXML private VBox elementsVBox;
    @FXML private ImageView spoilerDetectorImageView;
    @FXML private Button logoutButton;
    @FXML private Button newAdminButton;
    @FXML private ChoiceBox searchChoiceΒox;

    private MongoDBDriver mongoDBDriver;
    private int currentI = 0;
    private int currentJ = 0;
    private GridPane gridPane;
    private int limit;

    private User loggedUser = Session.getLocalSession().getLoggedUser();

    public void initialize() {

        mongoDBDriver = MongoDBDriver.getInstance();
        limit = ConfigParams.getLocalConfig().getLimitNumber();

        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(15);
        gridPane.setVgap(15);

        fillInterfaceElements();

        initButtons();

        if (loggedUser.getIs_admin()==0) {
            initializeSuggestions();
        }
    }

    private void initButtons(){
        logoutButton.setOnMouseClicked(Utils::logout);
        spoilerDetectorImageView.setOnMouseClicked(clickEvent -> Utils.changeScene(Utils.DISCOVERY_PAGE, clickEvent));
        usernameLabel.setOnMouseClicked(this::myProfile);
        profilePicImageView.setOnMouseClicked(this::myProfile);
        searchButton.setOnMouseClicked(this::searchHandler);
        if (loggedUser.getIs_admin()==1) {
            newAdminButton.setText("Create new admin");
            newAdminButton.setStyle("-fx-background-color: red; -fx-background-radius: 13px;" + "-fx-text-fill: white");
            newAdminButton.setOnMouseClicked(clickEvent -> Utils.changeScene(Utils.REGISTRATION_PAGE, clickEvent));
            newAdminButton.setPrefSize(160, 33);
            searchChoiceΒox.setVisible(true);
            searchChoiceΒox.getItems().addAll("Movie", "User");
            searchChoiceΒox.setValue("User");
        }
        else
            newAdminButton.setVisible(false);
    }

    private void myProfile(MouseEvent clickEvent){
        if(loggedUser.getIs_admin()==0){
            ProfilePageController profilePageController = (ProfilePageController) Utils.changeScene(Utils.PROFILE_PAGE, clickEvent);
            profilePageController.setProfileUser(loggedUser);
        }
        else{
            Utils.changeScene(Utils.PERSONAL_PAGE, clickEvent);
        }

    }

    private void fillInterfaceElements() {

        usernameLabel.setText(loggedUser.getUsername());

        if (loggedUser.getIs_admin()==1) {
            profilePicImageView.setImage(new Image(String.valueOf(DiscoveryPageController.class.getResource("/img/createAdmin.png"))));
        }
    }

    private void initializeSuggestions(){
        Utils.addLine(elementsVBox, null, null, Utils.MOST_RECENT);
        Utils.addLine(elementsVBox, null, null, Utils.RANDOM_MOVIES);
    }

    private void searchHandler(MouseEvent clickEvent){
        currentI = 0;
        currentJ = 0;

        String searchedText = searchTextField.getText();

        elementsVBox.getChildren().clear();
        gridPane.getChildren().clear();

        if(loggedUser.getIs_admin()==1)
            if(searchChoiceΒox.getValue()=="User")
                addMoreResearchedUsers(searchedText);
            else
                addMoreResearchedMovies(searchedText);
        else{
            if(searchedText.equals(""))
                initializeSuggestions();
            else
                addMoreResearchedMovies(searchedText);
        }


        elementsVBox.getChildren().add(gridPane);
        searchTextField.setText("");
    }

    private void addMoreResearchedMovies(String title){
        List<Movie> searchedCourses = mongoDBDriver.findMoviesByTitle(title, ((currentI*4)+currentJ), limit);

        for(int i = currentI; i<currentI + 4; i++){
            int j;
            if(i == currentI)
                j = currentJ;
            else
                j = 0;
            for(; j<4; j++) {
                if(((i-currentI)*4+j) < searchedCourses.size() && searchedCourses.get((i-currentI)*4 + j) != null) {
                    Pane coursePane = Utils.loadMovieSnapshot(searchedCourses.get((i-currentI)*4 + j));
                    GridPane.setHalignment(coursePane, HPos.CENTER);
                    GridPane.setValignment(coursePane, VPos.CENTER);
                    gridPane.add(coursePane, j, i);
                }
                if((i == currentI + 3 && j == 3) || ((i-currentI)*4+j) >= searchedCourses.size()) {
                    ImageView more = new ImageView(new Image(String.valueOf(DiscoveryPageController.class.getResource(Utils.READ_MORE))));
                    more.setPreserveRatio(true);
                    more.setFitWidth(100);
                    more.setFitWidth(100);
                    more.setCursor(Cursor.HAND);
                    currentI = i;
                    currentJ = (j);
                    more.setOnMouseClicked(newClickEvent -> addMoreResearchedMovies(title));
                    more.setCursor(Cursor.HAND);
                    GridPane.setHalignment(more, HPos.CENTER);
                    GridPane.setValignment(more, VPos.CENTER);
                    gridPane.add(more, j, i);
                    return;
                }
            }
        }
    }

    private void addMoreResearchedUsers(String username){
        List<User> searchedUsers = mongoDBDriver.findUsersByUsername(username, ((currentI*4)+currentJ), limit);

        for(int i = currentI; i<currentI + 4; i++){
            int j;
            if(i == currentI)
                j = currentJ;
            else
                j = 0;
            for(; j<4; j++) {
                if(((i-currentI)*4+j) < searchedUsers.size() && searchedUsers.get((i-currentI)*4 + j) != null) {
                    Pane userPane = Utils.loadUserSnapshot(searchedUsers.get((i-currentI)*4 + j));
                    GridPane.setHalignment(userPane, HPos.CENTER);
                    GridPane.setValignment(userPane, VPos.CENTER);
                    gridPane.add(userPane, j, i);
                }
                if((i == currentI + 3 && j == 3) || ((i-currentI)*4+j) >= searchedUsers.size()) {
                    ImageView more = new ImageView(new Image(String.valueOf(DiscoveryPageController.class.getResource(Utils.READ_MORE))));
                    more.setPreserveRatio(true);
                    more.setFitWidth(100);
                    more.setFitWidth(100);
                    more.setCursor(Cursor.HAND);
                    currentI = i;
                    currentJ = (j);
                    more.setOnMouseClicked(newClickEvent -> addMoreResearchedUsers(username));
                    more.setCursor(Cursor.HAND);
                    GridPane.setHalignment(more, HPos.CENTER);
                    GridPane.setValignment(more, VPos.CENTER);
                    gridPane.add(more, j, i);
                    return;
                }
            }
        }
    }
}
