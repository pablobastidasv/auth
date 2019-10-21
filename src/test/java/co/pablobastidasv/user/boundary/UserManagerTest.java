package co.pablobastidasv.user.boundary;

import static co.pablobastidasv.user.entity.SystemUser.FIND_BY_USERNAME_AND_TENANT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.pablobastidasv.user.entity.SystemUser;
import co.pablobastidasv.user.entity.UserEvent;
import co.pablobastidasv.user.entity.UserEvent.Email;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import java.util.Arrays;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import io.smallrye.reactive.messaging.annotations.Emitter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.CapturesArguments;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
class UserManagerTest {

  @Mock EntityManager em;
  @Mock PasswordTools passwordTools;
  @Mock TypedQuery<SystemUser> query;
  @Mock Logger logger;
  @Mock Emitter<KafkaMessage<String, SystemUser>> loginCreatedEmitter;

  @InjectMocks private UserManager userManager;

  private final String username = "username";
  private final String tenant = "avalane";
  
  private final String password = "password";

  private final String userId = "123L";
  private final Email mainEmail = new Email();
  private final Email otherEmail = new Email();

  @Test
  @SuppressWarnings("unchecked")
  void findByUserTenant_NoResultException() {
    when(em.createNamedQuery(anyString(), any(Class.class))).thenReturn(query);
    when(query.setParameter(anyString(), anyString())).thenReturn(query);

    when(query.getSingleResult()).thenThrow(NoResultException.class);

    var byUser = userManager.findByUserAndTenant(username, tenant);

    verify(em).createNamedQuery(FIND_BY_USERNAME_AND_TENANT, SystemUser.class);
    assertFalse(byUser.isPresent());
  }

  @Test
  @SuppressWarnings("unchecked")
  void findByUserTenant() {
    var user = new SystemUser();
    when(em.createNamedQuery(anyString(), any(Class.class))).thenReturn(query);
    when(query.setParameter(anyString(), anyString())).thenReturn(query);

    when(query.getSingleResult()).thenReturn(user);

    var byUser = userManager.findByUserAndTenant(username, tenant);

    verify(em).createNamedQuery(FIND_BY_USERNAME_AND_TENANT, SystemUser.class);
    assertTrue(byUser.isPresent());
    assertEquals(user, byUser.get());
  }

  @Test
  @SuppressWarnings("unchecked")
  void createUserFromUserEvent(){
    // Given a user event
    var event = new UserEvent();
    event.userId = userId;
    event.emails = Arrays.asList(mainEmail, otherEmail);

    when(passwordTools.generateRandomPassword()).thenReturn(password);

    ArgumentCaptor<SystemUser> systemUserCaptor = ArgumentCaptor.forClass(SystemUser.class);
    var msg = ArgumentCaptor.forClass(KafkaMessage.class);

    // When a user is created
    userManager.createUser(event);

    // Then, persisted user must be returned
    verify(em).persist(systemUserCaptor.capture());
    verify(loginCreatedEmitter).send(msg.capture());
    assertEquals(userId, systemUserCaptor.getValue().getUserId());
    assertEquals(mainEmail.email, systemUserCaptor.getValue().getUsername());
    assertEquals(userId, msg.getValue().getKey());
    assertEquals(systemUserCaptor.getValue(), msg.getValue().getPayload());
  }
}
