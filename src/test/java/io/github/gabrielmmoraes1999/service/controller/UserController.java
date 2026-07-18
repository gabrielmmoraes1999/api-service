package io.github.gabrielmmoraes1999.service.controller;

import io.github.gabrielmmoraes1999.apiservice.annotation.*;
import io.github.gabrielmmoraes1999.apiservice.http.ResponseEntity;
import io.github.gabrielmmoraes1999.apiservice.http.HttpStatus;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RefreshTokenAuthenticationMethod;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RegisteredClient;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.RegisteredClientJDBC;
import io.github.gabrielmmoraes1999.apiservice.security.oauth2.TokenSettings;
import io.github.gabrielmmoraes1999.db.Repository;
import io.github.gabrielmmoraes1999.service.entity.User;
import io.github.gabrielmmoraes1999.service.repository.UserRepository;

import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepository = Repository.createRepository(UserRepository.class);

    @PostMapping("/user")
    public ResponseEntity<Object> create(@RequestBody User user) {
        user.setUuid(UUID.randomUUID().toString());

        // Sem tokenSettings: TOKEN_SETTINGS fica NULL e o Bean global é usado em runtime.
        RegisteredClient registeredClient = RegisteredClient
                .withId(user.getUuid())
                .clientId(user.getUsername())
                .clientName(user.getName())
                .clientSecret(user.getPassword())
                .build();

        user.setPassword(registeredClient.getClientSecret());

        RegisteredClientJDBC.save(registeredClient);
        return new ResponseEntity<>(userRepository.save(user), HttpStatus.CREATED);
    }

    @GetMapping("/user/{uuid}")
    public User findById(@PathVariable("uuid") String uuid) {
        return userRepository.findById(uuid);
    }

    @PutMapping("/user")
    public ResponseEntity<Object> update(@RequestBody User login) {
        // Sobrescreve apenas este usuário no banco; demais clientes continuam com o Bean.
        RegisteredClientJDBC.updateTokenSettings(customUserTokenSettings(), login.getUuid());
        return new ResponseEntity<>(login, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/user/{uuid}")
    public ResponseEntity<Object> delete(@PathVariable("uuid") String uuid) {
        userRepository.deleteById(uuid);
        RegisteredClientJDBC.delete(uuid);
        return new ResponseEntity<>("Registro excluído com sucesso!", HttpStatus.NO_CONTENT);
    }

    private static TokenSettings customUserTokenSettings() {
        return TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(12))
                .refreshTokenTimeToLive(Duration.ofDays(60))
                .refreshTokenAuthenticationMethod(RefreshTokenAuthenticationMethod.CLIENT_ID_ONLY)
                .build();
    }

}