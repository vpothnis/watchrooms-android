package vp.com.watchrooms.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import vp.com.watchrooms.CommonConstants;
import vp.com.watchrooms.R;
import vp.com.watchrooms.models.Device;
import vp.com.watchrooms.models.User;

import static java.lang.String.format;


/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class LoginActivity extends PlusBaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    // UI references.
    private View mProgressView;
    private SignInButton mPlusSignInButton;
    private View mSignOutButtons;
    private View mLoginFormView;

    // The signed in user
    Person currentPerson;
    String currentPersonEmail;
    User currentBackendUser;

    // GCM related attributes
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    private String SENDER_ID = "321890819296";
    private GoogleCloudMessaging gcm = null;
    private String registrationId;
    private Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();

        // Find the Google+ sign in button.
        mPlusSignInButton = (SignInButton) findViewById(R.id.plus_sign_in_button);
        if (supportsGooglePlayServices()) {
            // Set a listener to connect the user when the G+ button is clicked.
            mPlusSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });
        } else {
            // Don't offer G+ sign in if the app's version is too low to support Google Play Services.
            mPlusSignInButton.setVisibility(View.GONE);
            return;
        }

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mSignOutButtons = findViewById(R.id.plus_sign_out_buttons);
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                Boolean success = false;
                try {

                    // Check if the user exists on the server
                    String url = format("http://10.0.2.2:9000/v1/userByEmailId/%s", currentPersonEmail);
                    RestTemplate template = new RestTemplate();
                    try {
                        currentBackendUser = template.getForObject(url, User.class);
                        Log.v(TAG, format("Retrieved user with email [%s] from backend server", currentPersonEmail));
                    } catch (HttpClientErrorException e) {
                        // 404 is acceptable. The others are not.
                        if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                            currentBackendUser = null;
                        } else {
                            throw e;
                        }
                    }

                    // Get the registration id
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    registrationId = getRegistrationId(getApplicationContext());
                    if (registrationId.isEmpty()) {
                        registrationId = gcm.register(SENDER_ID);

                        // Persist the regID in the app shared preferences
                        storeRegistrationId(context, registrationId);

                        if (currentBackendUser != null) {
                            // The user already exists, check if the device is registered
                            if (!isDeviceRegistered(currentBackendUser, registrationId)) {
                                // register the device with the user.
                                success = sendRegistrationIdToBackend();
                            } else {
                                Log.v(TAG, format("Device already registered with user [%s]", currentPersonEmail));
                            }

                        } else {
                            // create the user with the registration details
                            User newUser = new User(currentPersonEmail, currentPerson.getDisplayName(), false);
                            newUser.addDevice(new Device(registrationId, "Android Device", Device.DevicePlatform.ANDROID));
                            url = "http://10.0.2.2:9000/v1/users";
                            template.setErrorHandler(new DefaultResponseErrorHandler());
                            currentBackendUser = template.postForObject(url, newUser, User.class);
                            success = true;
                        }
                    } else {
                        if (currentBackendUser != null) {
                            success = true;
                        }
                    }

                } catch (Exception ex) {
                    Log.e(TAG, "Unable to register device for GCM", ex);
                    // If there is an error, don't just keep trying to register. Require the user to click a button again, or perform exponential back-off.
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                // forward to new activity
                try {
                    Intent mainActivityIntent = new Intent(context, BuildingsActivity.class);
                    mainActivityIntent.putExtra(CommonConstants.EXTRA_CURRENT_USER, new ObjectMapper().writeValueAsString(currentBackendUser));
                    startActivity(mainActivityIntent);
                } catch (JsonProcessingException e) {
                    Log.e(TAG, "Unable to forward to next activity", e);
                }
            }
        }.execute(null, null, null);
    }

    /**
     * Check if the device is already registered with the user
     *
     * @param fromServer
     * @param registrationId
     * @return
     */
    private boolean isDeviceRegistered(User fromServer, String registrationId) {
        boolean registered = false;
        if (fromServer.getDevices() != null && !fromServer.getDevices().isEmpty()) {
            for (Device device : fromServer.getDevices()) {
                if (registrationId.equals(device.getDeviceId())) {
                    registered = true;
                    break;
                }
            }
        }
        return registered;
    }

    /**
     * Send the registration id to the backend server.
     */
    private boolean sendRegistrationIdToBackend() {
        boolean success = false;
        String url = format("http://10.0.2.2:9000/v1/users/%s/devices/register", Plus.AccountApi.getAccountName(getGoogleApiClient()));
        try {
            // prepare the post body
            Device postBody = new Device(registrationId, "android device", Device.DevicePlatform.ANDROID);
            RestTemplate postTemplate = new RestTemplate();
            User result = postTemplate.postForObject(url, postBody, User.class);
            Log.v(TAG, format("Successfully sent registration details to backend server. User: [%s]", result));
            success = true;
        } catch (Exception ex) {
            Log.e(TAG, "Unable to send the registration id to the backend server", ex);
        }
        return success;
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, format("Saving regId: [%s] on app version: [%s] ", regId, appVersion));
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(LoginActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    @Override
    public void onConnectionSuspended(int i) {
        getGoogleApiClient().connect();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow for very easy animations.
        // If available, use these APIs to fade-in the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onPlusClientSignIn() {

        /**
         * The flow:
         *
         * Initial Check:
         *  - Call the server to check if the user exists
         *  - If the user exists, do the "existing user flow"
         *  - else, do the "New User Flow"
         *
         * New User Flow:
         *  - Sign In
         *  - Set up GCM
         *  - Send to registration id to server
         *  - Pass on to Next Activity
         *
         * Existing User Flow:
         *  - Go on to New Activity
         */

        // get the current user details
        if (Plus.PeopleApi.getCurrentPerson(getGoogleApiClient()) != null) {
            currentPerson = Plus.PeopleApi.getCurrentPerson(getGoogleApiClient());
            currentPersonEmail = Plus.AccountApi.getAccountName(getGoogleApiClient());
        }
        Log.v(TAG, format("Current User: %s", currentPersonEmail));

        // Set up GCM
        registerInBackground();

        //Set up sign out and disconnect buttons.
        Button signOutButton = (Button) findViewById(R.id.plus_sign_out_button);
        signOutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        Button disconnectButton = (Button) findViewById(R.id.plus_disconnect_button);
        disconnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                revokeAccess();
            }
        });
    }

    @Override
    protected void onPlusClientBlockingUI(boolean show) {
        showProgress(show);
    }

    @Override
    protected void updateConnectButtonState() {
        boolean connected = getGoogleApiClient().isConnected();
        mSignOutButtons.setVisibility(connected ? View.VISIBLE : View.GONE);
        mPlusSignInButton.setVisibility(connected ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onPlusClientRevokeAccess() {
        // TODO: Access to the user's G+ account has been revoked.  Per the developer terms, delete any stored user data here.
        Log.v(TAG, "TODO: Need to clear out the user's data on our application");
    }

    @Override
    protected void onPlusClientSignOut() {
        Log.v(TAG, "TODO: logic for signing out the user.");
    }

    /**
     * Check if the device supports Google Play Services.  It's best practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
    }

}

