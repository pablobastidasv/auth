package co.pablobastidasv.user.boundary;

import static co.pablobastidasv.user.entity.SystemUser.FIND_BY_USERNAME_AND_TENANT;
import static co.pablobastidasv.user.entity.SystemUser.TENANT_FIELD;
import static co.pablobastidasv.user.entity.SystemUser.USERNAME_FIELD;

import co.pablobastidasv.user.entity.SystemUser;
import co.pablobastidasv.user.entity.UserEvent;
import io.smallrye.reactive.messaging.annotations.Channel;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import org.slf4j.Logger;

@ApplicationScoped
public class UserManager {

  @Inject EntityManager em;
  @Inject PasswordTools passwordTools;
  @Inject Logger logger;
  @Inject @Channel("login_created") Emitter<KafkaMessage<String, SystemUser>> loginCreatedEmitter;

  /**
   * Query in DB to find a {@link SystemUser} based on their {@code username} and {@code tenantId}.
   *
   * @param username The {@code username} which will be used in the query.
   * @param tenantId The {@code tenantId} which will be used in the query.
   * @return And optional with a {@link SystemUser} object if the query give result or empty in case the
   *     query does not return anything.
   */
  public Optional<SystemUser> findByUserAndTenant(String username, String tenantId) {
    try {
      var user = em.createNamedQuery(FIND_BY_USERNAME_AND_TENANT, SystemUser.class)
              .setParameter(USERNAME_FIELD, username)
              .setParameter(TENANT_FIELD, tenantId)
              .getSingleResult();

      return Optional.of(user);
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  /**
   * Create the user registry in the database and return the assigned password in raw format.
   *
   * @param userEvent The event received with basic user information.
   */
  @Transactional
  public void createUser(@ObservesAsync UserEvent userEvent) {
    logger.trace("Creating user...");
    var user = new SystemUser(passwordTools)
            .setUsername(userEvent.emails.get(0).email)
            .setUserId(userEvent.userId)
            .resetPassword();

    em.persist(user);
    em.flush();
    logger.trace("User Created.");

    logger.trace("Sending event...");
    var message = KafkaMessage.of(userEvent.userId, user);
    loginCreatedEmitter.send(message);
    logger.trace("Event Sent.");
  }
}
