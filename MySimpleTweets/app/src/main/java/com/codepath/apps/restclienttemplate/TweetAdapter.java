package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.GlideApp;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.ComposeActivity.TAG;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    Context context;
    TwitterClient client;
    boolean favorited;
    boolean retweeted;

    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;

    }

    // for each row, inflate the layout and cache (pass) references into ViewHolder

    @NonNull
    @Override
    // this method is only invoked when you need to create a new row
    // onCreateViewHolder inflates the layout and caches all the findViewById lookups
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate the layout you created
        // need to first get context
        context = parent.getContext();
        // get the layout using the context object
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate item_tweet
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }


    // Or else will call on onBindViewHolder
    // bind the values based on the position of the element
    // passes a position and the previously cached ViewHolder and repopulate the data based on the position of that element

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the data according to position
        Tweet tweet = mTweets.get(position);

        // populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvID.setText(tweet.user.screenName);
        holder.tvTimeStamp.setText(tweet.createdAt);
        holder.tvFavCount.setText(tweet.favorite_count+"");
        holder.tvRetweetCount.setText(tweet.retweet_count+"");
        holder.tvComCount.setText(tweet.reply_count+"");

        // TODO: profile image using Glide
        GlideApp.with(context)
                .load(tweet.user.profileImageUrl)
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // create ViewHolder class

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvID;
        public TextView tvTimeStamp;
        public ImageButton ibComment;
        public ImageButton ibRetweet;
        public ImageButton ibHeart;
        public TextView tvFavCount;
        public TextView tvComCount;
        public TextView tvRetweetCount;

        public ViewHolder (View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            // perform findViewById lookups

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvID = (TextView) itemView.findViewById(R.id.tvID);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            ibComment = (ImageButton) itemView.findViewById(R.id.ibComment);
            ibRetweet = (ImageButton) itemView.findViewById(R.id.ibRetweet);
            ibHeart = (ImageButton) itemView.findViewById(R.id.ibHeart);

            tvFavCount = (TextView) itemView.findViewById(R.id.tvFavCount);
            tvComCount = (TextView) itemView.findViewById(R.id.tvComCount);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tvRetweetCount);



            ibComment.setOnClickListener(this);
            ibRetweet.setOnClickListener(this);
            ibHeart.setOnClickListener(this);
//            tvLikes = (TextView) itemView.findViewById(R.id.tvLikes);
//            tvRetweets = (TextView) itemView.findViewById(R.id.tvRetweets);
//            tvComment = (TextView) itemView.findViewById(R.id.tvComment);

        }

        @Override
        public void onClick(View view) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {

                // REPLY TWEET
                if (view.getId() == R.id.ibComment) {
                    Tweet tweet = mTweets.get(position);
                    // create intent for the new activity
                    Intent intent = new Intent(context, ResponseActivity.class);
                    // serialize the movie using parceler, use its short name as a key
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    // show the activity
                    ((Activity) context).startActivityForResult(intent, 2);
                    tvComCount.setText(tweet.getReply_count()+"");
                }

                // RETWEET and UN-RETWEET
                else if (view.getId() == R.id.ibRetweet) {
                    client = TwitterApp.getRestClient(context);

                    Tweet tweet = mTweets.get(position);
                    long id = tweet.uid;
                    retweeted = tweet.isRetweeted();

                    if (retweeted == false) {
                        client.reTweet(id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                try {
                                    Tweet tweet = Tweet.fromJSON(response);
                                    mTweets.add(tweet);
                                    notifyDataSetChanged();
                                    tvRetweetCount.setText(tweet.getRetweet_count() + "");
                                    retweeted = tweet.isRetweeted();
                                } catch (JSONException e) {
                                    logError("Failed to load update", e, true);
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                throwable.printStackTrace();
                            }
                        });
                    }
                    else {
                        client.unReTweet(id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                try {
                                    Tweet tweet = Tweet.fromJSON(response);
                                    tvRetweetCount.setText(tweet.getRetweet_count() + "");
                                    retweeted = tweet.isRetweeted();
                                    //System.out.println("successfully unretweeted");
                                } catch (JSONException e) {
                                    logError("Failed to load update", e, true);
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                throwable.printStackTrace();
                            }
                        });
                    }
                }

                // FAVORITE
                else if (view.getId() == R.id.ibHeart) {
                    if (client == null) {
                        client = TwitterApp.getRestClient(context);
                    }
                    Tweet tweet = mTweets.get(position);
                    long id = tweet.uid;
                    favorited = tweet.isFavorited();
                    if (favorited == false) {
                        client.favoriteTweet(id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                try {
                                    Tweet tweet = Tweet.fromJSON(response);
                                    tvFavCount.setText(tweet.getFavorite_count() + "");
                                    favorited = tweet.isFavorited();
                                    // TODO: Change image color
                                    // ibHeart.setImageResource(R.drawable.heart);
                                } catch (JSONException e) {
                                    logError("Failed to load update", e, true);
                                }
                            }


                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                throwable.printStackTrace();
                            }
                        });
                    }

                    else {
                        client.unFavorite(id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                try {
                                    Tweet tweet = Tweet.fromJSON(response);
                                    tvFavCount.setText(tweet.getFavorite_count() + "");
                                    // TODO: Change image color
                                    favorited = tweet.isFavorited();
//                                 ibHeart.setImageResource(R.drawable.heart);
                                } catch (JSONException e) {
                                    logError("Failed to load update", e, true);
                                }
                            }


                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                throwable.printStackTrace();
                            }
                        });
                    }
                }
                else {
                    // get the movie at the position, this won't work if the class is static
                    Tweet tweet = mTweets.get(position);
                    // create intent for the new activity
                    Intent intent = new Intent(context, DetailActivity.class);
                    // serialize the movie using parceler, use its short name as a key
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    // show the activity
                    context.startActivity(intent);
                }
            }
        }
    }


    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    // handle errors, log and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        // always log the error
        Log.e(TAG, message, error);
        // alert the user to avoid silent errors
        if (alertUser) {
            // show a long toast with the error message
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

}
