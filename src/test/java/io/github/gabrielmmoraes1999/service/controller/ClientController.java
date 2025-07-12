package io.github.gabrielmmoraes1999.service.controller;

import io.github.gabrielmmoraes1999.apiservice.annotation.*;
import io.github.gabrielmmoraes1999.apiservice.http.HttpStatus;
import io.github.gabrielmmoraes1999.apiservice.http.ResponseEntity;
import io.github.gabrielmmoraes1999.db.Repository;
import io.github.gabrielmmoraes1999.service.entity.Client;
import io.github.gabrielmmoraes1999.service.repository.ClientRepository;

@RestController
@RequestMapping("/api")
public class ClientController {

    private final ClientRepository clientRepository = Repository.createRepository(ClientRepository.class);

    @PostMapping("/client")
    public ResponseEntity<Object> create(@RequestBody Client client) {
        return new ResponseEntity<>(clientRepository.save(client), HttpStatus.CREATED);
    }

    @GetMapping("/client/name/{id}")
    public String findNameById(@PathVariable("id") Integer id) {
        return clientRepository.findById(id).getName();
    }

    @GetMapping("/client/{id}")
    public Client findById(@PathVariable("id") Integer id) {
        return clientRepository.findById(id);
    }

    @PutMapping("/client")
    public ResponseEntity<Object> update(@RequestBody Client client) {
        return new ResponseEntity<>(clientRepository.save(client), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/client/{id}")
    public ResponseEntity<Object> delete(@PathVariable("uuid") Integer id) {
        clientRepository.deleteById(id);
        return new ResponseEntity<>("Registro excluído com sucesso!", HttpStatus.NO_CONTENT);
    }

}
