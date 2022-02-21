package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller;

import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Session;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.User;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class PersonalPageController {

    @FXML private Label usernameLabel;
    @FXML private Button saveButton;
    @FXML private PasswordField passwordPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ImageView spoilerdetectorImageView;

    private User loggedUser;

    public void initialize() {
        loggedUser = Session.getLocalSession().getLoggedUser();
        spoilerdetectorImageView.setOnMouseClicked(this::backToHomeButtonHandler);
        spoilerdetectorImageView.setCursor(Cursor.HAND);
        saveButton.setOnMouseClicked(this::saveButtonHandler);
        saveButton.setCursor(Cursor.HAND);

        usernameLabel.setText(loggedUser.getUsername());

    }

    public void backToHomeButtonHandler(MouseEvent clickEvent) {
        if(loggedUser.getIs_admin()==0) {
            ProfilePageController profilePageController = (ProfilePageController) Utils.changeScene(Utils.PROFILE_PAGE, clickEvent);
            profilePageController.setProfileUser(Session.getLocalSession().getLoggedUser());
        }
        else
            Utils.changeScene(Utils.DISCOVERY_PAGE, clickEvent);
    }

    public void saveButtonHandler(MouseEvent clickEvent) {
        boolean ret;

        if (!validatePassword())
            return;

        loggedUser.setPassword(passwordPasswordField.getText());

        ret = MongoDBDriver.getInstance().editProfileInfo(loggedUser);

        if (ret) {
            Utils.showInfoAlert("Your password was updated with success!");

            Utils.changeScene(Utils.DISCOVERY_PAGE, clickEvent);
        } else {
            Utils.showInfoAlert("Something has gone wrong in updating your password");
        }
    }

    private boolean validatePassword() {
        if(passwordPasswordField.getText().equals(""))
            return false;
        if (!passwordPasswordField.getText().equals(confirmPasswordField.getText())) {
            Utils.showErrorAlert("The passwords do not match!");
            return false;
        } else if (!Utils.isPasswordSecure(passwordPasswordField.getText())) {
            Utils.showErrorAlert("Not secure password, try another password");
            return false;
        }

        return true;
    }
}
