package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.GlideApp;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    private TwitterClient client;
    Tweet tweet;
    User user;

    // the view objects
    // Automatically finds each field by the specified ID.
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvID) TextView tvID;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvTimeStamp) TextView tvTimeStamp;
    @BindView(R.id.tvLikes) TextView tvLikes;
    @BindView(R.id.tvRetweets) TextView tvRetweets;
    @BindView(R.id.tvComment) TextView tvComment;
    @BindView(R.id.ibComment)
    AppCompatImageButton ibComment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        // unwrap the movie passed in via intent, using its simple name as a key
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        user = tweet.user;
        Log.d("DetailActivity", String.format("Showing details for '%s'", tweet.getUid()));

        ibComment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ResponseActivity.class);
                    // serialize the movie using parceler, use its short name as a key
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    // show the activity
                    getApplicationContext().startActivity(intent);
                }
        });



        // get access to TwitterClient client
        //client = TwitterApp.getRestClient(getApplicationContext());

        // set the text fields using getters
        tvUserName.setText(user.getName());
        tvID.setText(user.getScreenName());
        tvBody.setText(tweet.getBody());
        tvTimeStamp.setText(tweet.getCreatedAt());
//        tvLikes.setText(tweet.getFavorite_count());
//        tvRetweets.setText(tweet.getRetweet_count());
//        tvComment.setText(tweet.getReply_count());


//      build url for poster image
        String imageUrl = user.getProfileImageUrl();

      // load image using glide
        GlideApp.with(this)
                .load(imageUrl)
               .into(ivProfileImage);

    }
}
