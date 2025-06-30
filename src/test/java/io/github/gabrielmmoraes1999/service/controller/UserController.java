package io.github.gabrielmmoraes1999.service.controller;

import io.github.gabrielmmoraes1999.apiservice.annotation.*;
import io.github.gabrielmmoraes1999.apiservice.http.ResponseEntity;
import io.github.gabrielmmoraes1999.apiservice.http.HttpStatus;
import io.github.gabrielmmoraes1999.apiservice.security.Authentication;
import io.github.gabrielmmoraes1999.apiservice.security.SecurityContextHolder;
import io.github.gabrielmmoraes1999.service.entity.Login;
import io.github.gabrielmmoraes1999.service.entity.User;
import io.github.gabrielmmoraes1999.service.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") int id) {
        return userService.findById(id);
    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@RequestBody User user, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Login login = (Login) authentication.getPrincipal();
        System.out.println(login.getUuid());
        return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.save(user), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") int id) {
        userService.delete(id);
        return new ResponseEntity<>("Registro excluído com sucesso!", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam("name") String name) {
        List<User> result = new ArrayList<>();
        for (User u : userService.getUsers()) {
            if (u.getName() != null && u.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(u);
            }
        }
        return result;
    }

}