package co.pablobastidasv;

import java.time.Clock;
import javax.enterprise.inject.Produces;

public class AppConfig {

  @Produces
  public Clock clockProvider() {
    return Clock.systemUTC();
  }
}
