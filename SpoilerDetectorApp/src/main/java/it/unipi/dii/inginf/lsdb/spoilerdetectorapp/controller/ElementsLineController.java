package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller;

import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.config.ConfigParams;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Movie;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.persistence.MongoDBDriver;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.util.List;

public class ElementsLineController{
    @FXML AnchorPane elementsLine;
    @FXML private Label headerLabel;
    @FXML private ImageView buttonImage;
    @FXML private HBox itemsHBox;

    private Movie movie;
    private int listType;
    private int pageNumber;
    private int limit;
    private MongoDBDriver mongoDBDriver;

    public void initialize(){
        pageNumber = 0;
        limit = ConfigParams.getLocalConfig().getLimitNumber();
        mongoDBDriver = MongoDBDriver.getInstance();
    }

    public void setMovies(Movie movie, int type){
        listType = type;
        this.movie = movie;

        loadMore();
        switch (type){
            case Utils.MOST_RECENT:
                headerLabel.setText("Most recent movies");
                break;
            case Utils.RANDOM_MOVIES:
                headerLabel.setText("Some other movies");
                break;
        }

        buttonImage.setImage(new Image(String.valueOf(ElementsLineController.class.getResource(Utils.READ_MORE))));
        buttonImage.setPreserveRatio(true);
        buttonImage.setFitWidth(40);
        buttonImage.setFitWidth(40);
        buttonImage.setOnMouseClicked(clickEvent -> loadMore());
        buttonImage.setCursor(Cursor.HAND);
    }



    private void loadMore() {
        int skip = pageNumber*limit;
        pageNumber++;
        List<Movie> moreMovies = null;


        switch (listType) {
            case Utils.MOST_RECENT:
                moreMovies = mongoDBDriver.getMostRecentMovies(skip, limit);
                if (moreMovies != null)
                    Utils.addMoviesSnapshot(itemsHBox, moreMovies);
                break;
            case Utils.RANDOM_MOVIES:
                moreMovies = mongoDBDriver.getRandomMovies(limit);
                if(moreMovies != null)
                    Utils.addMoviesSnapshot(itemsHBox, moreMovies);

        }
    }
}
