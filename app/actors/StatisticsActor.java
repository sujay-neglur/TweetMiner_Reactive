package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import models.TweetMiner;

public class StatisticsActor extends AbstractActor {

    private TweetMiner tf;

    public StatisticsActor(TweetMiner tf) {
        this.tf = tf;
    }

    public static Props props(TweetMiner tf){
        return Props.create(StatisticsActor.class,tf);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MessageType.Stat.class,request -> sender().tell(tf.calculateStatistics(request.topic,tf.returnTweets(request.topic)),self()))
                .build();

    }
}
