package co.pablobastidasv.user.boundary;

import co.pablobastidasv.user.entity.User;
import co.pablobastidasv.user.entity.UserEvent;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;

@ApplicationScoped
public class UserStreamListener {

  @Inject Logger log;
  @Inject UserManager userManager;

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
  @Outgoing("login_created")
  public KafkaMessage<String, User> onUserCreated(KafkaMessage<String, String> message) {
    log.debug("Message received = {}", message);
    UserEvent userEvent = JsonbBuilder.create().fromJson(message.getPayload(), UserEvent.class);
    userEvent.userId = Long.valueOf(message.getKey());
    log.debug("Event received = {}", userEvent);

    User user = userManager.createUser(userEvent);

    return KafkaMessage.of(message.getKey(), user);
  }
}
