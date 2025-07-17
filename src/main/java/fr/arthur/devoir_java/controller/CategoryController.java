package fr.arthur.devoir_java.controller;

import fr.arthur.devoir_java.dao.CategoryDao;
import fr.arthur.devoir_java.model.Category;
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
@RequestMapping("/api/category")
@Tag(name = "CRUD catégorie")
public class CategoryController {

    @Autowired
    protected CategoryDao categoryDao;

    @GetMapping("/list")
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable int id) {
        Optional<Category> category = categoryDao.findById(id);

        if (category.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(category.get(), HttpStatus.OK);
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<Category> save(@RequestBody @Validated(Category.save.class) Category category) {
        categoryDao.save(category);

        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @IsAdmin
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody @Validated(Category.update.class) Category category) {
        Optional<Category> oldCategory = categoryDao.findById(id);

        if (oldCategory.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        category.setId(id);
        categoryDao.save(category);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<?> delete(@PathVariable int id) {
        Optional<Category> oldCategory = categoryDao.findById(id);

        if (oldCategory.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        categoryDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
