module it.unipi.dii.inginf.lsdb.learnitapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires java.xml;
    requires xstream;
    requires com.google.gson;
    requires org.mongodb.bson;
    requires org.neo4j.driver;
    //requires pmml.evaluator;

    opens it.unipi.dii.inginf.lsdb.spoilerdetectorapp.config to xstream;
    opens it.unipi.dii.inginf.lsdb.spoilerdetectorapp.app to javafx.fxml;
    opens it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller to javafx.fxml;
    exports it.unipi.dii.inginf.lsdb.spoilerdetectorapp.model to org.mongodb.bson;
    exports it.unipi.dii.inginf.lsdb.spoilerdetectorapp.app;
    exports it.unipi.dii.inginf.lsdb.spoilerdetectorapp.controller;
}