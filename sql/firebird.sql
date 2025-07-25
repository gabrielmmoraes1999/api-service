CREATE TABLE OAUTH2_REGISTERED_CLIENT (
  ID VARCHAR(100) NOT NULL,
  CLIENT_ID VARCHAR(100) NOT NULL,
  CLIENT_ID_ISSUED_AT TIMESTAMP NOT NULL,
  CLIENT_SECRET VARCHAR(150) NOT NULL,
  CLIENT_NAME VARCHAR(100) NOT NULL,
  TOKEN_SETTINGS VARCHAR(255)
);

ALTER TABLE OAUTH2_REGISTERED_CLIENT
  ADD CONSTRAINT PK_OAUTH2_REGISTERED_CLIENT PRIMARY KEY (ID);

ALTER TABLE OAUTH2_REGISTERED_CLIENT
  ADD CONSTRAINT UNQ1_OAUTH2_REGISTERED_CLIENT UNIQUE (CLIENT_ID);

CREATE TABLE OAUTH2_AUTHORIZATION (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY,
    REGISTERED_CLIENT_ID VARCHAR(100) NOT NULL,
    PRINCIPAL_NAME VARCHAR(100) NOT NULL,
    ACCESS_TOKEN_ISSUED_AT TIMESTAMP NOT NULL,
    ACCESS_TOKEN_EXPIRES_AT TIMESTAMP NOT NULL,
    ACCESS_TOKEN_TYPE VARCHAR(50) NOT NULL,
    ACCESS_TOKEN_VALUE BLOB SUB_TYPE 1 SEGMENT SIZE 80 NOT NULL);

ALTER TABLE OAUTH2_AUTHORIZATION
  ADD CONSTRAINT PK_OAUTH2_AUTHORIZATION PRIMARY KEY (ID);

ALTER TABLE OAUTH2_AUTHORIZATION
  ADD CONSTRAINT FK_OAUTH2_AUTHORIZATION_1 FOREIGN KEY (REGISTERED_CLIENT_ID) REFERENCES OAUTH2_REGISTERED_CLIENT(ID);
