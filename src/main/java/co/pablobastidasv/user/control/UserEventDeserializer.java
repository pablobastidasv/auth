package co.pablobastidasv.user.control;

import co.pablobastidasv.user.entity.UserEvent;
import java.util.Map;
import javax.json.bind.JsonbBuilder;
import org.apache.kafka.common.serialization.Deserializer;

public class UserEventDeserializer implements Deserializer<UserEvent> {

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {}

  @Override
  public UserEvent deserialize(String topic, byte[] data) {
    return JsonbBuilder.create().fromJson(new String(data), UserEvent.class);
  }

  @Override
  public void close() {}
}
