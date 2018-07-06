package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ResponseActivity extends AppCompatActivity {

    TwitterClient client;
    Tweet tweet;
    TextView etTypeResponse;
    long id;
    public final static String TAG = "ComposeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        etTypeResponse = (EditText) findViewById(R.id.etTypeResponse);
        String strValue = etTypeResponse.getText().toString();
        client = TwitterApp.getRestClient(this);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        Log.d("ResponseActivity", String.format("Showing details for '%s'", tweet.getClass()));
        id = tweet.uid;
        etTypeResponse.setText(tweet.user.screenName);
    }

    public void onSubmitResponse(View v) {
        // closes the activity and returns to first screen

        client.replyTweet(etTypeResponse.getText().toString(), id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Tweet tweet = Tweet.fromJSON(response);
                    Intent data = new Intent();
                    data.putExtra("tweet", Parcels.wrap(tweet));
                    data.putExtra("request_code", 8); // ints work too
                    // Activity finished ok, return the data
                    setResult(RESULT_OK, data); // set result code and bundle data for response
                    finish();
                } catch (JSONException e) {
                    logError("Failed to load update", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse) {
                throwable.printStackTrace();
            }
        });
    }

    // handle errors, log and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        // always log the error
        Log.e(TAG, message, error);
        // alert the user to avoid silent errors
        if (alertUser) {
            // show a long toast with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
