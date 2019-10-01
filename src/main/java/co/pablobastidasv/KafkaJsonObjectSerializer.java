package co.pablobastidasv;

import java.util.Map;
import javax.json.JsonObject;
import org.apache.kafka.common.serialization.Serializer;

public class KafkaJsonObjectSerializer implements Serializer<JsonObject> {

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {

  }

  @Override
  public byte[] serialize(String topic, JsonObject data) {
    return data.toString().getBytes();
  }

  @Override
  public void close() {

  }
}
