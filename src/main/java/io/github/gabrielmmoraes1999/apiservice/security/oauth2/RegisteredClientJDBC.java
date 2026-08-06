package io.github.gabrielmmoraes1999.apiservice.security.oauth2;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class RegisteredClientJDBC {

    private static final Logger log = LoggerFactory.getLogger(RegisteredClientJDBC.class);

    private static final ObjectMapper TOKEN_SETTINGS_MAPPER = createTokenSettingsMapper();

    private static Connection connection;
    private static HikariDataSource hikariDataSource;

    @FunctionalInterface
    private interface SqlFunction<T> {
        T apply(Connection conn) throws SQLException, JsonProcessingException;
    }

    @FunctionalInterface
    private interface SqlConsumer {
        void accept(Connection conn) throws SQLException, JsonProcessingException;
    }

    public static RegisteredClient findById(String id) {
        return withConnection(conn -> {
            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    "select * from oauth2_registered_client where id = ?")) {
                preparedStatement.setString(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapRegisteredClient(resultSet);
                    }
                }
            }
            return null;
        });
    }

    public static RegisteredClient findByClientId(String clientId) {
        return withConnection(conn -> {
            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    "select * from oauth2_registered_client where client_id = ?")) {
                preparedStatement.setString(1, clientId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapRegisteredClient(resultSet);
                    }
                }
            }
            return null;
        });
    }

    public static RegisteredClient findByClientIdAndClientSecret(String clientId, String clientSecret) {
        return withConnection(conn -> {
            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    "select * from oauth2_registered_client where client_id = ? and client_secret = ?")) {
                preparedStatement.setString(1, clientId);
                preparedStatement.setString(2, clientSecret);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapRegisteredClient(resultSet);
                    }
                }
            }
            return null;
        });
    }

    public static void save(RegisteredClient registeredClient) {
        withConnectionVoid(conn -> {
            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    "insert into oauth2_registered_client (id, client_id, client_id_issued_at, client_secret, client_name, token_settings) values (?,?,?,?,?,?)")) {
                preparedStatement.setString(1, registeredClient.id);
                preparedStatement.setString(2, registeredClient.clientId);
                preparedStatement.setTimestamp(3, Timestamp.from(registeredClient.clientIdIssuedAt));
                preparedStatement.setString(4, registeredClient.clientSecret);
                preparedStatement.setString(5, registeredClient.clientName);
                writeTokenSettings(preparedStatement, 6, registeredClient.tokenSettings);
                preparedStatement.execute();
            }
        });
    }

    public static void updateClientId(String clientId, String id) {
        withConnectionVoid(conn -> {
            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    "update oauth2_registered_client set client_id = ? where id = ?")) {
                preparedStatement.setString(1, clientId);
                preparedStatement.setString(2, id);
                preparedStatement.execute();
            }
        });
    }

    public static void updateClientSecret(String clientSecret, String id) {
        withConnectionVoid(conn -> {
            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    "update oauth2_registered_client set client_secret = ? where id = ?")) {
                preparedStatement.setString(1, clientSecret);
                preparedStatement.setString(2, id);
                preparedStatement.execute();
            }
        });
    }

    public static void updateTokenSettings(TokenSettings tokenSettings, String id) {
        withConnectionVoid(conn -> {
            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    "update oauth2_registered_client set token_settings = ? where id = ?")) {
                writeTokenSettings(preparedStatement, 1, tokenSettings);
                preparedStatement.setString(2, id);
                preparedStatement.execute();
            }
        });
    }

    public static void delete(String uuid) {
        withConnectionVoid(conn -> {
            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    "delete from oauth2_registered_client where id = ?")) {
                preparedStatement.setString(1, uuid);
                preparedStatement.execute();
            }
        });
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
        withConnectionVoid(conn -> insertToken(
                conn,
                registeredClientId,
                principalName,
                accessTokenIssuedAt,
                accessTokenExpiresAt,
                accessTokenType,
                accessTokenValue,
                refreshTokenValue,
                refreshTokenIssuedAt,
                refreshTokenExpiresAt
        ));
    }

    /**
     * Invalidates the old refresh token and persists the new authorization in a single transaction.
     */
    public static void rotateRefreshToken(
            String oldRefreshToken,
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
        withTransaction(conn -> {
            try (PreparedStatement invalidate = conn.prepareStatement(
                    "update oauth2_authorization set refresh_token_value = null, refresh_token_issued_at = null, refresh_token_expires_at = null where refresh_token_value = ?")) {
                invalidate.setString(1, oldRefreshToken);
                invalidate.execute();
            }
            insertToken(
                    conn,
                    registeredClientId,
                    principalName,
                    accessTokenIssuedAt,
                    accessTokenExpiresAt,
                    accessTokenType,
                    accessTokenValue,
                    refreshTokenValue,
                    refreshTokenIssuedAt,
                    refreshTokenExpiresAt
            );
        });
    }

    public static OAuth2Authorization findAuthorizationByRefreshToken(String refreshToken) {
        return withConnection(conn -> {
            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    "select registered_client_id, principal_name, refresh_token_expires_at from oauth2_authorization where refresh_token_value = ?")) {
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
            return null;
        });
    }

    public static void invalidateRefreshToken(String refreshToken) {
        withConnectionVoid(conn -> {
            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    "update oauth2_authorization set refresh_token_value = null, refresh_token_issued_at = null, refresh_token_expires_at = null where refresh_token_value = ?")) {
                preparedStatement.setString(1, refreshToken);
                preparedStatement.execute();
            }
        });
    }

    public static void setConnection(Connection connection) {
        RegisteredClientJDBC.connection = connection;
    }

    public static void setHikariDataSource(HikariDataSource hikariDataSource) {
        RegisteredClientJDBC.hikariDataSource = hikariDataSource;
    }

    private static RegisteredClient mapRegisteredClient(ResultSet resultSet) throws SQLException, JsonProcessingException {
        RegisteredClient registeredClient = new RegisteredClient(resultSet.getString("id"));
        registeredClient.clientId = resultSet.getString("client_id");
        registeredClient.clientIdIssuedAt = resultSet.getTimestamp("client_id_issued_at").toInstant();
        registeredClient.clientSecret = resultSet.getString("client_secret");
        registeredClient.clientName = resultSet.getString("client_name");
        registeredClient.tokenSettings = readTokenSettings(resultSet.getString("token_settings"));
        return registeredClient;
    }

    private static void insertToken(
            Connection conn,
            String registeredClientId,
            String principalName,
            Timestamp accessTokenIssuedAt,
            Timestamp accessTokenExpiresAt,
            String accessTokenType,
            String accessTokenValue,
            String refreshTokenValue,
            Timestamp refreshTokenIssuedAt,
            Timestamp refreshTokenExpiresAt
    ) throws SQLException {
        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "insert into oauth2_authorization (registered_client_id, principal_name, access_token_issued_at, access_token_expires_at, access_token_type, access_token_value, refresh_token_value, refresh_token_issued_at, refresh_token_expires_at) values (?,?,?,?,?,?,?,?,?)")) {
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
    }

    private static <T> T withConnection(SqlFunction<T> action) {
        Connection conn = null;
        try {
            conn = getConnection();
            return action.apply(conn);
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            closeIfPooled(conn);
        }
    }

    private static void withConnectionVoid(SqlConsumer action) {
        withConnection(conn -> {
            action.accept(conn);
            return null;
        });
    }

    private static void withTransaction(SqlConsumer action) {
        Connection conn = null;
        boolean previousAutoCommit = true;
        try {
            conn = getConnection();
            previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                action.accept(conn);
                conn.commit();
            } catch (SQLException | JsonProcessingException | RuntimeException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    log.warn("Failed to rollback transaction", rollbackEx);
                }
                if (e instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                throw new RuntimeException(e);
            } finally {
                try {
                    conn.setAutoCommit(previousAutoCommit);
                } catch (SQLException e) {
                    log.warn("Failed to restore auto-commit", e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeIfPooled(conn);
        }
    }

    private static Connection getConnection() throws SQLException {
        if (RegisteredClientJDBC.hikariDataSource != null) {
            return RegisteredClientJDBC.hikariDataSource.getConnection();
        }
        return RegisteredClientJDBC.connection;
    }

    private static void closeIfPooled(Connection conn) {
        if (conn != null && RegisteredClientJDBC.hikariDataSource != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.debug("Failed to close pooled connection", e);
            }
        }
    }

    private static TokenSettings readTokenSettings(String tokenSettingsJson) throws JsonProcessingException {
        if (tokenSettingsJson == null || tokenSettingsJson.isBlank()) {
            return null;
        }
        return TOKEN_SETTINGS_MAPPER.readValue(tokenSettingsJson, TokenSettings.class);
    }

    private static void writeTokenSettings(PreparedStatement preparedStatement, int index, TokenSettings tokenSettings)
            throws SQLException, JsonProcessingException {
        if (tokenSettings == null) {
            preparedStatement.setNull(index, Types.VARCHAR);
            return;
        }
        preparedStatement.setString(index, TOKEN_SETTINGS_MAPPER.writeValueAsString(tokenSettings));
    }

    private static ObjectMapper createTokenSettingsMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper;
    }

}
