package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import models.TweetMiner;

public class HashtagActor extends AbstractActor {

    private TweetMiner tf;

    public HashtagActor(TweetMiner tf) {
        this.tf = tf;
    }

    public static Props props(TweetMiner tf){
        return Props.create(HashtagActor.class,tf);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MessageType.Hashtag.class, request ->sender().tell(tf.searchTweetsByHashTag(request.hashtag),self()))
                .build();
    }
}
