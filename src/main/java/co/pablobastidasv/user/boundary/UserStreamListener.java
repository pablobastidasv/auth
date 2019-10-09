package co.pablobastidasv.user.boundary;

import co.pablobastidasv.user.entity.UserEvent;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class UserStreamListener {

  @Inject Logger log;
  @Inject Event<UserEvent> bus;

  /**
   * Method triggered when a user created event arrives to the Kafka.
   *
   * <p>This method will create the user login and trigger a new event indicating
   * that the login user has been created.</p>
   *
   * @param message User event from the kafka topic
   * @return The user created to be published in the kafka topic
   */
  @Incoming("user_created")
  public CompletionStage<Void> onUserCreated(KafkaMessage<String, UserEvent> message) {
    log.debug("Message received = {}", message);
    var userEvent = message.getPayload();
    userEvent.userId = message.getKey();
    log.debug("Event received = {}", userEvent);

    bus.fireAsync(userEvent);

    return message.ack();
  }
}
