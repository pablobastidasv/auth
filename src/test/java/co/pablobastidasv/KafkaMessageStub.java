package co.pablobastidasv;

import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import io.smallrye.reactive.messaging.kafka.MessageHeaders;
import java.nio.charset.Charset;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

public class KafkaMessageStub<K, T> implements KafkaMessage<K, T> {

  private K key;
  private T payload;

  public KafkaMessageStub(K key, T payload) {
    this.key = key;
    this.payload = payload;
  }

  @Override
  public KafkaMessage<K, T> withHeader(String key, byte[] content) {
    return null;
  }

  @Override
  public KafkaMessage<K, T> withHeader(String key, String content, Charset enc) {
    return null;
  }

  @Override
  public KafkaMessage<K, T> withAck(Supplier<CompletionStage<Void>> ack) {
    return null;
  }

  @Override
  public T getPayload() {
    return payload;
  }

  @Override
  public CompletionStage<Void> ack() {
    return null;
  }

  @Override
  public <C> C unwrap(Class<C> unwrapType) {
    return null;
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

  @Override
  public Supplier<CompletionStage<Void>> getAckSupplier() {
    return null;
  }
}
