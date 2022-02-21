package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.persistence;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.config.ConfigParams;
import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model.*;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.regex.Pattern;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

public class MongoDBDriver implements DBDriver {
    private static MongoDBDriver mongoDBInstance;

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Movie> moviesCollection;
    private static MongoCollection<User> usersCollection;
    private static String mongoDBPrimaryIP;
    private static int mongoDBPrimaryPort;
    private static String mongoDBSecondIP;
    private static int mongoDBSecondPort;
    private static String mongoDBThirdIP;
    private static int mongoDBThirdPort;

    private static String mongoDBUsername;
    private static String mongoDBPassword;
    private static String mongoDBName;
    private static CodecRegistry pojoCodecRegistry;
    private static CodecRegistry codecRegistry;
    private static String uriString;

    public static MongoDBDriver getInstance() {
        if (mongoDBInstance == null) {
            try {
                mongoDBInstance = new MongoDBDriver(ConfigParams.getInstance());
            } catch (Exception e) {
                uriString = "mongodb://127.0.0.1:27017/";
                mongoDBName = "spoilerDetector";
                mongoDBUsername = "root";
                mongoDBPassword = "";
            }

            mongoDBInstance = new MongoDBDriver();
            mongoDBInstance.initConnection();
        }

        return mongoDBInstance;
    }

    private MongoDBDriver() {

    }

    private MongoDBDriver(ConfigParams configParams) {
        this.mongoDBPrimaryIP = configParams.getMongoDBPrimaryIP();
        this.mongoDBPrimaryPort = configParams.getMongoDBPrimaryPort();
        this.mongoDBUsername = configParams.getMongoDBUsername();
        this.mongoDBPassword = configParams.getMongoDBPassword();
        this.mongoDBName = configParams.getMongoDBName();
    }

