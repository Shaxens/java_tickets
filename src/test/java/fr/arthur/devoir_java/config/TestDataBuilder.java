package fr.arthur.devoir_java.config;

import fr.arthur.devoir_java.model.Category;
import fr.arthur.devoir_java.model.Priority;
import fr.arthur.devoir_java.model.Ticket;
import fr.arthur.devoir_java.model.User;

public class TestDataBuilder {

    public static User createUser(String pseudo, boolean admin) {
        User user = new User();
        user.setPseudo(pseudo);
        user.setPassword("encodedPassword");
        user.setAdmin(admin);
        return user;
    }

    public static Priority createPriority(String name) {
        Priority priority = new Priority();
        priority.setName(name);
        return priority;
    }

    public static Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }

    public static Ticket createTicket(String titre, Priority priorite, User soumetteur) {
        Ticket ticket = new Ticket();
        ticket.setTitle(titre);
        ticket.setDescription("Description de test pour " + titre);
        ticket.setPriority(priorite);
        ticket.setSubmittingUser(soumetteur);
        ticket.setResolved(false);
        return ticket;
    }
}