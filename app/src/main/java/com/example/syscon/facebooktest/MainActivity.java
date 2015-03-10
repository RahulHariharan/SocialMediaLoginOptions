package com.example.syscon.facebooktest;

import android.content.IntentSender;
import android.hardware.usb.UsbInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.util.Arrays;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.*;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "B55FoU5FikKUcXexwU5O9dehX";
    private static final String TWITTER_SECRET = "SNa6tmCW27V4woumdijGH7KVotzdMsIsj0NJurz9goPk5Kd6uv";
    private TwitterLoginButton loginButton;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    private boolean mIntentInProgress;
    // sign in with Google Plus  button
    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;

    private UiLifecycleHelper uiHelper;
    private GraphUser user;

    private Session.StatusCallback callback = new Session.StatusCallback(){

        @Override
        public void call(Session session, SessionState sessionState, Exception e) {

            if(session != null)
                Log.v("Session",session.toString());
            else if(sessionState != null)
                Log.v("Session state", sessionState.toString());
            if(e != null)
                e.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        Log.v("on create","on create method");

        uiHelper = new UiLifecycleHelper(this,callback);
        uiHelper.onCreate(savedInstanceState);

        LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
        authButton.setReadPermissions(Arrays.asList("public_profile"));

        authButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {

            @Override
            public void onUserInfoFetched(GraphUser graphUser) {

                Session session = Session.getActiveSession();

                if(session != null)
                    Log.v("MySession", session.toString());

                if(graphUser != null) {
                    MainActivity.this.user = graphUser;

                    Log.v("Session Build", graphUser.toString());
                    Log.v("Graph user",graphUser.toString());

                    Intent intent = new Intent(getApplicationContext(),SecondActivity.class);

                    JSONObject jsonObject = new JSONObject(graphUser.asMap());
                    Log.v("JSONObject",jsonObject.toString());

                    String stringifiedJSON = jsonObject.toString();
                    try {
                        JSONObject reconstructedObject = new JSONObject(stringifiedJSON);
                        Log.v("Reconstructed JSON",reconstructedObject.toString());
                    }
                    catch(JSONException jsonException){

                        jsonException.printStackTrace();
                    }


                    intent.putExtra("StringifiedData",stringifiedJSON);


                    startActivity(intent);
                }
            }
        });

        /*loginButton = (TwitterLoginButton)
                findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a
                // TwitterSession for making API calls
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });*/

        /*this.mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                                    .addApi(Plus.API)
                                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks(){

                                        @Override
                                        public void onConnected(Bundle bundle) {

                                            mSignInClicked = false;
                                            Toast.makeText(getApplicationContext(), "User is connected!", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getApplicationContext(),SecondActivity.class);
                                            startActivity(intent);

                                        }

                                        @Override
                                        public void onConnectionSuspended(int i) {
                                            mGoogleApiClient.connect();
                                        }
                                    })
                                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener(){

                                        @Override
                                        public void onConnectionFailed(ConnectionResult result) {

                                            if (!mIntentInProgress && result.hasResolution()) {
                                                try {
                                                    mIntentInProgress = true;
                                                    startIntentSenderForResult(result.getResolution().getIntentSender(),
                                                            RC_SIGN_IN, null, 0, 0, 0);
                                                    mConnectionResult = result;

                                                    if(mSignInClicked){
                                                        resolveSignInError();
                                                    }

                                                } catch (IntentSender.SendIntentException e) {
                                                    // The intent was canceled before it was sent.  Return to the default
                                                    // state and attempt to connect to get an updated ConnectionResult.
                                                    mIntentInProgress = false;
                                                    mGoogleApiClient.connect();
                                                }
                                            }
                                        }
                                    })
                                    .build();*/

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if (view.getId() == R.id.sign_in_button
                        && !mGoogleApiClient.isConnecting()) {
                    mSignInClicked = true;
                    resolveSignInError();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        uiHelper.onPause();
        // Logs an app deactivation event
        AppEventsLogger.deactivateApp(this);

        // this app stores data in crash analytics
        // another way this can happen is because its
        // way too awesome

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode,resultCode,data);
        // Pass the activity result to the login button.
        //loginButton.onActivityResult(requestCode, resultCode,data);

        /*if (requestCode == RC_SIGN_IN) {

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }*/

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    /*********************** FUNCTIONS NEED TO BE UNCOMMENTED FOR USE **********************/
    /*protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }*/

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

}
