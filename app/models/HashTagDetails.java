package models;

import java.util.ArrayList;

public class HashTagDetails {

    public String tag;
    public ArrayList<String> tweets;

    public HashTagDetails(String tag, ArrayList<String> tweets) {
        this.tag = tag;
        this.tweets = tweets;
    }

}
