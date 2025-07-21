package fr.arthur.devoir_java.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.arthur.devoir_java.dao.UserDao;
import fr.arthur.devoir_java.model.User;
import fr.arthur.devoir_java.security.IsAdmin;
import fr.arthur.devoir_java.view.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@Tag(name = "CRUD utilisateur", description = "Permet de manipuler l'entité utilisateur")
@IsAdmin
public class UserController {

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @GetMapping("/list")
    @JsonView(UserView.class)
    public List<User> getAll() {
        return userDao.findAll();
    }

    @GetMapping("/{id}")
    @JsonView(UserView.class)
    public ResponseEntity<User> get(@PathVariable int id) {

        Optional<User> optionalUser = userDao.findById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            //return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(optionalUser.get(), HttpStatus.OK);
    }

    @PostMapping
    @Operation(
            summary = "Permet de persister un utilisateur",
            description = "Enregistre dans la base de donnée un utilisateur issu du JSON intégré au corp de la requête")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "L'utilisateur a été ajouté avec succès"),
            @ApiResponse(responseCode = "409", description = "L'opération a échouée car l'email est en doublon, ou le role absent"),
            @ApiResponse(responseCode = "400", description = "L'email ou le mot de passe est vide ou absent"),
    })
    public ResponseEntity<User> add(
            @RequestBody @Validated(User.add.class) User userSent) {

        userSent.setPassword(passwordEncoder.encode(userSent.getPassword()));

        userDao.save(userSent);

        return new ResponseEntity<>(userSent, HttpStatus.CREATED);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {

        Optional<User> optionalUser = userDao.findById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        userDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable int id,
            @RequestBody @Validated(User.update.class) User userSent) {

        userSent.setId(id);

        Optional<User> optionalUser = userDao.findById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        userSent.setPassword(optionalUser.get().getPassword());

        userDao.save(userSent);

        return new ResponseEntity<>(userSent, HttpStatus.OK);

    }

}