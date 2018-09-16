package models;

import akka.actor.ActorRef;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import twitter4j.*;
import twitter4j.conf.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FakeTwitter implements TweetMiner {
    public static RealTwitter realTwitter = new RealTwitter();
    private ActorRef ws;
    private HashMap<String, ArrayList<String>> dummySearchResultsofTweets = new HashMap<>();

    HashtagEntity hashtagEntity1 = new HashtagEntity() {
        @Override
        public String getText() {
            return "hashtag 1";
        }

        @Override
        public int getStart() {
            return 0;
        }

        @Override
        public int getEnd() {
            return 0;
        }
    };

    HashtagEntity hashtagEntity2 = new HashtagEntity() {
        @Override
        public String getText() {
            return "hashtag 2";
        }

        @Override
        public int getStart() {
            return 0;
        }

        @Override
        public int getEnd() {
            return 0;
        }
    };

    @Override
    public void initActor(ActorRef ws) {
        this.ws=ws;
        realTwitter.initActor(ws);
    }

    @Override
    public void initSearchTerms(ArrayList<String> searchTerms) {
        realTwitter.initSearchTerms(searchTerms);
    }

    @Override
    public void searchTweets(ActorRef caller,String topic) throws JsonProcessingException {
        searchLiveFeed(caller,topic,new ArrayList<>());
    }

    @Override
    public void searchLiveFeed(ActorRef caller, String topic, List<Details> detailsList) throws JsonProcessingException {

//        System.out.println("Live feed fake");
//        Details d= new Details("dummy user","dummy_screenName","This is dummy tweet",1,new HashtagEntity[]{hashtagEntity1,hashtagEntity2},"montreal",12.2,23.11,topic);
//        ObjectNode tweetData = Json.newObject();
//        updateMap(topic,"This is dummy tweet");
//        tweetData.put("topic",topic);
//        tweetData.put("tweet",new ObjectMapper().writeValueAsString(d));
//        System.out.println(new ObjectMapper().writeValueAsString(d));
//        ws.tell(tweetData,caller);
        ArrayList<Details> tempList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tempList.add(new Details("dummy user", "dummy_screenName",
                    "This is dummy tweet for " + topic, 1,
                    new HashtagEntity[]{hashtagEntity1, hashtagEntity2},
                    "montreal",
                    12.2, 23.11, topic));

        }
        ObjectNode objectNode = Json.newObject();
        objectNode.put("topic", topic);
        objectNode.put("tweet", new ObjectMapper().writeValueAsString(tempList));
        ws.tell(objectNode, caller);

    }

    public void updateMap(String key, String value) {
        realTwitter.updateMap(key, value);
    }

    @Override
    public void clearTwitterStream() {
        realTwitter.clearTwitterStream();
    }

    @Override
    public CompletableFuture<ObjectNode> showUser(String screenName) {
        System.out.println(screenName);
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode objectNodes = Json.newObject();
                ArrayList<String> tweets = new ArrayList<>();
                tweets.add("this is dummy tweet");
                tweets.add("this is dummy tweet");
                UserProfileDetails upd = new UserProfileDetails("dummy user", "dummy_screenname",
                        tweets, 2,
                        "montreal",
                        "dummy description");
                objectNodes.put("screenName", new ObjectMapper().writeValueAsString(upd));
                return objectNodes;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<ObjectNode> searchTweetsByLocation(String latitude, String longitude, String locationName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ObjectNode objectNode = Json.newObject();
                ArrayList<String> tweets = new ArrayList<>();
                tweets.add("dummy location tweet");
                tweets.add("dummy location tweet");
                LocationDetails lpd = new LocationDetails(latitude, longitude, locationName, tweets);
                objectNode.put("location", new ObjectMapper().writeValueAsString(lpd));

                return objectNode;
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
                ObjectNode objectNode = Json.newObject();
                ArrayList<String> tweets = new ArrayList<>();
                tweets.add("dummy hashtag tweet");
                tweets.add("dummy hashtag tweet");
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
    public CompletableFuture<ObjectNode> calculateStatistics(String topic, List<String> tweets) {
        System.out.println("tweets array " + tweets);
        return realTwitter.calculateStatistics(topic, tweets);
    }

    @Override
    public List<String> returnTweets(String topic) {
        ArrayList<String> al = new ArrayList<>();
        al.add("This is tweet");
        al.add("This is tweet");
        realTwitter.updateMap("modi", "This is tweet");
        realTwitter.updateMap("modi", "This is tweet");
        return realTwitter.returnTweets(topic);
    }

    @Override
    public String analyzeSentiments(String sentimentTopic) {
        if (sentimentTopic.equals("modi")) {
            for (int i = 0; i < 10; i++) {
                realTwitter.updateMap("modi", "this is tweet :-)");
            }
        }
        if (sentimentTopic.equals("obama")) {
            for (int i = 0; i < 10; i++) {
                realTwitter.updateMap("obama", "this is tweet :-(");
            }
        }

        if (sentimentTopic.equals("trump")) {
            for (int i = 0; i < 5; i++) {
                realTwitter.updateMap("trump", "this is tweet :-(");
                realTwitter.updateMap("trump", "this is tweet :-)");

            }
        }
        return realTwitter.analyzeSentiments(sentimentTopic);
    }

}
