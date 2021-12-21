package com.example.projetgelhydroalcoolique;
/**
 * Created by wassim6975 on 17/12/2021 (dd/mm/yyyy)
 */
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    public TextView smsContent;
    public IntentFilter receiveFilter;
    public MySmsReceiver messageReceiver;
    private static MainActivity ins;
    private String smsReceiveData = "V=4500mV T=500 Tm=240";
    private ArrayList dataInput =  new ArrayList<String>();
    private String dataOutput = "";
    JSONObject dataOutputObject = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Request permission (READING/SENDING SMS)
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);
        messageReceiver = new MySmsReceiver();
        ins = this;
    }

    public static MainActivity  getInstace(){
        return ins;
    }

    public void updateTheTextView(final String t) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                TextView textV1 = (TextView) findViewById(R.id.smsContent);
                textV1.setText(t);
                // Store the sms data
                smsReceiveData = t;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        Button buttonRequest = findViewById(R.id.buttonRequest);

        final TextView response1 = (TextView) findViewById(R.id.response1);
        final TextView response2 = (TextView) findViewById(R.id.response2);

        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // AsyncTask for getting token
                AsyncRequest token = (AsyncRequest) new AsyncRequest().execute();
                // AsyncTask for getting data (bat/gel level)
                String tokenRequest = null;
                try {
                    tokenRequest = token.get();
                    dataInput.clear();
                    dataInput.add(tokenRequest);
                    dataInput.add(smsReceiveData);
                    Log.i("MainData", tokenRequest);
                    Log.i("MainData", smsReceiveData);
                    AsyncRequestPatch request2 = new AsyncRequestPatch();
                    AsyncRequestPatch request2Data = (AsyncRequestPatch) request2.execute(dataInput);
                    dataOutput = request2Data.get();
                    // Parse String to JSON
                    dataOutputObject = new JSONObject(dataOutput);
                    String batLevel = dataOutputObject.getString("batLevel");
                    String gelLevel = dataOutputObject.getString("gelLevel");
                    Log.i("MainData", batLevel);
                    Log.i("MainData", gelLevel);
                    // Setting text
                    response1.setText(batLevel);
                    response2.setText(gelLevel);

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    // This function allow us to send a message
    public void smsSendMessage(View view) {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);

        // Find the sms_message view.
        EditText smsInputV = (EditText) findViewById(R.id.smsInputV);
        EditText smsInputT = (EditText) findViewById(R.id.smsInputT);
        EditText smsInputTm = (EditText) findViewById(R.id.smsInputTm);
        // Find the numberPhone
        EditText phoneNumber = (EditText) findViewById(R.id.phoneNumber);

        // Get the text of the sms message.
        String smsMessage = "V=" +  smsInputV.getText().toString() +"mV "+ "T=" + smsInputT.getText().toString()+ " Tm="+smsInputTm.getText().toString();
        // Set the service center address if needed, otherwise null.
        String scAddress = null;
        // Set pending intents to broadcast
        // when message sent and when delivered, or set to null.
        PendingIntent sentIntent = null, deliveryIntent = null;
        // Use SmsManager.
        SmsManager smsManager = SmsManager.getDefault();
        // Here we send the message
        smsManager.sendTextMessage(phoneNumber.getText().toString(), scAddress, smsMessage, sentIntent, deliveryIntent);
    }

}