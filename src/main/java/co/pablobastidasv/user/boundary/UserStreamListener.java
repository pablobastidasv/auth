package co.pablobastidasv.user.boundary;

import co.pablobastidasv.user.entity.UserEvent;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;

@ApplicationScoped
public class UserStreamListener {

  @Inject Logger log;
  @Inject UserManager userManager;

  /**
   * Method triggered when a user created event arrives to the Kafka.
   *
   * <p>This method will create the user login and trigger a new event indicating that the login
   * user has been created.
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

    //    var user = userManager.createUser(userEvent);

    return message.ack();
  }
}
