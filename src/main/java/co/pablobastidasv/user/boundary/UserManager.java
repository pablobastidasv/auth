package co.pablobastidasv.user.boundary;

import co.pablobastidasv.user.entity.User;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Optional;

import static co.pablobastidasv.user.entity.User.*;

@Dependent
public class UserManager {

    @Inject
    EntityManager em;

    public Optional<User> findByUserAndTenant(String username, String tenantId) {
        try {
            User user = em.createNamedQuery(FIND_BY_USERNAME_AND_TENANT, User.class)
                .setParameter(USERNAME_FIELD, username)
                .setParameter(TENANT_FIELD, tenantId)
                .getSingleResult();

            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
