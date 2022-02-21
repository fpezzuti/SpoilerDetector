package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller;

import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.User;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class UserSnapshotController {
    @FXML AnchorPane userSnapshot;
    @FXML Label usernameLabel;
    private User user;

    public void initialize(){
        userSnapshot.setCursor(Cursor.HAND);
        userSnapshot.setOnMouseClicked(this::showCompleteUserInfo);
    }

    public void setSnapshotUser(User user) {
        usernameLabel.setText(user.getUsername());

        this.user = user;
    }

    private void showCompleteUserInfo(MouseEvent mouseEvent){
        ProfilePageController profilePageController = (ProfilePageController) Utils.changeScene(Utils.PROFILE_PAGE, mouseEvent);
        profilePageController.setProfileUser(user);
    }
}
