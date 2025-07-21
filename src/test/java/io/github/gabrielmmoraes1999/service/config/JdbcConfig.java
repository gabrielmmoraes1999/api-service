package io.github.gabrielmmoraes1999.service.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.gabrielmmoraes1999.apiservice.annotation.Bean;
import io.github.gabrielmmoraes1999.apiservice.annotation.Component;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RegisteredClientJDBC;
import io.github.gabrielmmoraes1999.db.ConnectionPoolManager;
import io.github.gabrielmmoraes1999.db.DataBase;

@Component
public class JdbcConfig {

    @Bean
    public void createRegisteredClientJdbc() {
        HikariDataSource hikariDataSource = getHikariDataSource();
        ConnectionPoolManager.setHikariDataSource(hikariDataSource);
        RegisteredClientJDBC.setHikariDataSource(hikariDataSource);
    }

    private HikariDataSource getHikariDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:database.db");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000); // 30s
        config.setIdleTimeout(600000);      // 10min
        config.setMaxLifetime(1800000);     // 30min
        return new HikariDataSource(config);
    }

}
