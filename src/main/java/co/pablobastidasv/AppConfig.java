package co.pablobastidasv;

import java.time.Clock;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfig {

  @Produces
  public Logger loggerProducer(InjectionPoint ip) {
    var clazz = ip.getMember().getDeclaringClass().getName();
    return LoggerFactory.getLogger(clazz);
  }

  @Produces
  public Clock clockProvider() {
    return Clock.systemUTC();
  }
}
