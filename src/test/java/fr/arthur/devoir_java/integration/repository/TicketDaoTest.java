package fr.arthur.devoir_java.integration.repository;

import fr.arthur.devoir_java.config.TestDataBuilder;
import fr.arthur.devoir_java.dao.TicketDao;
import fr.arthur.devoir_java.model.Category;
import fr.arthur.devoir_java.model.Priority;
import fr.arthur.devoir_java.model.Ticket;
import fr.arthur.devoir_java.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@Rollback
@DisplayName("Tests du TicketDao - Repository avec relations")
class TicketDaoTest {

    @Autowired
    private TicketDao ticketDao;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;
    private Priority testPriority;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Créer les données de base avec des noms UNIQUES par test
        String uniqueSuffix = String.valueOf(System.nanoTime());

        testUser = TestDataBuilder.createUser("testuser_" + uniqueSuffix, false);
        testPriority = TestDataBuilder.createPriority("Haute_" + uniqueSuffix);
        testCategory = TestDataBuilder.createCategory("Matériel_" + uniqueSuffix);

        // Les persister
        entityManager.persistAndFlush(testUser);
        entityManager.persistAndFlush(testPriority);
        entityManager.persistAndFlush(testCategory);
    }

    @Test
    @DisplayName("Doit sauvegarder un ticket avec ses relations")
    void save_ShouldPersistTicketWithRelations() {
        // Given
        Ticket ticket = TestDataBuilder.createTicket("Ticket de test", testPriority, testUser);
        ticket.setCategories(List.of(testCategory));

        // When
        Ticket savedTicket = ticketDao.save(ticket);
        entityManager.flush();

        // Then
        assertThat(savedTicket.getId()).isNotNull();
        assertThat(savedTicket.getTitle()).isEqualTo("Ticket de test");
        assertThat(savedTicket.getPriority().getName()).startsWith("Haute_");
        assertThat(savedTicket.getSubmittingUser().getPseudo()).startsWith("testuser_");
        assertThat(savedTicket.getCategories()).hasSize(1);
        assertThat(savedTicket.getCategories().get(0).getName()).startsWith("Matériel_");
        assertThat(savedTicket.isResolved()).isFalse();
    }

    @Test
    @DisplayName("Doit pouvoir récupérer un ticket avec toutes ses relations")
    void findById_ShouldLoadAllRelations() {
        // Given
        Ticket ticket = TestDataBuilder.createTicket("Test relations", testPriority, testUser);
        ticket.setCategories(List.of(testCategory));
        Ticket savedTicket = entityManager.persistAndFlush(ticket);

        // When
        Optional<Ticket> found = ticketDao.findById(savedTicket.getId());

        // Then
        assertThat(found).isPresent();
        Ticket foundTicket = found.get();

        // Vérifier que toutes les relations sont chargées
        assertThat(foundTicket.getPriority()).isNotNull();
        assertThat(foundTicket.getPriority().getName()).startsWith("Haute_");
        assertThat(foundTicket.getSubmittingUser()).isNotNull();
        assertThat(foundTicket.getSubmittingUser().getPseudo()).startsWith("testuser_");
        assertThat(foundTicket.getCategories()).isNotEmpty();
        assertThat(foundTicket.getCategories().get(0).getName()).startsWith("Matériel_");
    }

    @Test
    @DisplayName("Doit pouvoir marquer un ticket comme résolu")
    void update_ShouldMarkTicketAsResolved() {
        // Given
        Ticket ticket = TestDataBuilder.createTicket("À résoudre", testPriority, testUser);
        Ticket savedTicket = entityManager.persistAndFlush(ticket);

        // When
        savedTicket.setResolved(true);
        savedTicket.setResolvingUser(testUser);
        Ticket updatedTicket = ticketDao.save(savedTicket);
        entityManager.flush();

        // Then
        assertThat(updatedTicket.isResolved()).isTrue();
        assertThat(updatedTicket.getResolvingUser()).isNotNull();
        assertThat(updatedTicket.getResolvingUser().getPseudo()).startsWith("testuser_");
    }
}