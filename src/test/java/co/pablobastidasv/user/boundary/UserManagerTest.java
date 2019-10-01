package co.pablobastidasv.user.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.pablobastidasv.user.entity.User;
import co.pablobastidasv.user.entity.UserEvent;
import co.pablobastidasv.user.entity.UserEvent.Email;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserManagerTest {

  @Mock private EntityManager em;
  @Mock private PasswordTools passwordTools;
  @Mock private TypedQuery<User> query;

  @InjectMocks private UserManager userManager;

  private final String username = "username";
  private final String tenant = "avalane";
  
  private final String password = "password";

  private final Long userId = 123L;
  private final Email mainEmail = new Email();
  private final Email otherEmail = new Email();

  @Test
  @SuppressWarnings("unchecked")
  void findByUserTenant_NoResultException() {
    when(em.createNamedQuery(anyString(), any(Class.class))).thenReturn(query);
    when(query.setParameter(anyString(), anyString())).thenReturn(query);

    when(query.getSingleResult()).thenThrow(NoResultException.class);

    Optional<User> byUser = userManager.findByUserAndTenant(username, tenant);

    verify(em).createNamedQuery("User.findByUsernameAndTenant", User.class);
    assertFalse(byUser.isPresent());
  }

  @Test
  @SuppressWarnings("unchecked")
  void findByUserTenant() {
    User user = new User();
    when(em.createNamedQuery(anyString(), any(Class.class))).thenReturn(query);
    when(query.setParameter(anyString(), anyString())).thenReturn(query);

    when(query.getSingleResult()).thenReturn(user);

    Optional<User> byUser = userManager.findByUserAndTenant(username, tenant);

    verify(em).createNamedQuery("User.findByUsernameAndTenant", User.class);
    assertTrue(byUser.isPresent());
    assertEquals(user, byUser.get());
  }

  @Test
  void createUserFromUserEvent(){
    // Given a user event
    UserEvent event = new UserEvent();
    event.userId = userId;
    event.emails = Arrays.asList(mainEmail, otherEmail);

    when(passwordTools.generateRandomPassword()).thenReturn(password);

    // When a user is created
    User userCreated = userManager.createUser(event);

    // Then, persisted user must be returned
    assertEquals(userId, userCreated.getUserId());
    assertEquals(mainEmail.email, userCreated.getUsername());
  }
}
