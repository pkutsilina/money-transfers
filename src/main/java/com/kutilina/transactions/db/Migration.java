package com.kutilina.transactions.db;

import com.kutilina.transactions.AppProperties;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

public class Migration {
    private static final String CONNECTION_URL = AppProperties.getProperty("db.url");
    private static final String PASSWORD = AppProperties.getProperty("db.password");
    private static final String USER = AppProperties.getProperty("db.user");

    public static void migrate() {
        FluentConfiguration fluentConfiguration = Flyway.configure().dataSource(CONNECTION_URL, USER,
                PASSWORD)
                .schemas("public")
                .locations("flyway")
                .sqlMigrationPrefix("V")
                .sqlMigrationSeparator("__")
                .sqlMigrationSuffixes(".sql")
                .validateOnMigrate(true)
                .table("schema_version");

        Flyway flyway = fluentConfiguration.load();
        flyway.migrate();
    }
}
