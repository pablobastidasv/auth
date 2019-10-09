package co.pablobastidasv.user.boundary;

import co.pablobastidasv.KafkaMessageStub;
import co.pablobastidasv.user.entity.UserEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import javax.enterprise.event.Event;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserStreamListenerTest {

  @Mock Logger log;
  @Mock Event<UserEvent> bus;

  @InjectMocks UserStreamListener userStreamListener;

  @Test
  void onUserCreated(){
    // Given, a kafka event
    var userCreatedEvent = new UserEvent();
    UserEvent.Email email = new UserEvent.Email();
    email.email = "pablob@gmail.com";
    email.type = "MAIN";
    userCreatedEvent.emails = Collections.singletonList(email);

    var userId = "123";
    var userMessage = new KafkaMessageStub<>(userId, userCreatedEvent);

    // When, the user is triggered.
    userStreamListener.onUserCreated(userMessage);

    // Then, message key must be the userId in user object
    var argument = ArgumentCaptor.forClass(UserEvent.class);
    verify(bus).fireAsync(argument.capture());

    assertEquals(userId, argument.getValue().userId);
    assertNotNull(argument.getValue().emails);
  }

}