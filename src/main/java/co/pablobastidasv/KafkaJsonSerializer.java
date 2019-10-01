package co.pablobastidasv;

import java.util.Map;
import javax.json.bind.JsonbBuilder;
import org.apache.kafka.common.serialization.Serializer;

public class KafkaJsonSerializer<T> implements Serializer<T> {

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {

  }

  @Override
  public byte[] serialize(String topic, T data) {
    return JsonbBuilder.create().toJson(data).getBytes();
  }

  @Override
  public void close() {

  }
}
