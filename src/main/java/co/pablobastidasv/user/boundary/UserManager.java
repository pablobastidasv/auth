package co.pablobastidasv.user.boundary;

import co.pablobastidasv.user.entity.User;
import co.pablobastidasv.user.entity.UserEvent;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Optional;

import static co.pablobastidasv.user.entity.User.*;

@ApplicationScoped
public class UserManager {

  @Inject EntityManager em;
  @Inject PasswordTools passwordTools;
  @Inject Logger logger;
  @Inject @Stream("login_created") Emitter<User> loginCreatedEmitter;

  /**
   * Query in DB to find a {@link User} based on their {@code username} and {@code tenantId}.
   *
   * @param username The {@code username} which will be used in the query.
   * @param tenantId The {@code tenantId} which will be used in the query.
   * @return And optional with a {@link User} object if the query give result or empty in case the
   *     query does not return anything.
   */
  public Optional<User> findByUserAndTenant(String username, String tenantId) {
    try {
      var user = em.createNamedQuery(FIND_BY_USERNAME_AND_TENANT, User.class)
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
    var user = new User(passwordTools)
            .setUsername(userEvent.emails.get(0).email)
            .setUserId(userEvent.userId)
            .resetPassword();

    em.persist(user);
    em.detach(user);
    logger.trace("User Created.");

    logger.trace("Sending event...");
    loginCreatedEmitter.send(user);
    logger.trace("Event Sent.");
  }
}
