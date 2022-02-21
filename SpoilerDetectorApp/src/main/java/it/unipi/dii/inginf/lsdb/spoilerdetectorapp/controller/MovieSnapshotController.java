package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller;

import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Movie;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class MovieSnapshotController {
    @FXML AnchorPane movieSnapshot;
    @FXML Label titleLabel;
    @FXML Label durationLabel;
    @FXML ImageView moviePicImageView;
    private Movie movie;

    public void initialize(){
        movieSnapshot.setCursor(Cursor.HAND);
        movieSnapshot.setOnMouseClicked(this::showCompleteMovieInfo);
    }

    public void setSnapshotMovie(Movie movie) {
        titleLabel.setText(movie.getTitle());

        if(movie.getCoverImage()!=null)
            moviePicImageView.setImage(new Image(movie.getCoverImage()));
        else
            moviePicImageView.setImage(new Image(String.valueOf(ReviewSnapshotPageController.class.getResource("/img/movieDefault.png"))));

        durationLabel.setText("Duration: " + movie.getDuration());

        this.movie = movie;
    }

    private void showCompleteMovieInfo(MouseEvent mouseEvent){
        MoviePageController moviePageController = (MoviePageController) Utils.changeScene(Utils.MOVIE_PAGE, mouseEvent);
        moviePageController.setMovie(movie);
    }
}
