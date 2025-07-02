package io.github.gabrielmmoraes1999.service.config;

import io.github.gabrielmmoraes1999.apiservice.annotation.Bean;
import io.github.gabrielmmoraes1999.apiservice.annotation.Component;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RegisteredClientJDBC;
import io.github.gabrielmmoraes1999.db.DataBase;

@Component
public class JdbcConfig {

    @Bean
    public void createRegisteredClientJdbc() {
        try {
            Class.forName("org.sqlite.JDBC").newInstance();
            DataBase.createConnection("jdbc:sqlite:database.db");
            RegisteredClientJDBC.setConnection(DataBase.getConnection());
        } catch (Exception ignore) {

        }
    }

}
