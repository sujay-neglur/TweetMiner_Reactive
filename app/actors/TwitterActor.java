package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.TweetMiner;

import java.util.ArrayList;

public class TwitterActor extends AbstractActor {
    ArrayList<String> searchTerms = new ArrayList<>();
    private ActorRef ws;
    TweetMiner tf;

    public TwitterActor(ActorRef ws, TweetMiner tf) {
        System.out.println("Twitter actor constructor");
        this.ws = ws;
        this.tf = tf;
        this.tf.initActor(ws);
        context().actorSelection("/user/intermediateActor").tell(new MessageType.Register(), self());
    }

    public static Props props(ActorRef ws, TweetMiner tf) {
        return Props.create(TwitterActor.class, ws, tf);
    }

    @Override
    public Receive createReceive() {
        System.out.println("Twitter actor createReceive");
        return receiveBuilder()
                .match(MessageType.SearchTweets.class, searchTweets -> {
                    searchTerms.add(searchTweets.topic);
//                    System.out.println(searchTerms);
                    tf.clearTwitterStream();
                    tf.initSearchTerms(searchTerms);
                    tf.searchTweets(self(),searchTweets.topic);
                })
                .build();
    }
}
