# api-service [![MIT License](https://img.shields.io/github/license/gabrielmmoraes1999/api-service.svg) ](https://github.com/gabrielmmoraes1999/api-service/blob/main/LICENSE) [![Maven Central](https://img.shields.io/maven-central/v/io.github.gabrielmmoraes1999/api-service.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.gabrielmmoraes1999/DBRepository)
Biblioteca Java para criação de api.

Importação da biblioteca:
- Maven :
```xml
<dependency>
    <groupId>io.github.gabrielmmoraes1999</groupId>
    <artifactId>api-service</artifactId>
    <version>3.1.0</version>
</dependency>
```

Veja a Wiki https://github.com/gabrielmmoraes1999/api-service/wiki, para ter um Tutorial Completo.

________________________________________________________________________________________________

# Histórico de Versões

## v3.1.0 - 17/07/2026
- Adicionado suporte opcional a `refresh_token` no endpoint OAuth2 (`grant_type=refresh_token`), configurável via `TokenSettings`.
- `TokenSettings` agora aceita configuração global via `@Bean` e sobrescrita por cliente no banco (`TOKEN_SETTINGS`):
  1. Se o cliente tiver `TOKEN_SETTINGS` preenchido no banco, usa o valor do banco.
  2. Se estiver nulo/vazio, usa o `TokenSettings` registrado como `@Bean`.
  3. Se nenhum Bean existir, usa o padrão interno (`accessTokenTimeToLive` de 1 hora).

Exemplo de Bean global:

```java
@Bean
public TokenSettings tokenSettings() {
    return TokenSettings.builder()
            .accessTokenTimeToLive(Duration.ofHours(6))
            .refreshTokenTimeToLive(Duration.ofDays(30))
            .refreshTokenAuthenticationMethod(RefreshTokenAuthenticationMethod.REFRESH_TOKEN_ONLY)
            .refreshTokenFormat(RefreshTokenFormat.OPAQUE_32_BASE64_URL)
            .build();
}
```

Para herdar o Bean, salve o cliente sem `.tokenSettings(...)` (grava `NULL` em `TOKEN_SETTINGS`).
Para sobrescrever um usuário específico, use `.tokenSettings(...)` ou `RegisteredClientJDBC.updateTokenSettings(...)`.
O formato do refresh token é definido em um único enum: `UUID`, `JWT`, ou variantes opacas
como `OPAQUE_32_BASE64_URL`, `OPAQUE_32_HEX`, `OPAQUE_32_BASE62`, `OPAQUE_64_BASE64_URL`,
`OPAQUE_64_HEX` e `OPAQUE_64_BASE62`. Quando omitido, o padrão é `UUID`. Em todos os formatos,
o token permanece persistido no banco para permitir rotação e revogação.

Com `RefreshTokenAuthenticationMethod.REFRESH_TOKEN_ONLY`, a renovação exige somente
`grant_type=refresh_token` e `refresh_token`. O token utilizado é invalidado após a renovação.

## v3.0.0 - 10/07/2026
- Removido suporte `Java 8` atualizado para `Java 21`.

## v2.2.3 - 13/01/2026
- Adicionado configuração `DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES`.

## v2.2.2 - 23/08/2025
- Corrigido bug na função `findById` no RegisteredClientJDBC.

## v2.2.1 - 23/07/2025
- Corrigido bug ao fechar as conexões JDBC.

## v2.2.0 - 20/07/2025
- Adicionado rotina para salvar o token gerado.

## v2.1.1 - 17/07/2025
- Melhoria no tratamento de campos timestamp.

## v2.1.0 - 16/07/2025
- Adicionado opção para configurar `TimeZone`.

## v2.0.1 - 13/07/2025
- Corrigido bug path com varios parametros.

## v2.0.0 - 12/07/2025
- Adicionado suporte a `WebSocket`.
- Melhorias na estrutura do código.

## v1.2.1 - 03/07/2025
- Corrigido bug no tratamento de campo do tipo `java.sql.Date`.

## v1.2.0 - 02/07/2025
- Adicionado suporte a autenticação OAuth2.

## v1.1.2 - 23/06/2025
- Adicionado baseClass no ComponentScan para poder informar as classes de forma manual.

## v1.1.1 - 13/06/2025
- Organização do 'run' do projeto.
- Adicionado para pegar o pacote de forma automático da classe informada.

## v1.1 - 06/06/2025
- Adicionado a anotação @ComponentScan possibilitando informar os pacotes com component.

## v1.0 - 04/06/2025
- Versão inicial
