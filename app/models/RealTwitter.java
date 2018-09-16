package models;

import akka.actor.ActorRef;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class RealTwitter implements TweetMiner {

    private ActorRef ws;
    TwitterStream twitterStream;
    Twitter twitter;
    ArrayList<String> searchTerms;
    Configuration configuration;
    public HashMap<String, List<String>> searchResultsOfTweets = new LinkedHashMap<>();
    public HashMap<String, List<Details>> tweetDetails= new LinkedHashMap<>();


    RealTwitter() {
        ConfigurationBuilder cf = new ConfigurationBuilder();
        cf.setDebugEnabled(true).setOAuthConsumerKey("Gkth7i2tEjADOeMh1eg9GSM6c").setOAuthConsumerSecret("ZjkVWqpuLmVA6lDd70KtY4115aONH8SyfHrlOYbfffJp85hj4a")
                .setOAuthAccessToken("1021898405029847040-JNPu1IXByi70qAc9CvR0HJyVw4f2qh").setOAuthAccessTokenSecret("s2spDv48YW2q5gEu0bGNzycMs5s09KASnTWzbwCShtBlU");
        configuration = cf.build();
        twitterStream = new TwitterStreamFactory(configuration).getInstance();
        twitter = new TwitterFactory(configuration).getInstance();
    }

    public void updateStatusDetailsMap(String topic, Status s){
        System.out.println(tweetDetails.containsKey(topic));
        if(tweetDetails.containsKey(topic))
        {
            if(tweetDetails.get(topic).size()>=10){
                tweetDetails.get(topic).remove(0);
            }
        }

        if(tweetDetails.containsKey(topic)){
            if(s.getPlace()==null){
                List<Details> list=tweetDetails.get(topic);
                list.add(new Details(s.getUser().getName(),s.getUser().getScreenName(),s.getText(),
                        s.getUser().getFollowersCount(),s.getHashtagEntities(),"",0,0,topic));
                tweetDetails.put(topic,list);
                System.out.println(tweetDetails.keySet());
            }
            else
            {
                Details d=new Details(s.getUser().getName(),s.getUser().getScreenName(),s.getText(),
                        s.getUser().getFollowersCount(),s.getHashtagEntities(),s.getPlace().getFullName(),
                        s.getPlace().getBoundingBoxCoordinates()[0][0].getLatitude(),
                        s.getPlace().getBoundingBoxCoordinates()[0][0].getLongitude(),topic);
                ArrayList<Details> al= new ArrayList<>();
                al.add(d);
                tweetDetails.put(topic,al);
            }
        }
        else
        {
            if(s.getPlace()==null){
                Details d= new Details(s.getUser().getName(),s.getUser().getScreenName(),s.getText(),
                        s.getUser().getFollowersCount(),s.getHashtagEntities(),"",0,0,topic);
                ArrayList<Details>  list= new ArrayList<>();
                list.add(d);
                tweetDetails.put(topic,list);
            }
            else
            {
                Details d=new Details(s.getUser().getName(),s.getUser().getScreenName(),s.getText(),
                        s.getUser().getFollowersCount(),s.getHashtagEntities(),s.getPlace().getFullName(),
                        s.getPlace().getBoundingBoxCoordinates()[0][0].getLatitude(),
                        s.getPlace().getBoundingBoxCoordinates()[0][0].getLongitude(),topic);
                ArrayList<Details> al= new ArrayList<>();
                al.add(d);
                tweetDetails.put(topic,al);
            }

        }
    }

    public void updateMap(String key, String value) {
        if (searchResultsOfTweets.containsKey(key)) {
            searchResultsOfTweets.get(key).add(value);
        } else {
            ArrayList<String> temp = new ArrayList<>();
            temp.add(value);
            this.searchResultsOfTweets.put(key, temp);
        }
    }

    public void clearTwitterStream() {
        twitterStream.cleanUp();
    }

    public void initActor(ActorRef ws) {
        this.ws = ws;
    }

    public void initSearchTerms(ArrayList<String> searchTerms) {
        this.searchTerms = searchTerms;
    }

    public void searchTweets(ActorRef caller,String topic){
        System.out.println("search twwets for "+topic);
        Query query = new Query(topic);
        query.setCount(10);
        try {
            QueryResult result= twitter.search(query);
            CompletableFuture.supplyAsync(() ->{
                List<Details> tweetData=new ArrayList<>();
                List<Status> statuses= result.getTweets();
                statuses.stream()
                        .map(s -> {

                            updateMap(topic,s.getText());
                            System.out.println("After upadte <map");
                            updateStatusDetailsMap(topic,s);
                            System.out.println("After Status Initial map");
                            if(s.getPlace()==null){
                                System.out.println("inside if");
                                tweetData.add(new Details(s.getUser().getName(),s.getUser().getScreenName(),s.getText(),
                                        s.getUser().getFollowersCount(),s.getHashtagEntities(),"",0,0,topic));
                            }
                            else
                            {
                                System.out.println("inside else");
                                tweetData.add(new Details(s.getUser().getName(),s.getUser().getScreenName(),s.getText(),
                                        s.getUser().getFollowersCount(),s.getHashtagEntities(),s.getPlace().getFullName(),
                                        s.getPlace().getBoundingBoxCoordinates()[0][0].getLatitude(),
                                        s.getPlace().getBoundingBoxCoordinates()[0][0].getLongitude(),topic));
                            }
                            System.out.println(tweetData);
                            return tweetData;
                        }).collect(Collectors.toList());
                System.out.println("done mapping");
                ObjectNode objectNode = Json.newObject();
                objectNode.put("topic",topic);
                try {
                    objectNode.put("tweet",new ObjectMapper().writeValueAsString(tweetData));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                searchLiveFeed(caller,topic,tweetData);
                ws.tell(objectNode,caller);
                return tweetData;
            });
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void searchLiveFeed(ActorRef caller, String topic,List<Details> detailsList) {
        System.out.println("Search live feeds");
        System.out.println(searchTerms);
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                String username = status.getUser().getName();
                String screenName = status.getUser().getScreenName();
                String tweet = status.getText();
                int followersCount = status.getUser().getFollowersCount();
                HashtagEntity[] hashtagEntities = status.getHashtagEntities();
                Details d;
                String updatedTopic=null;
                for(String s:searchTerms){
                    if(status.getText().contains(s)){
                        System.out.println("topic is "+s);
                        updatedTopic=s;
                        break;
                    }
                }
                if(status.getPlace()==null){
//                    System.out.println("place is null");
                    d= new Details(username,screenName,tweet,followersCount,hashtagEntities,"",0,0,updatedTopic);
                }
                else {
//                    System.out.println("place not null");
                    d= new Details(username,screenName,tweet,followersCount,hashtagEntities,
                            status.getPlace().getFullName(),
                            status.getPlace().getBoundingBoxCoordinates()[0][0].getLatitude(),
                            status.getPlace().getBoundingBoxCoordinates()[0][0].getLongitude(),
                            updatedTopic);
                }
                List<Details> tempDetailsList= tweetDetails.get(updatedTopic);
                //System.out.println(tweetDetails.get(updatedTopic));
                if(tempDetailsList.size()>=10)
                    tempDetailsList.remove(0);
                tempDetailsList.add(d);
                updateMap(updatedTopic,status.getText());
                tweetDetails.put(updatedTopic,tempDetailsList);
                System.out.println(tweetDetails.get(updatedTopic).get(0).tweet);
                ObjectNode objectNode = Json.newObject();
                try {
//                    System.out.println(new ObjectMapper().writeValueAsString(tempDetailsList));
                    objectNode.put("topic", updatedTopic);
                    objectNode.put("tweet", new ObjectMapper().writeValueAsString(tweetDetails.get(updatedTopic)));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                System.out.println("Tell method called");
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ws.tell(objectNode, caller);
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int i) {

            }

            @Override
            public void onScrubGeo(long l, long l1) {

            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {

            }

            @Override
            public void onException(Exception e) {

            }

        };
        twitterStream.addListener(listener);
        FilterQuery tweetFilterQuery = new FilterQuery();

        System.out.println(searchTerms.toArray(new String[searchTerms.size()]));
        tweetFilterQuery.track(searchTerms.toArray(new String[searchTerms.size()]));
        twitterStream.filter(tweetFilterQuery);
    }

    public CompletableFuture<ObjectNode> showUser(String screenName) {
        System.out.println(screenName);
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Status> statusList = twitter.getUserTimeline(screenName);
                ObjectNode objectNodes = Json.newObject();
                System.out.println("Object node null" + objectNodes == null);
                ArrayList<String> tweets = new ArrayList<>();
                statusList.stream().forEach(status -> tweets.add(status.getText()));
                UserProfileDetails upd = new UserProfileDetails(statusList.get(0).getUser().getName(), statusList.get(0).getUser().getScreenName(),
                        tweets, statusList.get(0).getUser().getFollowersCount(),
                        statusList.get(0).getUser().getLocation(),
                        statusList.get(0).getUser().getDescription());
                objectNodes.put("screenName", new ObjectMapper().writeValueAsString(upd));
                return objectNodes;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }


    public CompletableFuture<ObjectNode> searchTweetsByLocation(String latitude, String longitude, String locationName) {
        System.out.println("Latitude " + latitude + " Longitude" + longitude + " Location" + locationName);
        return CompletableFuture.supplyAsync(() -> {
            try {
                String geocodeString = "geocode:" + latitude + "," + longitude + ",50km";
                Query query = new Query(geocodeString);
                query.setCount(10);
                QueryResult result = twitter.search(query);
                List<Status> statusObjects = result.getTweets();
                ObjectNode objectNodes = Json.newObject();
                int numberOfTweets = statusObjects.size() >= 10 ? 10 : statusObjects.size();
                ArrayList<String> tweets = new ArrayList<>();
                statusObjects.stream()
                        .forEach(s -> tweets.add(s.getText()));
                LocationDetails lpd = new LocationDetails(latitude, longitude, locationName, tweets);
                objectNodes.put("location", new ObjectMapper().writeValueAsString(lpd));
                return objectNodes;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

    }

    @Override
    public CompletableFuture<ObjectNode> searchTweetsByHashTag(String tag) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Query query = new Query(tag);
                query.setCount(10);
                QueryResult result = twitter.search(query);
                List<Status> statusObjects = result.getTweets();
                ArrayList<String> tweets = new ArrayList<>();
                statusObjects.stream()
                        .forEach(s -> tweets.add(s.getText()));
                ObjectNode objectNode = Json.newObject();
                HashTagDetails htd = new HashTagDetails(tag, tweets);
                objectNode.put("hashtag", new ObjectMapper().writeValueAsString(htd));
                return objectNode;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public List<String> returnTweets(String topic) {
        return
        searchResultsOfTweets.get(topic);
    }

    @Override
    public CompletableFuture<ObjectNode> calculateStatistics(String topic, List<String> tweets) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HashMap<String, Integer> wordCount = new HashMap<>();
                tweets.stream()
                        .map(s -> {
                            String words[] = s.split(" ");
                            Arrays.stream(words)
                                    .map(word -> {
                                        if (wordCount.containsKey(word)) {
                                            int value = wordCount.get(word);
                                            value++;
                                            wordCount.put(word, value);
                                        } else {
                                            wordCount.put(word, 1);
                                        }
                                        return wordCount;
                                    }).collect(Collectors.toList());
                            return wordCount;
                        })
                        .collect(Collectors.toList());
                ObjectNode objectNode = Json.newObject();
                objectNode.put("stats", new ObjectMapper().writeValueAsString(wordCount));
                return objectNode;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public String analyzeSentiments(String sentimentTopic)  {

        String sentiment=null;

        List<String> sentimentData = returnTweets(sentimentTopic);

        int count=0;
        List<String> happyTweets = sentimentData.stream()
                .filter(d -> d.contains(":-)"))
                .collect(Collectors.toList());

        List<String> sadTweets = sentimentData.stream()
                .filter(d -> d.contains(":-("))
                .collect(Collectors.toList());
        sentiment= (happyTweets.size() / sentimentData.size() >= 0.7) ? ":-)" : ((sadTweets.size() / sentimentData.size() >= 0.7)? ":-(":":-|");
        return sentiment;
    }

}
