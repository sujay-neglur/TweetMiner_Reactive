package models;

import java.util.ArrayList;

public class LocationDetails {
    public String latitude;
    public String longitude;
    public String locationName;
    public ArrayList<String> tweets;
    public LocationDetails(String latitude, String longitude, String locationName,ArrayList<String> tweets) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.tweets=tweets;
    }
}
