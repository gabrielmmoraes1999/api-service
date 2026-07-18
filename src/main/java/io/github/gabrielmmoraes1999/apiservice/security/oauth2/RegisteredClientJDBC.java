package io.github.gabrielmmoraes1999.apiservice.security.oauth2;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class RegisteredClientJDBC {

    private static Connection connection;
    private static HikariDataSource hikariDataSource;

    public static RegisteredClient findById(String id) {
        Connection conn = null;
        RegisteredClient registeredClient = null;

        try {
            conn = RegisteredClientJDBC.getConnection();

            try (PreparedStatement preparedStatement = conn.prepareStatement("select * from oauth2_registered_client where id = ?")) {
                preparedStatement.setString(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        registeredClient = new RegisteredClient(resultSet.getString("id"));
                        registeredClient.clientId = resultSet.getString("client_id");
                        registeredClient.clientIdIssuedAt = resultSet.getTimestamp("client_id_issued_at").toInstant();
                        registeredClient.clientSecret = resultSet.getString("client_secret");
                        registeredClient.clientName = resultSet.getString("client_name");
                        registeredClient.tokenSettings = readTokenSettings(resultSet.getString("token_settings"));
                    }
                }
            }
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    if (RegisteredClientJDBC.hikariDataSource != null)
                        conn.close();
                } catch (SQLException ignore) {

                }
            }
        }

        return registeredClient;
    }

    public static RegisteredClient findByClientId(String clientId) {
        Connection conn = null;
        RegisteredClient registeredClient = null;

        try {
            conn = RegisteredClientJDBC.getConnection();

            try (PreparedStatement preparedStatement = conn.prepareStatement("select * from oauth2_registered_client where client_id = ?")) {
                preparedStatement.setString(1, clientId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        registeredClient = new RegisteredClient(resultSet.getString("id"));
                        registeredClient.clientId = resultSet.getString("client_id");
                        registeredClient.clientIdIssuedAt = resultSet.getTimestamp("client_id_issued_at").toInstant();
                        registeredClient.clientSecret = resultSet.getString("client_secret");
                        registeredClient.clientName = resultSet.getString("client_name");
                        registeredClient.tokenSettings = readTokenSettings(resultSet.getString("token_settings"));
                    }
                }
            }
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    if (RegisteredClientJDBC.hikariDataSource != null)
                        conn.close();
                } catch (SQLException ignore) {

                }
            }
        }

        return registeredClient;
    }

    public static RegisteredClient findByClientIdAndClientSecret(String clientId, String clientSecret) {
        Connection conn = null;
        RegisteredClient registeredClient = null;

        try {
            conn = RegisteredClientJDBC.getConnection();

            try (PreparedStatement preparedStatement = conn.prepareStatement("select * from oauth2_registered_client where client_id = ? and client_secret = ?")) {
                preparedStatement.setString(1, clientId);
                preparedStatement.setString(2, clientSecret);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        registeredClient = new RegisteredClient(resultSet.getString("id"));
                        registeredClient.clientId = resultSet.getString("client_id");
                        registeredClient.clientIdIssuedAt = resultSet.getTimestamp("client_id_issued_at").toInstant();
                        registeredClient.clientSecret = resultSet.getString("client_secret");
                        registeredClient.clientName = resultSet.getString("client_name");
                        registeredClient.tokenSettings = readTokenSettings(resultSet.getString("token_settings"));
                    }
                }
            }
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    if (RegisteredClientJDBC.hikariDataSource != null)
                        conn.close();
                } catch (SQLException ignore) {

                }
            }
        }

        return registeredClient;
    }

    public static void save(RegisteredClient registeredClient) {
        Connection conn = null;

        try {
            conn = RegisteredClientJDBC.getConnection();

            try (PreparedStatement preparedStatement = conn.prepareStatement("insert into oauth2_registered_client (id, client_id, client_id_issued_at, client_secret, client_name, token_settings) values (?,?,?,?,?,?)")) {
                preparedStatement.setString(1, registeredClient.id);
                preparedStatement.setString(2, registeredClient.clientId);
                preparedStatement.setTimestamp(3, Timestamp.from(registeredClient.clientIdIssuedAt));
                preparedStatement.setString(4, registeredClient.clientSecret);
                preparedStatement.setString(5, registeredClient.clientName);
                writeTokenSettings(preparedStatement, 6, registeredClient.tokenSettings);
                preparedStatement.execute();
            }

            if (RegisteredClientJDBC.hikariDataSource != null)
                conn.close();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    if (RegisteredClientJDBC.hikariDataSource != null)
                        conn.close();
                } catch (SQLException ignore) {

                }
            }
        }

    }

    public static void updateClientId(String clientId, String id) {
        Connection conn = null;

        try {
            conn = RegisteredClientJDBC.getConnection();

            try (PreparedStatement preparedStatement = conn.prepareStatement("update oauth2_registered_client set client_id = ? where id = ?")) {
                preparedStatement.setString(1, clientId);
                preparedStatement.setString(2, id);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    if (RegisteredClientJDBC.hikariDataSource != null)
                        conn.close();
                } catch (SQLException ignore) {

                }
            }
        }
    }

    public static void updateClientSecret(String clientSecret, String id) {
        Connection conn = null;

        try {
            conn = RegisteredClientJDBC.getConnection();

            try (PreparedStatement preparedStatement = conn.prepareStatement("update oauth2_registered_client set client_secret = ? where id = ?")) {
                preparedStatement.setString(1, clientSecret);
                preparedStatement.setString(2, id);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    if (RegisteredClientJDBC.hikariDataSource != null)
                        conn.close();
                } catch (SQLException ignore) {

                }
            }
        }
    }

    public static void updateTokenSettings(TokenSettings tokenSettings, String id) {
        Connection conn = null;

        try {
            conn = RegisteredClientJDBC.getConnection();

            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    "update oauth2_registered_client set token_settings = ? where id = ?"
            )) {
                writeTokenSettings(preparedStatement, 1, tokenSettings);
                preparedStatement.setString(2, id);
                preparedStatement.execute();
            }
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    if (RegisteredClientJDBC.hikariDataSource != null)
                        conn.close();
                } catch (SQLException ignore) {

                }
            }
        }
    }

    public static void delete(String uuid) {
        Connection conn = null;

        try {
            conn = RegisteredClientJDBC.getConnection();

            try (PreparedStatement preparedStatement = conn.prepareStatement("delete from oauth2_registered_client where id = ?")) {
                preparedStatement.setString(1, uuid);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    if (RegisteredClientJDBC.hikariDataSource != null)
                        conn.close();
                } catch (SQLException ignore) {

                }
            }
        }
    }

    public static void saveToken(
            String registeredClientId,
            String principalName,
            Timestamp accessTokenIssuedAt,
            Timestamp accessTokenExpiresAt,
            String accessTokenType,
            String accessTokenValue
    ) {
        saveToken(
                registeredClientId,
                principalName,
                accessTokenIssuedAt,
                accessTokenExpiresAt,
                accessTokenType,
                accessTokenValue,
                null,
                null,
                null
        );
    }

    public static void saveToken(
            String registeredClientId,
            String principalName,
            Timestamp accessTokenIssuedAt,
            Timestamp accessTokenExpiresAt,
            String accessTokenType,
            String accessTokenValue,
            String refreshTokenValue,
            Timestamp refreshTokenIssuedAt,
            Timestamp refreshTokenExpiresAt
    ) {
        Connection conn = null;

        try {
            conn = RegisteredClientJDBC.getConnection();

            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    "insert into oauth2_authorization (registered_client_id, principal_name, access_token_issued_at, access_token_expires_at, access_token_type, access_token_value, refresh_token_value, refresh_token_issued_at, refresh_token_expires_at) values (?,?,?,?,?,?,?,?,?)"
            )) {
                preparedStatement.setString(1, registeredClientId);
                preparedStatement.setString(2, principalName);
                preparedStatement.setTimestamp(3, accessTokenIssuedAt);
                preparedStatement.setTimestamp(4, accessTokenExpiresAt);
                preparedStatement.setString(5, accessTokenType);
                preparedStatement.setString(6, accessTokenValue);
                preparedStatement.setString(7, refreshTokenValue);
                preparedStatement.setTimestamp(8, refreshTokenIssuedAt);
                preparedStatement.setTimestamp(9, refreshTokenExpiresAt);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    if (RegisteredClientJDBC.hikariDataSource != null)
                        conn.close();
                } catch (SQLException ignore) {

                }
            }
        }
    }

    public static OAuth2Authorization findAuthorizationByRefreshToken(String refreshToken) {
        Connection conn = null;

        try {
            conn = RegisteredClientJDBC.getConnection();

            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    "select registered_client_id, principal_name, refresh_token_expires_at from oauth2_authorization where refresh_token_value = ?"
            )) {
                preparedStatement.setString(1, refreshToken);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return new OAuth2Authorization(
                                resultSet.getString("registered_client_id"),
                                resultSet.getString("principal_name"),
                                resultSet.getTimestamp("refresh_token_expires_at")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    if (RegisteredClientJDBC.hikariDataSource != null)
                        conn.close();
                } catch (SQLException ignore) {

                }
            }
        }

        return null;
    }

    public static void invalidateRefreshToken(String refreshToken) {
        Connection conn = null;

        try {
            conn = RegisteredClientJDBC.getConnection();

            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    "update oauth2_authorization set refresh_token_value = null, refresh_token_issued_at = null, refresh_token_expires_at = null where refresh_token_value = ?"
            )) {
                preparedStatement.setString(1, refreshToken);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    if (RegisteredClientJDBC.hikariDataSource != null)
                        conn.close();
                } catch (SQLException ignore) {

                }
            }
        }
    }

    public static void setConnection(Connection connection) {
        RegisteredClientJDBC.connection = connection;
    }

    public static void setHikariDataSource(HikariDataSource hikariDataSource) {
        RegisteredClientJDBC.hikariDataSource = hikariDataSource;
    }

    private static Connection getConnection() throws SQLException {
        if (RegisteredClientJDBC.hikariDataSource != null) {
            return RegisteredClientJDBC.hikariDataSource.getConnection();
        }

        return RegisteredClientJDBC.connection;
    }

    private static TokenSettings readTokenSettings(String tokenSettingsJson) throws JsonProcessingException {
        if (tokenSettingsJson == null || tokenSettingsJson.isBlank()) {
            return null;
        }

        ObjectMapper mapper = createTokenSettingsMapper();
        return mapper.readValue(tokenSettingsJson, TokenSettings.class);
    }

    private static void writeTokenSettings(PreparedStatement preparedStatement, int index, TokenSettings tokenSettings)
            throws SQLException, JsonProcessingException {
        if (tokenSettings == null) {
            preparedStatement.setNull(index, Types.VARCHAR);
            return;
        }

        ObjectMapper mapper = createTokenSettingsMapper();
        preparedStatement.setString(index, mapper.writeValueAsString(tokenSettings));
    }

    private static ObjectMapper createTokenSettingsMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper;
    }

}
