package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import models.TweetMiner;

public class LocationActor extends AbstractActor {

    private TweetMiner tf;

    public LocationActor(TweetMiner tf) {
        this.tf = tf;
    }

    public static Props props(TweetMiner tf) {
        return Props.create(LocationActor.class, tf);
    }

    @Override
    public Receive createReceive() {
        System.out.println("Location actor received");
        return receiveBuilder()
                .match(MessageType.Location.class, request ->
                        sender().tell(tf.searchTweetsByLocation(request.latitude, request.longitude, request.locationName),self()))
                .build();
    }
}
