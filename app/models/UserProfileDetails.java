package models;

import java.util.ArrayList;

public class UserProfileDetails {
    public String username;
    public String screenName;
    public ArrayList<String> tweets;
    public int followersCount;
    public String location;
    public String description;

    public UserProfileDetails(String username, String screenName, ArrayList<String> tweets, int followersCount, String location, String description) {
        this.username = username;
        this.screenName = screenName;
        this.tweets = tweets;
        this.followersCount = followersCount;
        this.location = location;
        this.description = description;
    }
}
