package fr.arthur.devoir_java.controller;

import fr.arthur.devoir_java.dao.PriorityDao;
import fr.arthur.devoir_java.model.Priority;
import fr.arthur.devoir_java.security.IsAdmin;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/priority")
@Tag(name = "CRUD priorité")
public class PriorityController {

    @Autowired
    protected PriorityDao priorityDao;

    @GetMapping("/list")
    public List<Priority> getAllPriorities() {
        return priorityDao.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Priority> getPriorityById(@PathVariable int id) {
        Optional<Priority> priority = priorityDao.findById(id);

        if (priority.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(priority.get(), HttpStatus.OK);
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<Priority> save(@RequestBody @Validated(Priority.save.class) Priority priority) {
        priorityDao.save(priority);

        return new ResponseEntity<>(priority, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @IsAdmin
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody @Validated(Priority.update.class) Priority priority) {
        Optional<Priority> oldPriority = priorityDao.findById(id);

        if (oldPriority.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        priority.setId(id);
        priorityDao.save(priority);

        return new ResponseEntity<>(priority, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Priority> delete(@PathVariable int id) {
        Optional<Priority> priority = priorityDao.findById(id);

        if (priority.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        priorityDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
