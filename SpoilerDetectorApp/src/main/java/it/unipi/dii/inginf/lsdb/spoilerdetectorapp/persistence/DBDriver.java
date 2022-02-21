package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.persistence;

public interface DBDriver {
    boolean initConnection();
    void closeConnection();
}
