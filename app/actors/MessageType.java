package actors;

public class MessageType {

    public static class SearchTweets{
        public String topic;

        public SearchTweets(String topic) {
            this.topic = topic;
        }
    }

    public static class UserInfo{
        public String screenName;

        public UserInfo(String screenName) {
            this.screenName = screenName;
        }
    }

    public static class Location{
        public String latitude;
        public String longitude;
        public String locationName;

        public Location(String latitude, String longitude, String locationName) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.locationName = locationName;
        }
    }
    public static class Register{

    }

    public static class Stat{
        public String topic;

        public Stat(String topic) {
            this.topic = topic;
        }
    }
    public static class Hashtag{
        public String hashtag;

        public Hashtag(String hashtag) {
            this.hashtag = hashtag;
        }
    }
}
