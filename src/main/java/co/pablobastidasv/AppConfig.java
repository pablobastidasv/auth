package co.pablobastidasv;

import javax.enterprise.inject.Produces;
import java.time.Clock;

public class AppConfig {

    @Produces
    public Clock clockProvider(){
        return Clock.systemUTC();
    }
}
