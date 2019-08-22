package co.pablobastidasv;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;

@ApplicationScoped
public class MigrationService {
  @Inject
  Flyway flyway;

  public void checkMigration() {
    // This will print 1.0.0
    System.out.println(flyway.info().current().getVersion().toString());
  }
}
