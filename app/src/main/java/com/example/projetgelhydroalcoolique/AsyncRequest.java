package com.example.projetgelhydroalcoolique;
/**
 * Created by wassim6975 on 17/12/2021 (dd/mm/yyyy)
 */
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

// This class allow us to get the token (with a request POST to the server)
// It return a string (token)
public class AsyncRequest extends AsyncTask<String, String, String> {
    protected String doInBackground(String... smsDataReceive) {
        // Initialization
        String Resultat = "";
        String token = "";
        JSONObject tokenObject = null;

        try {
            Log.i("AsyncTask","Get xml change with AsyncTaskRunner");

            // Connection
            // Création de l'objet URL token
            URL urlToken = new URL("https://api.superveezer.com/auth/login");
            // Create connection
            HttpsURLConnection myConnection = (HttpsURLConnection) urlToken.openConnection();
            // Header
            myConnection.setRequestProperty("Content-Type", "application/json");
            myConnection.setRequestProperty("x-app-name", "bo");
            myConnection.setRequestMethod("POST");

            // Writing
            // Create the data
            String bodyData = "{\"email\": \"dev.sms2@yopmail.com\", \"password\": \"123456!\"}";
            // Enable writing
            myConnection.setDoOutput(true);
            // Write the data
            myConnection.getOutputStream().write(bodyData.getBytes());
            int responseCode = myConnection.getResponseCode();

            // Reading token response
            Log.i("responseCode", "responseCode : " + responseCode);
            if (responseCode == 201) {
                Log.i("responseCode", "HTTP_OK");

                String line;
                BufferedReader bufferReaderToken = new BufferedReader(new InputStreamReader(
                        myConnection.getInputStream()));
                while ((line = bufferReaderToken.readLine()) != null) {
                    token += line;
                    tokenObject = new JSONObject(token);
                    token = tokenObject.getString("access_token");
                }
                Log.i("token", token);
                Log.i("AsyncRequestReturn", token);
                return token;

            } else {
                Log.e("responseCode", "14 - False - HTTP_OK");
                token = "";
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Log.i("AsyncTask", "doInBackground: done");
        return Resultat;
    }

    protected void onProgressUpdate(Integer... progress) {
        // receive progress updates from doInBackground
        //super.onPreExecute();
        // display a progress dialog for good user experiance
        /*ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading data from Alcohol gel");
        progressDialog.setCancelable(false);
        progressDialog.show();
        */
    }

    protected void onPostExecute(Long result) {
        // update the UI after background processes completes
    }
}