    @Override
    public boolean initConnection() {
        try {
            uriString = "mongodb://";
            if (!mongoDBUsername.equals("")) {
                uriString += mongoDBUsername + ":" + mongoDBPassword + "@";
            }

            pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
            codecRegistry = CodecRegistries.fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
            );

            ConnectionString uri = new ConnectionString("mongodb://127.0.0.1:27017/");
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(uri)
                    .retryWrites(true)
                    .codecRegistry(codecRegistry)
                    .build();

            mongoClient = MongoClients.create(settings);

            database = mongoClient.getDatabase(mongoDBName);

            DBObject ping = new BasicDBObject("ping","1");

            moviesCollection = database.getCollection("movies", Movie.class);
            usersCollection = database.getCollection("users", User.class);

            User u = usersCollection.find().first();

            database.runCommand((Bson) ping);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void closeConnection() {
        if (mongoClient != null)
            mongoClient.close();
    }


    /**
     * Returns a movie with a certain title
     * @param title the title of the movie
     */
    public Movie getMovieByTitle(String title) {
        try {
            return moviesCollection.find(Filters.eq("title", title)).first();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns a user with a certain username
     * @param username the username of the user
     */
    public User getUserByUsername(String username) {
        try {
            return usersCollection.find(Filters.eq("username", username)).first();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Updates the reviews of a movie
     * @param editedMovie the course to be updated
     */
    public boolean updateMovieReviews(Movie editedMovie) throws MongoException {
            Bson filter = Filters.eq("_id", editedMovie.getId());
            Bson update_reviews = Updates.set("reviews", editedMovie.getReviews());
            moviesCollection.updateOne(filter, update_reviews);
            return true;
    }

    /**
     * Deletes a course from the courses collection
     */
    private void removeMovie(Movie toBeDeleted) throws  MongoException {

        Bson match = Filters.eq("_id", toBeDeleted.getId());
        moviesCollection.deleteOne(match);
    }

    public boolean deleteMovie(Movie toBeDeleted) {
        try {
            removeMovie(toBeDeleted);
        } catch (MongoException e) {
            e.printStackTrace();
            return false;
        }

        try {
            deleteMovieReviewsFromUsers(toBeDeleted);
        }
        catch (MongoException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * Edits the information about a specific review of a movie
     */
    public boolean editReview(Movie course, Review review){
        List<Review> reviews = course.getReviews();
        int count = 0;
        for (Review r: reviews) {
            if (r.getUsername().equals(review.getUsername())) {
                reviews.set(count, review);
                break;
            }
            count++;
        }
        return updateMovieReviews(course);
    }

    /**
     * Deletes all the reviews of a certain User from Movies
     */
    public void deleteUserReviewsFromMovie(User user) throws MongoException {
        Bson pullFilter = Updates.pull("reviews", Filters.eq("username", user.getUsername()));
        moviesCollection.updateMany(Filters.empty(), pullFilter);
    }

    /**
     * Deletes all the reviews of a certain Movie from a Users
     */
    public void deleteMovieReviewsFromUsers(Movie movie) throws MongoException {
        Bson pullFilter = Updates.pull("reviews", Filters.eq("title", movie.getTitle()));
        usersCollection.updateMany(Filters.empty(), pullFilter);
    }



    /**
     * Finds all movies with certain parameters
     * @param title the searched title
     * @return a list of Movie
     */
    public List<Movie> findMovies(String title, int toSkip, int quantity){

        List<Movie> movies;

        Bson filter = null;

        if(!title.equals("")){

            Pattern pattern = Pattern.compile("^.*" + title + ".*$", Pattern.CASE_INSENSITIVE);
            filter = Filters.regex("title", pattern);
        }

        movies = moviesCollection.find(filter)
                .skip(toSkip)
                .limit(quantity)
                .projection(Projections.exclude("reviews"))
                .into(new ArrayList<Movie>());

        return movies;
    }

    public List<Movie> findMovieSnapshotsByIds(List<ObjectId> ids) {
        return moviesCollection.find(Filters.in("_id", ids)).projection(Projections.exclude("reviews")).into(new ArrayList<>());
    }


    private List<User> findUserSnapshotsByIds(List<String> usernames) {
        return usersCollection.find(Filters.in("username", usernames)).projection(Projections.exclude("reviews")).into(new ArrayList<>());
    }

    /**
     * Adds a Review to a Movie
     */
    private void addReviewToMovie(Movie movie, Review review) throws MongoException{
        List<Review> reviewsList = movie.getReviews();
        if (reviewsList == null)
            reviewsList = new ArrayList<>();
        reviewsList.add(review);
        movie.setReviews(reviewsList);
        updateMovieReviews(movie);
    }

    /**
     * Adds a Review to the document of a User
     */
    private void addReviewToUser(Movie movie, Review review) throws MongoException{
        Document d = new Document("title", movie.getTitle())
                                .append("review_date", review.getDate())
                                .append("review_text", review.getReview_text())
                                .append("rating", review.getRating())
                                .append("is_spoiler", review.getSpoiler());

        usersCollection.updateOne(Filters.eq("username", review.getUsername()),
                Updates.push("reviews", d));
    }

    /**
     * Handles the whole process of adding a Review to a Movie and a User
     * @param movie movie
     * @param review review
     * @return true if the operation ends with a success, otherwise false
     */
    public boolean addReview(Movie movie, Review review) throws MongoException{
        addReviewToMovie(movie, review);

        String exception;
        try{
            addReviewToUser(movie, review);
            return true;
        }catch (MongoException e){
            exception = e.getMessage();
        }

        //rollback
        try {
            deleteReview(movie, review);
        } catch (MongoException e){
            System.err.println("Error while registering a new review");
        }
        return false;
    }

    private void deleteReviewFromUser(Movie movie, Review review) throws MongoException{
        Bson userFilter = Filters.eq("username", review.getUsername());
        Bson pullFilter = Updates.pull("reviews", Filters.eq("title", review.getTitle()));
        usersCollection.updateOne(userFilter, pullFilter);
    }

    private void deleteReviewFromMovie(Movie movie, Review review) throws MongoException{
        Bson courseFilter = Filters.eq("title", movie.getTitle());
        Bson pullFilter = Updates.pull("reviews", Filters.eq("username", review.getUsername()));
        moviesCollection.updateOne(courseFilter, pullFilter);
    }

    /**
     * Deletes a Review from a Movie and a User
     */
    public boolean deleteReview(Movie movie, Review review) throws MongoException{
        List<Review> reviewsList  = movie.getReviews();
        reviewsList.remove(review);

        deleteReviewFromMovie(movie, review);

        String exception;
        try {
            deleteReviewFromUser(movie, review);
            return true;
        } catch (MongoException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    /**
     * Gets the review of the user for a certain Movie, if there exists one
     * @return the Review object or null
     */
    public Review getMovieReviewByUser(Movie movie, User user) {
        Movie m = moviesCollection.find(Filters.eq("_id", movie.getId())).projection(Projections.elemMatch(
                "reviews", Filters.eq("username", user.getUsername()))).first();
        if (m == null)
            return null;
        else {
            if (m.getReviews() == null)
                return null;
            else
                return m.getReviews().get(0);
        }
    }

    /**
     * Checks if the password submitted by a user is the same as the hash saved in the database
     */
    public User login(String username, String password) {
        /*
        String shaPass = "";
        try {
            shaPass = Utils.toHexString(Utils.getSHA(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }*/

        Bson eqUsername = Filters.eq("username", username);
        Bson eqPass = Filters.eq("password", password);
        try {
            User loggedUser = usersCollection.find(Filters.and(eqUsername, eqPass)).projection(Projections.exclude("reviews")).first();
            return loggedUser;
        } catch (MongoException e) {
            return null;
        }
    }

    /**
     * Updates the information about a user
     */
    public boolean editProfileInfo(User newInfo) throws MongoException {
        /*String shaPass = "";
        try {
            shaPass = Utils.toHexString(Utils.getSHA(newInfo.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }*/

        try {
            Bson match = Filters.eq("username", newInfo.getUsername());
            Bson update = Updates.set("password", /*shaPass*/ newInfo.getPassword());
            usersCollection.updateOne(match, update);
        }
        catch (MongoException e){
            System.err.println("Error in updating user information");
            return false;
        }
        return true;
    }


    /**
     * Search functionality for users, confronts the searched text with the username
     */
    public List<User> searchUserByUsername(String searchedText, int skip, int limit) {
        try {
            Pattern pattern = Pattern.compile("^.*" + searchedText + ".*$", Pattern.CASE_INSENSITIVE);
            Bson usernameFilter = Filters.regex("username", pattern);
            List<User> res = usersCollection.find(usernameFilter)
                    .skip(skip)
                    .limit(limit)
                    .projection(Projections.exclude("reviews"))
                    .into(new ArrayList<>());

            return res;
        } catch (MongoException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Search functionality for users, confronts the searched text with the title
     */
    public List<Movie> findMoviesByTitle(String searchedText, int skip, int limit) {
        try {
            Pattern pattern = Pattern.compile("^.*" + searchedText + ".*$", Pattern.CASE_INSENSITIVE);
            Bson movieFilter = Filters.regex("title", pattern);
            List<Movie> res = moviesCollection.find(movieFilter)
                    .skip(skip)
                    .limit(limit)
                    .projection(Projections.exclude("reviews"))
                    .into(new ArrayList<>());

            return res;
        } catch (MongoException e) {
            System.err.println("Error while retrieving searched movies by title \""+searchedText+"\"");
            return new ArrayList<>();
        }
    }

    /**
     * Search functionality for users, confronts the searched text with the username
     */
    public List<User> findUsersByUsername(String searchedText, int skip, int limit) {
        try {
            Pattern pattern = Pattern.compile("^.*" + searchedText + ".*$", Pattern.CASE_INSENSITIVE);
            Bson userFilter = Filters.regex("username", pattern);
            List<User> res = usersCollection.find(userFilter)
                    .skip(skip)
                    .limit(limit)
                    .projection(Projections.exclude("reviews"))
                    .into(new ArrayList<>());

            return res;
        } catch (MongoException e) {
            System.err.println("Error while retrieving searched users by username \""+searchedText+"\"");
            return new ArrayList<>();
        }
    }

    public boolean checkIfUserExists(String username) {
        try {
            Pattern pattern = Pattern.compile("^.*" + username + ".*$", Pattern.CASE_INSENSITIVE);
            User user = usersCollection.find(Filters.regex("username", pattern))
                    .projection(Projections.exclude("reviews"))
                    .first();
            return (user != null);
        } catch (MongoException e) {
            return true;
        }
    }

    /**
     * Adds a new user to the database
     */
    public boolean addUser(User user) throws MongoException {/*
        String shaPass = "";
        try {
            shaPass = Utils.toHexString(Utils.getSHA(user.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }*/

        String clear = user.getPassword();
        /*user.setPassword(shaPass)*/
        try {
            usersCollection.insertOne(user);
        }catch (MongoException e) {
            return false;
        }
        user.setPassword(clear);
        return true;
    }

    private void removeUser(User user) throws MongoException {
        Bson filter = Filters.eq("username", user.getUsername());
        usersCollection.deleteOne(filter);
    }

    public boolean deleteUser(User user){

        deleteUserReviewsFromMovie(user);

        try{
            removeUser(user);
        }
        catch (MongoException e){
            return false;
        }
        return true;
    }

    /**
     * Gets all the reviewes of a user
     */
    public List<Review> getAllUserReviews(User user, int skip, int limit) {
        List<Document> aggregation =
                Arrays.asList(new Document("$match",
                                new Document("username", user.getUsername())),
                        new Document("$unwind",
                                new Document("path", "$reviews")),
                        new Document("$sort",
                                new Document("reviews.review_date", -1L)),
                        new Document("$skip", skip),
                        new Document("$limit", limit),
                        new Document("$group",
                                new Document("_id", "$_id")
                                        .append("reviews",
                                                new Document("$push", "$reviews"))));

        User res = usersCollection.aggregate(aggregation).first();
        if (res == null)
            return new ArrayList<>();
        if (res.getReviews() == null)
            return new ArrayList<>();
        return res.getReviews();
    }

    public List<Movie> getMostRecentMovies(int skip, int limit){
        List<Document> aggregation = Arrays.asList(new Document("$sort", new Document("release_date", -1L)), new Document("$skip", skip), new Document("$limit", limit));
        List<Movie> res = moviesCollection.aggregate(aggregation).into(new ArrayList<>());
        if (res == null)
            return new ArrayList<>();
        return res;
    }

    public List<Movie> getRandomMovies(int limit){
        List<Document> aggregation = Arrays.asList(new Document("$sample", new Document("size", limit)));
        List<Movie> res = moviesCollection.aggregate(aggregation).into(new ArrayList<>());
        if (res == null)
            return new ArrayList<>();
        return res;
    }


}
