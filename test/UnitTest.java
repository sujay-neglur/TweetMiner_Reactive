
import actors.MessageType;
import actors.TwitterActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.HomeController;

import models.FakeTwitter;
import models.RealTwitter;
import models.TweetMiner;
import org.junit.*;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import akka.testkit.javadsl.TestKit;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;


import static play.mvc.Http.Status.OK;
import static play.test.Helpers.fakeRequest;

import org.junit.Test;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class UnitTest {

   /* @Inject
    Application application;*/


    static HomeController homeController;
    static ActorSystem system;
    static Materializer materializer;
    static FakeTwitter tf;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
        materializer = ActorMaterializer.create(system);
        tf  = new FakeTwitter();
        homeController = new HomeController(system, materializer, tf);
        tf.initSearchTerms(new ArrayList<>());
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }


    @Test
    public void testShowStatistics(){
        Result r= homeController.showStatistics("modi");
        assertEquals(r.status(),OK);
    }

    @Test
    public void testGetTweetStatistics() throws ExecutionException, InterruptedException {
        CompletionStage<Result> csResult = homeController.getTweetStatistics("modi");
        Result result = csResult.toCompletableFuture().get();
        assertEquals(OK, result.status());
    }

    @Test
    public void testCallAnalyzeSentiment() throws ExecutionException, InterruptedException {
       Result r = homeController.callAnalyzeSentiment("modi");
        assertEquals(OK, r.status());
    }


    @Test
    public void testUpdateFeed() {
        Result r = homeController.updateFeed("Modi");
        assertEquals(r.status(), OK);
    }

    @Test
    public void testShowHashTag() {
        Result r = homeController.showHashTag("Modi");
        assertEquals(r.status(), OK);
    }

    @Test
    public void testShowLocation() {
        Result r = homeController.showLocation("45.50884", "-73.58781", "Montreal");
        assertEquals(r.status(), OK);
    }

    @Test
    public void testShowUser() {
        Result r = homeController.showUser("MalthankarSuya");
        assertEquals(r.status(), OK);
    }

    @Test
    public void testIndex() {
        Helpers help=new Helpers();
        Http.RequestBuilder requestBuilder=fakeRequest(controllers.routes.HomeController.index());
        Http.Context context=help.httpContext(requestBuilder.build());
        Http.Context.current.set(context);
        Result r = homeController.index();
        assertEquals(r.status(), OK);
    }

    @Test
    public void testGetTweetsByHashtag() throws Exception{
        CompletionStage<Result> csResult = homeController.getTweetsByHashtag("Modi");
        Result result = csResult.toCompletableFuture().get();
        assertEquals(OK, result.status());
    }

    @Test
    public void testGetTweetsByLocation() throws Exception{
        CompletionStage<Result> csResult = homeController.getTweetsByLocation("45.50884", "-73.58781", "Montreal");
        Result result = csResult.toCompletableFuture().get();
        assertEquals(OK, result.status());
    }


    @Test
    public void testGetUserTimeLine() throws Exception{
        CompletionStage<Result> csResult = homeController.getUserTimeline("MalthankarSuya");
        Result result = csResult.toCompletableFuture().get();
        assertEquals(OK, result.status());
    }


    @Test
    public void testActors() throws Exception{
        new TestKit(system){{
            //intermediate actor and twitter actor
            final ActorRef intermediateActor = homeController.intermediateActor;
            MessageType.Register rm = new MessageType.Register();
            ActorRef twitterActor= system.actorOf(TwitterActor.props(getRef(),tf));
            intermediateActor.tell(rm, getRef());
            MessageType.SearchTweets searchTweets = new MessageType.SearchTweets("Modi");
            intermediateActor.tell(searchTweets, twitterActor);
            expectMsg(duration("5 seconds"), searchTweets);
            assertThat(intermediateActor.isTerminated()).isEqualTo(false);
            final ActorRef userActorTest = homeController.userProfileActor;
            MessageType.UserInfo userInfo = new MessageType.UserInfo("MalthankarSuya");
            userActorTest.tell(userInfo, getRef());

            expectMsgAnyClassOf(com.fasterxml.jackson.databind.node.ObjectNode.class, CompletableFuture.class);

            Thread.sleep(10);
            assertThat(userActorTest.isTerminated()).isEqualTo(false);


            //Location Test
            final ActorRef locationActor = homeController.locationActor;
            MessageType.Location locationInfo = new MessageType.Location("45.50884", "-73.58781", "Montreal");
            locationActor.tell(locationInfo, getRef());

            expectMsgAnyClassOf(com.fasterxml.jackson.databind.node.ObjectNode.class, CompletableFuture.class);

            Thread.sleep(10);
            assertThat(locationActor.isTerminated()).isEqualTo(false);


            //Hashtag Test
            final ActorRef hashtagActor = homeController.hashtagActor;
            MessageType.Hashtag hashtagInfo = new MessageType.Hashtag("modi");
            hashtagActor.tell(hashtagInfo, getRef());

            expectMsgAnyClassOf(com.fasterxml.jackson.databind.node.ObjectNode.class, CompletableFuture.class);

            Thread.sleep(10);
            assertThat(hashtagActor.isTerminated()).isEqualTo(false);

            //statistics actor
            new MessageType();
            final ActorRef statActor= homeController.statisticsActor;
            MessageType.Stat stat= new MessageType.Stat("modi");
            statActor.tell(stat,getRef());
            expectMsgAnyClassOf(com.fasterxml.jackson.databind.node.ObjectNode.class, CompletableFuture.class);
            Thread.sleep(10);
            assertThat(statActor.isTerminated()).isEqualTo(false);
        }};
    }

    @Test
    public void testUpdateMap(){
        tf.updateMap("key","value");
    }

    @Test
    public void testClearStream(){
        tf.clearTwitterStream();
    }

    @Test
    public void testSentimentMethod(){
        String s= tf.analyzeSentiments("modi");
        assertEquals(":-)",s);
        s=tf.analyzeSentiments("obama");
        assertEquals(":-(",s);
        s=tf.analyzeSentiments("trump");
        assertEquals(":-|",s);
    }

}