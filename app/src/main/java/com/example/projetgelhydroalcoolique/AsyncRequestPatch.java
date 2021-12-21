package com.example.projetgelhydroalcoolique;
/**
 * Created by wassim6975 on 17/12/2021 (dd/mm/yyyy)
 */
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

// This class allow us to get the data batLevel/getLevel (with a request Patch to the server)
// It takes 1 parameter -an ArrayList size 2- with first the token and second smsReceiveData
// It returns a string data (batLevel/getLevel)
public class AsyncRequestPatch extends AsyncTask<ArrayList<String>, String, String> {
    private String dataOutput = "";
    private String token = "";
    private String smsReceiveData = "";

    protected String doInBackground(ArrayList<String>... data) {

        // Request data gelLevel and batterieLevel ...
        // Création de l'objet URL token
        try {
            URL urlData = new URL("https://api.superveezer.com/distributors/350/usages");
            // Create connection
            HttpsURLConnection myConnectionData = (HttpsURLConnection) urlData.openConnection();
            // Header
            myConnectionData.setRequestProperty("Content-Type", "application/json");
            token = data[0].get(0);
            smsReceiveData = data[0].get(1);
            Log.i("params", smsReceiveData);
            Log.i("params", token);
            // here the parsing

            //d: un chiffre
            //+: 1 ou plusieurs chiffres
            String V = "V=\\d+";
            String Tm= "Tm=\\d+";
            String valueOfV = ""; // batteryLevel
            String valueOfTm = ""; // numberOfUsages
            Pattern patternV = Pattern.compile(V);
            Pattern patternTm = Pattern.compile(Tm);
            Matcher matcherV = patternV.matcher(smsReceiveData);
            Matcher matcherTm = patternTm.matcher(smsReceiveData);
            if (matcherV.find()) {
                //afficher le premier groupes
                valueOfV=matcherV.group().substring(2);
                Log.i("parsing", valueOfV);
            }
            if(matcherTm.find() ){
                valueOfTm= matcherTm.group().substring(3);
                Log.i("parsing", valueOfTm);
            }

            myConnectionData.setRequestProperty("Authorization", "Bearer "+ token);
            myConnectionData.setRequestMethod("PATCH");

            // Writing
            // Getting the params
            String bodyData = "{\r\n    \"numberOfUsages\":"+valueOfTm+",\r\n    \"batteryLevel\":"+valueOfV+"\r\n}";
            // Enable writing
            myConnectionData.setDoOutput(true);
            // Write the data
            myConnectionData.getOutputStream().write(bodyData.getBytes());
            int responseCode2 = myConnectionData.getResponseCode();
            Log.i("responseCode2", "responseCode2 :" + responseCode2);
            if (responseCode2 == 200) {
                // Success
                Log.i("responseCode2", "HTTP ok : responseCode2 :" + responseCode2);
                String line;
                BufferedReader bufferReaderToken = new BufferedReader(new InputStreamReader(myConnectionData.getInputStream()));
                while ((line = bufferReaderToken.readLine()) != null) {
                    dataOutput += line;
                }
                Log.i("dataOutput", dataOutput);
            }
            else {
                // Failed
                Log.i("responseCode2", "HTTP false : responseCode2 :" + responseCode2);
            }
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataOutput;
    }
}
