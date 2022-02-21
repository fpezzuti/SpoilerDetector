package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.config.ConfigParams;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller.MoviePageController;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller.ElementsLineController;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller.MovieSnapshotController;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller.UserSnapshotController;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Movie;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.Session;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.User;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class Utils {

    public static final String READ_MORE = "/img/readMore.png";
    public static final String STAR_ON = "/img/star-on.png";
    public static final String STAR_OFF = "/img/star-off.png";
    public static final String TRASH_BIN = "/img/trash-bin.png";
    public static final String USER_DEFAULT = "/img/userDefault.png";
    public static final String LOGIN_PAGE = "/fxml/LoginPage.fxml";

    public final static String DISCOVERY_PAGE = "/fxml/DiscoveryPage.fxml";
    public final static String PERSONAL_PAGE = "/fxml/PersonalPage.fxml";
    public final static String REGISTRATION_PAGE = "/fxml/RegistrationPage.fxml";
    public final static String PROFILE_PAGE = "/fxml/ProfilePage.fxml";
    public final static String MOVIE_PAGE = "/fxml/MoviePage.fxml";
    public final static String REVIEW_SNAPSHOT = "/fxml/ReviewSnapshotPage.fxml";
    public final static int MAX_RATING = 10;

    public final static int MOST_RECENT = 0;
    public final static int RANDOM_MOVIES = 1;

    public static ConfigParams getParams() {
        if (validConfigParams()) {
            XStream xstream = new XStream();
            xstream.addPermission(AnyTypePermission.ANY);
            xstream.addPermission(NullPermission.NULL);   // allow "null"
            xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
            String text = null;

            try {
                text = new String(Files.readAllBytes(Paths.get("config.xml")));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

            return (ConfigParams) xstream.fromXML(text);

        } else {
            System.out.println("Problem with the configuration file");
            //Utils.showAlert("Problem with the configuration file!");
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(1);
        }

        return null;
    }

    private static boolean validConfigParams() {
        Document document;
        try
        {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            document = documentBuilder.parse("config.xml");
            Schema schema = schemaFactory.newSchema(new StreamSource("config.xsd"));
            schema.newValidator().validate(new DOMSource(document));
        }
        catch (Exception e) {
            if (e instanceof SAXException)
                System.err.println("Validation Error: " + e.getMessage());
            else
                System.err.println(e.getMessage());

            return false;
        }
        return true;
    }

    public static boolean isPasswordSecure(String password) {
        Pattern pattern = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static Object changeScene (String fileName, Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource(fileName));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void showErrorAlert (String text) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText(text);
        errorAlert.setHeaderText("Please click 'OK' and try again");
        errorAlert.setTitle("Error...");
        Image errorImage = new Image(Utils.class.getResource("/img/error.png").toString());
        ImageView errorImageView = new ImageView(errorImage);
        errorImageView.setFitHeight(70);
        errorImageView.setFitWidth(70);
        errorAlert.setGraphic(errorImageView);
        errorAlert.show();
    }

    public static void showInfoAlert (String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(text);
        alert.setHeaderText("Confirm Message");
        alert.setTitle("Information");
        ImageView imageView = new ImageView(new Image(String.valueOf(Utils.class.getResource("/img/success.png"))));
        imageView.setFitHeight(60);
        imageView.setFitWidth(60);
        imageView.setPreserveRatio(true);
        alert.setGraphic(imageView);
        alert.show();
    }

    public static void showSpoilerAlert (HBox spoilerHBox, Label contentLabel, String comment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"This review may contain a spoiler. Do you want to read its content?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Spoiler Alert!");
        alert.setTitle("Confirm");
        ImageView imageView = new ImageView(new Image(String.valueOf(Utils.class.getResource("/img/danger.png"))));
        imageView.setFitHeight(60);
        imageView.setFitWidth(60);
        imageView.setPreserveRatio(true);
        alert.setGraphic(imageView);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            spoilerHBox.setVisible(false);
            contentLabel.setText(comment);
        }
    }

    public static Pane loadMovieSnapshot(Movie movie) {
        Pane pane = null;
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource("/fxml/MovieSnapshot.fxml"));
            pane = loader.load();
            MovieSnapshotController movieSnapshotController = loader.getController();
            movieSnapshotController.setSnapshotMovie(movie);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pane;
    }

    public static Pane loadUserSnapshot(User user){
        Pane pane = null;
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource("/fxml/UserSnapshot.fxml"));
            pane = loader.load();
            UserSnapshotController userSnapshotController = loader.getController();
            userSnapshotController.setSnapshotUser(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pane;
    }

    public static void addMoviesSnapshot(HBox courseHBox, List<Movie> movies) {
        for (Movie movie : movies) {
            Pane coursePane = loadMovieSnapshot(movie);
            courseHBox.getChildren().add(coursePane);
        }
    }

    public static void addLine(VBox discoverySections, Movie movie, User user, int type) {
        Pane line = loadElementsLine(movie, user, type);
        discoverySections.getChildren().add(line);
    }

    private static Pane loadElementsLine(Movie movie, User user, int type) {
        //coursesUsersLine
        // 0 -> courses
        // 1 -> users
        //buttonLoadMore
        // 0 -> no button
        // 1 -> yes button
        Pane pane = null;
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource("/fxml/ElementsLine.fxml"));
            pane = loader.load();

            ElementsLineController moviesLine = loader.getController();
            moviesLine.setMovies(movie, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pane;
    }


    public static void fillStars(int rating, HBox ratingHBox){
        for(Node star: ratingHBox.getChildren()){
            int index = ratingHBox.getChildren().indexOf(star);
            if(index > rating - 1)
                break;
            ImageView starImageView = (ImageView)star;
            Image starImage = new Image(String.valueOf(MoviePageController.class.getResource(STAR_ON)));
            starImageView.setImage(starImage);
            ratingHBox.getChildren().set(index, starImageView);
        }
        for(int index=rating; index <MAX_RATING; index++){
            Node star = ratingHBox.getChildren().get(index);
            ImageView starImageView = (ImageView)star;
            Image starImage = new Image(String.valueOf(MoviePageController.class.getResource(STAR_OFF)));
            starImageView.setImage(starImage);
            ratingHBox.getChildren().set(index, starImageView);
        }
    }

    public static void  logout(MouseEvent clickEvent){
        Session.destroySession();
        changeScene(LOGIN_PAGE, clickEvent);

    }

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash)
    {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
}
