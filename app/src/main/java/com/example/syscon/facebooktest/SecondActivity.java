package com.example.syscon.facebooktest;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.gms.plus.PlusShare;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import com.facebook.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;


public class SecondActivity extends FragmentActivity {

    String data;
    JSONObject jsonObject;
    TextView textView;
    ProfilePictureView profilePicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        GridView gridView = (GridView)findViewById(R.id.grid_view);
        ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext());
        gridView.setAdapter(imageAdapter);

        String pack = getIntent().getPackage();
        if(pack != null )
            Log.v("This is a mighty pack", pack);

        String stringifiedData = getIntent().getStringExtra("StringifiedData");
        Log.v("Stringified Data",stringifiedData.toString());

        profilePicture = (ProfilePictureView)findViewById(R.id.selection_profile_pic);

        try{

            JSONObject jsonObject = new JSONObject(stringifiedData);
            if(jsonObject.getString("first_name") != null){

                textView = (TextView)findViewById(R.id.user_name);
                textView.setText("Welcome, "+jsonObject.getString("first_name"));

                profilePicture.setProfileId(jsonObject.getString("id"));

            }

        }
        catch(JSONException jsonException){

             jsonException.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);
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
    public void tweetMe(View view){

        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text("Just used the best wedding share app on Android !");

        builder.show();

    }

    public void shareOnGooglePlus(View view){

        Intent shareIntent = new PlusShare.Builder(this)
                .setType("text/plain")
                .setText("Welcome to the Google+ platform.")
                .setContentUrl(Uri.parse("https://developers.google.com/+/"))
                .getIntent();

        startActivityForResult(shareIntent, 0);

    }
}
