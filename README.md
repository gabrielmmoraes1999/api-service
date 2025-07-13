# api-service [![MIT License](https://img.shields.io/github/license/gabrielmmoraes1999/api-service.svg) ](https://github.com/gabrielmmoraes1999/api-service/blob/main/LICENSE) [![Maven Central](https://img.shields.io/maven-central/v/io.github.gabrielmmoraes1999/api-service.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.gabrielmmoraes1999/DBRepository)
Biblioteca Java para criação de api.

Importação da biblioteca:
- Maven :
```xml
<dependency>
    <groupId>io.github.gabrielmmoraes1999</groupId>
    <artifactId>api-service</artifactId>
    <version>2.0.1</version>
</dependency>
```

Veja a Wiki https://github.com/gabrielmmoraes1999/api-service/wiki, para ter um Tutorial Completo.

________________________________________________________________________________________________

# Histórico de Versões

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
