package co.pablobastidasv;

import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import io.smallrye.reactive.messaging.kafka.MessageHeaders;

public class KafkaMessageStub<K, T> implements KafkaMessage<K, T> {

  private K key;
  private T payload;

  public KafkaMessageStub(K key, T payload) {
    this.key = key;
    this.payload = payload;
  }

  @Override
  public T getPayload() {
    return payload;
  }

  @Override
  public K getKey() {
    return key;
  }

  @Override
  public String getTopic() {
    return null;
  }

  @Override
  public Integer getPartition() {
    return null;
  }

  @Override
  public Long getTimestamp() {
    return null;
  }

  @Override
  public MessageHeaders getHeaders() {
    return null;
  }
}
