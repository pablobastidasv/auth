package co.pablobastidasv;

import org.flywaydb.core.Flyway;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MigrationService {
    @Inject
    Flyway flyway;

    public void checkMigration() {
        // This will print 1.0.0
        System.out.println(flyway.info().current().getVersion().toString());
    }
}
