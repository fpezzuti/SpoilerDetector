package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller;

import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Session;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.User;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class LoginPageController {
    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button signUpButton;

    public void initialize() {
        loginButton.setOnMouseClicked(this::loginHandler);
        loginButton.setCursor(Cursor.HAND);
        signUpButton.setOnMouseClicked(this::signUpHandler);
        signUpButton.setCursor(Cursor.HAND);
    }

    public void loginHandler(MouseEvent clickEvent) {
        User loggedUser = MongoDBDriver.getInstance().login(usernameTextField.getText(), passwordField.getText());
        if (loggedUser == null) {
            Utils.showErrorAlert("Error: wrong username or password");
        } else {
            Session.getLocalSession().setLoggedUser(loggedUser);
            Utils.changeScene(Utils.DISCOVERY_PAGE, clickEvent);
        }
    }

    public void signUpHandler(MouseEvent clickEvent) {
        Utils.changeScene(Utils.REGISTRATION_PAGE, clickEvent);
    }
}
