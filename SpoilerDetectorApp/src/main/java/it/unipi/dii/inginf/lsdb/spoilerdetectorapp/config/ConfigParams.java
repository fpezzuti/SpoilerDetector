package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.config;

import it.unipi.dii.inginf.lsdb.spoilerdetectorapp.utils.Utils;

import java.io.IOException;

public class ConfigParams {
    private static volatile ConfigParams localConfig;
    private String mongoDBPrimaryIP;
    private int mongoDBPrimaryPort;
   // private String mongoDBSecondIP;
    //private int mongoDBSecondPort;
   // private String mongoDBThirdIP;
    //private int mongoDBThirdPort;

    private String mongoDBUsername;
    private String mongoDBPassword;
    private String mongoDBName;

    private int limitNumber;

    public static ConfigParams getInstance() throws IOException {
        if (localConfig == null) {
            synchronized (ConfigParams.class) {
                if (localConfig == null) {
                    localConfig = Utils.getParams();
                }
            }
        }
    return localConfig;
    }

    public static ConfigParams getLocalConfig() {
        return localConfig;
    }

    public static void setLocalConfig(ConfigParams localConfig) {
            ConfigParams.localConfig = localConfig;
        }

    public String getMongoDBPrimaryIP() {
            return mongoDBPrimaryIP;
        }

    public void setMongoDBPrimaryIP(String mongoDBPrimaryIP) {
            this.mongoDBPrimaryIP = mongoDBPrimaryIP;
        }

    public int getMongoDBPrimaryPort() {
            return mongoDBPrimaryPort;
        }

    public void setMongoDBPrimaryPort(int mongoDBPrimaryPort) {
            this.mongoDBPrimaryPort = mongoDBPrimaryPort;
        }

    public String getMongoDBUsername() {
            return mongoDBUsername;
        }

    public void setMongoDBUsername(String mongoDBUsername) {
            this.mongoDBUsername = mongoDBUsername;
        }

    public String getMongoDBPassword() {
            return mongoDBPassword;
        }

    public void setMongoDBPassword(String mongoDBPassword) {
            this.mongoDBPassword = mongoDBPassword;
        }

    public String getMongoDBName() {
            return mongoDBName;
        }

    public void setMongoDBName(String mongoDBName) {
            this.mongoDBName = mongoDBName;
        }

    public int getLimitNumber() {
        return limitNumber;
    }

    public void setLimitNumber(int limitNumber) {
        this.limitNumber = limitNumber;
    }
}
