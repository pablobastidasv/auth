package co.pablobastidasv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.wildfly.common.Assert.assertNotNull;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import org.junit.jupiter.api.Test;

class KafkaJsonObjectSerializerTest {

  @Test
  void serialize() {
    String jsonString = "{\"emails\":[{\"type\":\"MAIN\",\"email\":\"cvrodriguez@gmail.com\"}]}";
    JsonObject json = Json.createReader(new StringReader(jsonString)).readObject();

    KafkaJsonObjectSerializer serializer = new KafkaJsonObjectSerializer();
    byte[] serializedJson = serializer.serialize("", json);

    assertNotNull(serializedJson);
    assertEquals(jsonString, new String(serializedJson));
  }
}