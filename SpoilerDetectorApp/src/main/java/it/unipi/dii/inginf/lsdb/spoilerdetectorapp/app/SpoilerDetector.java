package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.app;

import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.config.ConfigParams;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class SpoilerDetector extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SpoilerDetector.class.getResource(Utils.LOGIN_PAGE));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("SpoilerDetector");
        primaryStage.show();
        primaryStage.setResizable(true);
        primaryStage.sizeToScene();
        ConfigParams.getInstance();

        MongoDBDriver m = MongoDBDriver.getInstance();

        primaryStage.setOnCloseRequest(windowEvent -> {
            m.closeConnection();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}