package co.pablobastidasv.user.boundary;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import co.pablobastidasv.KafkaMessageStub;
import co.pablobastidasv.user.entity.UserEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
class UserStreamListenerTest {

  @Mock Logger log;
  @Mock UserManager userManager;

  @InjectMocks UserStreamListener userStreamListener;

  @Test
  void onUserCreated(){
    // Given, a kafka event
    var userCreatedEvent = "{"
        + "\"emails\": [{\"type\":\"MAIN\",\"email\":\"pablob@gmail.com\"}]}";
    var userId = "123";
    var userMessage = new KafkaMessageStub<>(userId, userCreatedEvent);

    // When, the user is triggered.
    userStreamListener.onUserCreated(userMessage);

    // Then, message key must be the userId in user object
    var argument = ArgumentCaptor.forClass(UserEvent.class);
    verify(userManager).createUser(argument.capture());

    verify(userManager).createUser(any(UserEvent.class));
    verify(log).debug(anyString(), any(UserEvent.class));
  }

}