package models;

import akka.actor.ActorRef;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TweetMiner {

    void initActor(ActorRef ws);
    void initSearchTerms(ArrayList<String> searchTerms);
    void searchLiveFeed(ActorRef caller, String topic, List<Details> detailsList) throws JsonProcessingException;
    void clearTwitterStream();
    CompletableFuture<ObjectNode> showUser(String screenName);
    CompletableFuture<ObjectNode> searchTweetsByLocation(String latitude, String longitude, String locationName);
    CompletableFuture<ObjectNode> searchTweetsByHashTag(String tag);
    CompletableFuture<ObjectNode> calculateStatistics(String topic,List<String> tweets);
    List<String> returnTweets(String topic);
    String analyzeSentiments(String sentimentTopic);
    void searchTweets(ActorRef caller,String topic) throws JsonProcessingException;


}
