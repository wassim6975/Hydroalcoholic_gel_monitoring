package com.example.projetgelhydroalcoolique;
/**
 * Created by wassim6975 on 17/12/2021 (dd/mm/yyyy)
 */
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

// This class allow us to receive an sms and send it to the MainActivityClass to use it
public class MySmsReceiver extends BroadcastReceiver {
    public static final String pdu_type = "pdus";
    public String strMessage = "";
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the SMS message.
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        //String strMessage = "";
        String format = bundle.getString("format");
        // Retrieve the SMS message received.
        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {
            // Check the Android version.
            boolean isVersionM =
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
            // Fill the msgs array.
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                // Check Android version and use appropriate createFromPdu.
                if (isVersionM) {
                    // If Android version M or newer:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    // If Android version L or older:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                // Build the message to show.
                //strMessage += "SMS from " + msgs[i].getOriginatingAddress();
                strMessage += msgs[i].getMessageBody();
                // Log and display the SMS message.
                Log.d("onReceive", "onReceive: " + strMessage);
                Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
                try {
                    MainActivity.getInstace().updateTheTextView(strMessage);
                } catch (Exception e) {

                }
            }
        }
    }

}