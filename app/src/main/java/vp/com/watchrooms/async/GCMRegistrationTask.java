package vp.com.watchrooms.async;

import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by vinaypothnis on 2015-01-02.
 */
public class GCMRegistrationTask extends AsyncTask<Void, Void, String> {

    
    public GCMRegistrationTask() {
        
    }

    @Override
    protected String doInBackground(Void... params) {
        String msg = "";
//        try {
//            if (gcm == null) {
//                gcm = GoogleCloudMessaging.getInstance(context);
//            }
//            registrationId = gcm.register(SENDER_ID);
//            msg = "Device registered, registration ID=" + registrationId;
//
//            // You should send the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send messages to your app.
//            // The request to your server should be authenticated if your app is using accounts.
//            sendRegistrationIdToBackend();
//
//            // Persist the regID - no need to register again.
//            storeRegistrationId(context, registrationId);
//        } catch (IOException ex) {
//            msg = "Error :" + ex.getMessage();
//            // If there is an error, don't just keep trying to register. Require the user to click a button again, or perform exponential back-off.
//        }
        return msg;
    }



}
