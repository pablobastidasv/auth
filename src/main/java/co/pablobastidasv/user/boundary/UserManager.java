package co.pablobastidasv.user.boundary;

import static co.pablobastidasv.user.entity.User.FIND_BY_USERNAME_AND_TENANT;
import static co.pablobastidasv.user.entity.User.TENANT_FIELD;
import static co.pablobastidasv.user.entity.User.USERNAME_FIELD;

import co.pablobastidasv.user.entity.User;
import java.util.Optional;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Dependent
public class UserManager {

  @Inject EntityManager em;

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
      User user =
          em.createNamedQuery(FIND_BY_USERNAME_AND_TENANT, User.class)
              .setParameter(USERNAME_FIELD, username)
              .setParameter(TENANT_FIELD, tenantId)
              .getSingleResult();

      return Optional.of(user);
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }
}
