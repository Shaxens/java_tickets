package fr.arthur.devoir_java.controller;

import fr.arthur.devoir_java.dao.CategoryDao;
import fr.arthur.devoir_java.dao.PriorityDao;
import fr.arthur.devoir_java.dao.TicketDao;
import fr.arthur.devoir_java.dao.UserDao;
import fr.arthur.devoir_java.model.Category;
import fr.arthur.devoir_java.model.Priority;
import fr.arthur.devoir_java.model.Ticket;
import fr.arthur.devoir_java.security.AppUserDetails;
import fr.arthur.devoir_java.security.IsAdmin;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ticket")
@Tag(name = "CRUD ticket", description = "Permet de manipuler les tickets")
public class TicketController {

    @Autowired
    protected TicketDao ticketDao;

    @Autowired
    protected PriorityDao priorityDao;

    @Autowired
    protected CategoryDao categoryDao;

    @Autowired
    protected UserDao userDao;

    @GetMapping("/list")
    public List<Ticket> getAll() {
        return ticketDao.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> get(@PathVariable int id) {
        Optional<Ticket> ticket = ticketDao.findById(id);

        if (ticket.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(ticket.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody Ticket ticket) {
        try {
            if (ticket.getPriority() == null || ticket.getPriority().getId() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Optional<Priority> priority = priorityDao.findById(ticket.getPriority().getId());

            if (priority.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            ticket.setPriority(priority.get());

            // Gestion des catégories
            if (ticket.getCategories() != null && !ticket.getCategories().isEmpty()) {
                List<Integer> categoryIds = ticket.getCategories().stream().map(Category::getId).toList();
                List<Category> categories = categoryDao.findAllById(categoryIds);
                ticket.setCategories(categories);
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof AppUserDetails appUserDetails) {
                ticket.setSubmittingUser(appUserDetails.getUser());
            }

            ticket.setResolved(false);

            ticketDao.save(ticket);

            return new ResponseEntity<>(ticket, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @IsAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Ticket> delete(@PathVariable int id) {
        Optional<Ticket> ticket = ticketDao.findById(id);

        if (ticket.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ticketDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody @Validated(Ticket.update.class) Ticket ticketRequest) {

        Optional<Ticket> existingTicket = ticketDao.findById(id);

        if (existingTicket.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Ticket ticket = existingTicket.get();
            ticket.setId(ticketRequest.getId());
            ticket.setTitle(ticketRequest.getTitle());
            ticket.setDescription(ticketRequest.getDescription());

            // Update des priorités
            if (ticketRequest.getPriority() != null && ticketRequest.getPriority().getId() != null) {
                Optional<Priority> priority = priorityDao.findById(ticketRequest.getPriority().getId());
                if (priority.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                ticket.setPriority(priority.get());
            }

            // Update des catégories
            if (ticketRequest.getCategories() != null && !ticketRequest.getCategories().isEmpty()) {
                List<Integer> categoryIds = ticketRequest.getCategories().stream().map(Category::getId).toList();
                List<Category> categories = categoryDao.findAllById(categoryIds);

                ticket.setCategories(categories);
            }

            ticketDao.save(ticket);

            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/resolve")
    @IsAdmin
    public ResponseEntity<Ticket> resolve(@PathVariable int id) {
        Optional<Ticket> existingTicket = ticketDao.findById(id);

        if (existingTicket.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Ticket ticket = existingTicket.get();
        ticket.setResolved(true);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AppUserDetails appUserDetails) {
            ticket.setResolvingUser(appUserDetails.getUser());
        }

        ticketDao.save(ticket);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }
}
