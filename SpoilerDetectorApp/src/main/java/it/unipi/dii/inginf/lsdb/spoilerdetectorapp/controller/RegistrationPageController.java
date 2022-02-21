package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller;

import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Session;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.User;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class RegistrationPageController {

    @FXML private Button signUpButton;
    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ImageView spoilerdetectorLogoImageView;
    @FXML private ImageView spoilerdetectorImageView;

    private User admin;

    public void initialize() {
        admin = Session.getLocalSession().getLoggedUser();

        spoilerdetectorImageView.setCursor(Cursor.HAND);
        signUpButton.setCursor(Cursor.HAND);
        signUpButton.setOnMouseClicked(this::signUpHandler);

        if (admin != null) {
            spoilerdetectorLogoImageView.setImage(new Image(String.valueOf(RegistrationPageController.class.getResource("/img/createAdmin.png"))));
            spoilerdetectorLogoImageView.setOnMouseClicked(clickEvent -> Utils.changeScene(Utils.DISCOVERY_PAGE, clickEvent));
            signUpButton.setText("Create admin");
            signUpButton.setStyle("-fx-background-color: red; -fx-background-radius: 13px");
        } else {
            spoilerdetectorLogoImageView.setOnMouseClicked(clickEvent -> Utils.changeScene(Utils.LOGIN_PAGE, clickEvent));
            spoilerdetectorLogoImageView.setCursor(Cursor.HAND);
            spoilerdetectorImageView.setOnMouseClicked(clickEvent -> Utils.changeScene(Utils.LOGIN_PAGE, clickEvent));

        }
    }

    public void signUpHandler(MouseEvent clickEvent) {
        //System.out.println("signuphandler");
        int role = admin == null ? 0 : 1;

        if (validateUsernameAndPassword()) {
            System.out.println("valid credentials");
            User toRegister = new User(usernameTextField.getText(), passwordPasswordField.getText(), role);
            boolean ret = MongoDBDriver.getInstance().addUser(toRegister);

            if (ret) {
                Utils.showInfoAlert("User registered with success");
                Utils.changeScene(Utils.LOGIN_PAGE, clickEvent);
            } else {
                Utils.showInfoAlert("Error, registration failed");
            }
        }
    }

    private boolean validateUsernameAndPassword() {
        if (!passwordPasswordField.getText().equals(confirmPasswordField.getText())) {
            Utils.showErrorAlert("The passwords do not match!");
            return false;
        } else if (!Utils.isPasswordSecure(passwordPasswordField.getText())) {
            Utils.showErrorAlert("Not secure password, try another password");
            return false;
        } else if (usernameTextField.getText().length() < 5) {
            Utils.showErrorAlert("Username too short, try another username");
            return false;
        } else if (MongoDBDriver.getInstance().checkIfUserExists(usernameTextField.getText())) {
            Utils.showErrorAlert("Username already taken, try another username");
            return false;
        }
        return true;
    }

}
