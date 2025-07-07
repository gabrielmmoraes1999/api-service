package io.github.gabrielmmoraes1999.apiservice.security.oauth2;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.gabrielmmoraes1999.apiservice.context.ApplicationContext;
import io.github.gabrielmmoraes1999.apiservice.security.crypto.PasswordEncoder;
import io.github.gabrielmmoraes1999.apiservice.security.crypto.md5.Md5PasswordEncoder;

import java.sql.*;

public class RegisteredClientJDBC {

    private static Connection connection;

    public static RegisteredClient findById(String id) {
        RegisteredClient registeredClient = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from oauth2_registered_client where id = ?")) {
            preparedStatement.setString(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    registeredClient = new RegisteredClient(resultSet.getString("id"));
                    registeredClient.clientId = resultSet.getString("client_id");
                    registeredClient.clientIdIssuedAt = resultSet.getTimestamp("client_id_issued_at").toInstant();
                    registeredClient.clientSecret = resultSet.getString("client_secret");
                    registeredClient.clientName = resultSet.getString("client_name");
                    if (resultSet.getString("token_settings") != null) {
                        registeredClient.tokenSettings = new ObjectMapper()
                                .readValue(resultSet.getString("token_settings"), TokenSettings.class);
                    }
                }
            }
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return registeredClient;
    }

    public static RegisteredClient findByClientId(String clientId) {
        RegisteredClient registeredClient = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from oauth2_registered_client where client_id = ?")) {
            preparedStatement.setString(1, clientId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    registeredClient = new RegisteredClient(resultSet.getString("id"));
                    registeredClient.clientId = resultSet.getString("client_id");
                    registeredClient.clientIdIssuedAt = resultSet.getTimestamp("client_id_issued_at").toInstant();
                    registeredClient.clientSecret = resultSet.getString("client_secret");
                    registeredClient.clientName = resultSet.getString("client_name");
                    if (resultSet.getString("token_settings") != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                        registeredClient.tokenSettings = mapper.readValue(
                                resultSet.getString("token_settings"),
                                TokenSettings.class
                        );
                    }
                }
            }
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return registeredClient;
    }

    public static RegisteredClient findByClientIdAndClientSecret(String clientId, String clientSecret) {
        RegisteredClient registeredClient = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from oauth2_registered_client where client_id = ? and client_secret = ?")) {
            preparedStatement.setString(1, clientId);
            preparedStatement.setString(2, clientSecret);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    registeredClient = new RegisteredClient(resultSet.getString("id"));
                    registeredClient.clientId = resultSet.getString("client_id");
                    registeredClient.clientIdIssuedAt = resultSet.getTimestamp("client_id_issued_at").toInstant();
                    registeredClient.clientSecret = resultSet.getString("client_secret");
                    registeredClient.clientName = resultSet.getString("client_name");
                    if (resultSet.getString("token_settings") != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                        registeredClient.tokenSettings = mapper.readValue(
                                resultSet.getString("token_settings"),
                                TokenSettings.class
                        );
                    }
                }
            }
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return registeredClient;
    }

    public static void save(RegisteredClient registeredClient) {
        String sql = "insert into oauth2_registered_client (id, client_id, client_id_issued_at, client_secret, client_name, token_settings) values (?,?,?,?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, registeredClient.id);
            preparedStatement.setString(2, registeredClient.clientId);
            preparedStatement.setTimestamp(3, Timestamp.from(registeredClient.clientIdIssuedAt));
            preparedStatement.setString(4, registeredClient.clientSecret);
            preparedStatement.setString(5, registeredClient.clientName);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            preparedStatement.setString(6, mapper.writeValueAsString(registeredClient.tokenSettings));
            preparedStatement.execute();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateClientId(String clientId, String id) {
        String sql = "update oauth2_registered_client set client_id = ? where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, clientId);
            preparedStatement.setString(2, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateClientSecret(String clientSecret, String id) {
        String sql = "update oauth2_registered_client set client_secret = ? where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, clientSecret);
            preparedStatement.setString(2, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(String uuid) {
        String sql = "delete from oauth2_registered_client where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, uuid);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setConnection(Connection connection) {
        RegisteredClientJDBC.connection = connection;
    }

}
