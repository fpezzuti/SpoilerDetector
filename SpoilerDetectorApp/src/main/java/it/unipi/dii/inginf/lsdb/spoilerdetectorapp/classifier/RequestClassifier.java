package it.unipi.dii.inginf.lsdb.spoilerdetectorapp.classifier;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class RequestClassifier {
    private static final String urlString = "http://localhost:12345/predict";

    public static boolean sendRequest(String review) throws IOException {

        URL url = new URL(urlString);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST"); // POST request to the flask-server
        http.setDoOutput(true);

        String jsonString = "{\"review_text\":\""+review+"\"}"; // JSON document to send to the flask-server

        byte[] out = jsonString.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        http.connect();
        try(OutputStream os = http.getOutputStream()) {
            os.write(out); // send the JSON to the flask-server
        }

        Reader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8")); // read response from flask-server

        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;)
            sb.append((char)c);
        String response = sb.toString();

        if(response.contains("False")) return false;
        else return true;

    }

}
