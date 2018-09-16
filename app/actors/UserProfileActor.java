package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import models.TweetMiner;

public class UserProfileActor extends AbstractActor {

    TweetMiner tf;

    public UserProfileActor(TweetMiner tf) {
        this.tf = tf;
    }

    public static Props props(TweetMiner tf){
        return Props.create(UserProfileActor.class,tf);
    }

    @Override
    public Receive createReceive() {
        System.out.println("User profile actor createReceive");
        return receiveBuilder()
                .match(MessageType.UserInfo.class, request-> {
                    sender().tell(tf.showUser(request.screenName),self());
                })
                .build();
    }
}
