package models;

import twitter4j.HashtagEntity;

public class Details {
    public String username;
    public String screenName;
    public String tweet;
    public int followersCount;
    public HashtagEntity [] hashtagEntities;
    public String location;
    public double latitude;
    public double longitude;
    public String topic;

    public Details(String username, String screenName, String tweet, int followersCount, HashtagEntity[] hashtagEntities, String location, double latitude, double longitude,String topic) {
        this.username = username;
        this.screenName = screenName;
        this.tweet = tweet;
        this.followersCount = followersCount;
        this.hashtagEntities = hashtagEntities;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.topic=topic;
    }
}