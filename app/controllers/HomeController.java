package controllers;

import actors.*;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.Materializer;
import akka.util.Timeout;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.TweetMiner;
import play.libs.streams.ActorFlow;
import play.mvc.*;

import views.html.*;

import static akka.pattern.PatternsCS.ask;

import javax.inject.Inject;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public ActorSystem actorSystem;
    Materializer materializer;
    public ActorRef intermediateActor;
    public ActorRef userProfileActor;
    public ActorRef locationActor;
    public ActorRef hashtagActor;
    public ActorRef statisticsActor;
    TweetMiner tf;

    @Inject
    public HomeController(ActorSystem actorSystem, Materializer materializer, TweetMiner tf) {
        System.out.println("Home controller constructor");
        this.actorSystem = actorSystem;
        this.materializer = materializer;
        intermediateActor = actorSystem.actorOf(Props.create(IntermediateActor.class, actorSystem, materializer), "intermediateActor");
        locationActor = actorSystem.actorOf(LocationActor.props(tf), "locationActor");
        userProfileActor = actorSystem.actorOf(UserProfileActor.props(tf), "userProfileActor");
        hashtagActor= actorSystem.actorOf(HashtagActor.props(tf),"hashtagActor");
        statisticsActor=actorSystem.actorOf(StatisticsActor.props(tf),"statisticsActor");
        this.tf = tf;
    }

    public Result updateFeed(String topic) {
        System.out.println("Update feed method " + topic);
        intermediateActor.tell(new MessageType.SearchTweets(topic), ActorRef.noSender());
        return ok();
    }

    public Result showLocation(String latitude, String longitude,String location){
        return ok(locationtweets.render(latitude,longitude,location));
    }

    public Result showHashTag(String tag){
        return ok(hashtags.render(tag));
    }

    public CompletionStage<Result> getTweetsByHashtag(String tag) throws ExecutionException, InterruptedException {
        System.out.println("Show tweets by hashtag method");
        Timeout timeout= new Timeout(10,TimeUnit.SECONDS);
        ObjectMapper objectMapper = new ObjectMapper();
        CompletionStage<Object> hashtagTweets= ask(hashtagActor, new MessageType.Hashtag(tag),timeout);
        CompletableFuture<ObjectNode> cfHashtagTweets= (CompletableFuture<ObjectNode>) hashtagTweets.toCompletableFuture().get();
        CompletionStage<Result> resultCompletableFuture= cfHashtagTweets.thenApply(data -> ok(objectMapper.convertValue(data,JsonNode.class)));
        return resultCompletableFuture;
    }

    public CompletionStage<Result> getTweetsByLocation(String latitude, String longitude,String location) throws ExecutionException, InterruptedException {
        System.out.println("Show tweets by location method");
        Timeout timeout= new Timeout(10,TimeUnit.SECONDS);
        ObjectMapper objectMapper = new ObjectMapper();
        CompletionStage<Object> locationTweets= ask(locationActor, new MessageType.Location(latitude,longitude,location),timeout);
        CompletableFuture<ObjectNode> cfLocationTweets= (CompletableFuture<ObjectNode>) locationTweets.toCompletableFuture().get();
        CompletionStage<Result> resultCompletableFuture= cfLocationTweets.thenApply(data -> ok(objectMapper.convertValue(data,JsonNode.class)));
        return resultCompletableFuture;
    }

    public Result showUser(String screenName) {
        System.out.println("Show user method " + screenName);
        return ok(usertimeline.render(screenName));
    }


    public CompletionStage<Result> getUserTimeline(String screenName) throws ExecutionException, InterruptedException {
        System.out.println("User timeline method " + screenName);
        Timeout timeout = new Timeout(10, TimeUnit.SECONDS);
        ObjectMapper objectMapper = new ObjectMapper();
        CompletionStage<Object> profileData = ask(userProfileActor, new MessageType.UserInfo(screenName), timeout);
        System.out.println("after profile data");
        CompletableFuture<ObjectNode> cfProfileData = (CompletableFuture<ObjectNode>) profileData.toCompletableFuture().get();
        System.out.println("after cf profile data");
        CompletionStage<Result> resultCompletableFuture = cfProfileData.thenApply(data -> {
            System.out.println("Data class " + data.getClass());
            return ok(objectMapper.convertValue(data, JsonNode.class));
        });
        return resultCompletableFuture;
    }

    public Result showStatistics(String topic){
        System.out.println("show statistics method");
        return ok(statistics.render(topic));
    }

    public CompletionStage<Result> getTweetStatistics(String topic) throws ExecutionException, InterruptedException {
        Timeout timeout = new Timeout(10, TimeUnit.SECONDS);
        ObjectMapper objectMapper = new ObjectMapper();
        CompletionStage<Object> tweetStatData= ask(statisticsActor, new MessageType.Stat(topic),timeout);
        CompletableFuture<ObjectNode> cfTweetStatData= (CompletableFuture<ObjectNode>) tweetStatData.toCompletableFuture().get();
        CompletionStage<Result> resultCompletableFuture= cfTweetStatData.thenApply(data ->{
            return ok(objectMapper.convertValue(data,JsonNode.class));
        });
        return resultCompletableFuture;
    }

    public Result index() {
        return ok(index.render(request()));
    }

    public Result callAnalyzeSentiment(String searchTopic){
        String  result = tf.analyzeSentiments(searchTopic);
        return ok(result);
    }

    public WebSocket ws() {
        System.out.println("web socket");
        return WebSocket.Json.accept(request -> ActorFlow.actorRef(websocket -> TwitterActor.props(websocket, tf), actorSystem, materializer));
    }

}
