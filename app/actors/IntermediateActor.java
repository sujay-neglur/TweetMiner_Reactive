package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.Materializer;

import java.util.ArrayList;

public class IntermediateActor extends AbstractActor {

    private final Materializer materializer;
    private final ActorSystem actorSystem;
    ArrayList<ActorRef> registeredActors = new ArrayList<>();

    public IntermediateActor(ActorSystem actorSystem, Materializer materializer) {
        System.out.println("Intermediate actor constructor");
        this.materializer = materializer;
        this.actorSystem = actorSystem;
    }

    @Override
    public Receive createReceive() {
        System.out.println("Intermediate actor createReceive");
        return receiveBuilder()
                .match(MessageType.Register.class, register -> {
                    System.out.println(getContext().sender()+" Actor registered");
                    registeredActors.add(sender());
                })
                .match(MessageType.SearchTweets.class, searchTweets -> registeredActors.forEach(actor -> {
                    System.out.println("Intermediate actor got search tweet match");
                    actor.tell(searchTweets, self());
                }))
                .match(MessageType.Location.class, searchTweetsByLocation -> registeredActors.forEach(actor -> {
                    System.out.println("Intermediate actor got location tweet match");
                    actor.tell(searchTweetsByLocation,self());
                }))
                .build();
    }
}
