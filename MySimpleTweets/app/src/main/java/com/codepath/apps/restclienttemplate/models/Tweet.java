package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Tweet {

    // list out the attributes
    public String body;
    public long uid; // database ID for the tweet
    public User user;
    public String createdAt;
    public int favorite_count;
    public int retweet_count;
    public int reply_count;

    // deserialize the JSON
    // create a method to take in a JSONObject and give back a Tweet Object
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException{
        Tweet tweet = new Tweet();
        ParseRelativeData parse = new ParseRelativeData();

        // extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = parse.getRelativeTimeAgo(jsonObject.getString("created_at"));
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.favorite_count = jsonObject.getInt("favorite_count");
        tweet.retweet_count = jsonObject.getInt("retweet_count");
//        tweet.reply_count = jsonObject.getInt("reply_count");

        return tweet;
    }

    public long getUid() {
        return uid;
    }

    public String getBody() {
        return body;
    }

    public User getUser() {
        return user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Integer getFavorite_count() {
        return favorite_count;
    }

    public int getRetweet_count() {
        return retweet_count;
    }

    public int getReply_count() {
        return reply_count;
    }
}
