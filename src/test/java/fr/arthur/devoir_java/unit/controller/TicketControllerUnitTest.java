package fr.arthur.devoir_java.unit.controller;

import fr.arthur.devoir_java.controller.TicketController;
import fr.arthur.devoir_java.dao.CategoryDao;
import fr.arthur.devoir_java.dao.PriorityDao;
import fr.arthur.devoir_java.dao.TicketDao;
import fr.arthur.devoir_java.dao.UserDao;
import fr.arthur.devoir_java.model.Category;
import fr.arthur.devoir_java.model.Priority;
import fr.arthur.devoir_java.model.Ticket;
import fr.arthur.devoir_java.model.User;
import fr.arthur.devoir_java.security.AppUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires du TicketController")
class TicketControllerUnitTest {

    @Mock
    private TicketDao mockTicketDao;

    @Mock
    private PriorityDao mockPriorityDao;

    @Mock
    private CategoryDao mockCategoryDao;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private SecurityContext mockSecurityContext;

    @Mock
    private Authentication mockAuthentication;

    @InjectMocks
    private TicketController ticketController;

    @Captor
    private ArgumentCaptor<Ticket> ticketCaptor;

    private Ticket testTicket;
    private Priority testPriority;
    private Category testCategory1, testCategory2;
    private User testUser;
    private AppUserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        // Initialisation des objets de test
        testPriority = new Priority();
        testPriority.setId(1);
        testPriority.setName("High");

        testCategory1 = new Category();
        testCategory1.setId(1);
        testCategory1.setName("Bug");

        testCategory2 = new Category();
        testCategory2.setId(2);
        testCategory2.setName("Feature");

        testUser = new User();
        testUser.setId(1);
        testUser.setPseudo("testuser");
        testUser.setAdmin(false);

        testUserDetails = new AppUserDetails(testUser);

        testTicket = new Ticket();
        testTicket.setId(1);
        testTicket.setTitle("Test Ticket");
        testTicket.setDescription("Description du ticket de test");
        testTicket.setPriority(testPriority);
        testTicket.setCategories(Collections.singletonList(testCategory1));
        testTicket.setSubmittingUser(testUser);
        testTicket.setResolved(false);
    }

    @Test
    @DisplayName("getAll - Doit retourner tous les tickets")
    void getAll_ShouldReturnAllTickets() {
        // Given
        List<Ticket> expectedTickets = Collections.singletonList(testTicket);
        when(mockTicketDao.findAll()).thenReturn(expectedTickets);

        // When
        List<Ticket> result = ticketController.getAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(testTicket);
        verify(mockTicketDao).findAll();
    }

    @Test
    @DisplayName("getAll - Doit retourner une liste vide quand aucun ticket")
    void getAll_ShouldReturnEmptyList_WhenNoTickets() {
        // Given
        when(mockTicketDao.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Ticket> result = ticketController.getAll();

        // Then
        assertThat(result).isEmpty();
        verify(mockTicketDao).findAll();
    }

    @Test
    @DisplayName("get - Doit retourner le ticket quand ID existe")
    void get_ShouldReturnTicket_WhenIdExists() {
        // Given
        when(mockTicketDao.findById(1)).thenReturn(Optional.of(testTicket));

        // When
        ResponseEntity<Ticket> response = ticketController.get(1);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testTicket);
        verify(mockTicketDao).findById(1);
    }

    @Test
    @DisplayName("get - Doit retourner NOT_FOUND quand ID n'existe pas")
    void get_ShouldReturnNotFound_WhenIdDoesNotExist() {
        // Given
        when(mockTicketDao.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Ticket> response = ticketController.get(999);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(mockTicketDao).findById(999);
    }

    @Test
    @DisplayName("save - Doit créer un ticket avec succès")
    void save_ShouldCreateTicket_WhenValidData() {
        // Given
        Ticket newTicket = new Ticket();
        newTicket.setTitle("Nouveau Ticket");
        newTicket.setDescription("Description");
        newTicket.setPriority(testPriority);
        newTicket.setCategories(Collections.singletonList(testCategory1));

        when(mockPriorityDao.findById(1)).thenReturn(Optional.of(testPriority));
        when(mockCategoryDao.findAllById(List.of(1))).thenReturn(Collections.singletonList(testCategory1));
        when(mockTicketDao.save(any(Ticket.class))).thenReturn(newTicket);

        try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
            securityMock.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);
            when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
            when(mockAuthentication.getPrincipal()).thenReturn(testUserDetails);

            // When
            ResponseEntity<?> response = ticketController.save(newTicket);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isEqualTo(newTicket);

            verify(mockPriorityDao).findById(1);
            verify(mockCategoryDao).findAllById(List.of(1));
            verify(mockTicketDao).save(ticketCaptor.capture());

            Ticket savedTicket = ticketCaptor.getValue();
            assertThat(savedTicket.getTitle()).isEqualTo("Nouveau Ticket");
            assertThat(savedTicket.getSubmittingUser()).isEqualTo(testUser);
            assertThat(savedTicket.isResolved()).isFalse();
            assertThat(savedTicket.getPriority()).isEqualTo(testPriority);
            assertThat(savedTicket.getCategories()).containsExactly(testCategory1);
        }
    }


    @Test
    @DisplayName("save - Doit créer ticket sans catégories")
    void save_ShouldCreateTicket_WhenNoCategoriesProvided() {
        // Given
        Ticket newTicket = new Ticket();
        newTicket.setTitle("Ticket sans catégories");
        newTicket.setPriority(testPriority);
        newTicket.setCategories(null);

        when(mockPriorityDao.findById(1)).thenReturn(Optional.of(testPriority));
        when(mockTicketDao.save(any(Ticket.class))).thenReturn(newTicket);

        try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
            securityMock.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);
            when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
            when(mockAuthentication.getPrincipal()).thenReturn(testUserDetails);

            // When
            ResponseEntity<?> response = ticketController.save(newTicket);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            verify(mockCategoryDao, never()).findAllById(anyList());
        }
    }

    @Test
    @DisplayName("delete - Doit supprimer le ticket quand ID existe")
    void delete_ShouldDeleteTicket_WhenIdExists() {
        // Given
        when(mockTicketDao.findById(1)).thenReturn(Optional.of(testTicket));

        // When
        ResponseEntity<Ticket> response = ticketController.delete(1);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(mockTicketDao).findById(1);
        verify(mockTicketDao).deleteById(1);
    }

    @Test
    @DisplayName("update - Doit permettre la mise à jour sans priorité")
    void update_ShouldAllowUpdateWithoutPriority() {
        // Given
        Ticket updateRequest = new Ticket();
        updateRequest.setTitle("Titre mis à jour sans priorité");
        updateRequest.setPriority(null);

        when(mockTicketDao.findById(1)).thenReturn(Optional.of(testTicket));
        when(mockTicketDao.save(any(Ticket.class))).thenReturn(testTicket);

        // When
        ResponseEntity<?> response = ticketController.update(1, updateRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(mockPriorityDao, never()).findById(anyInt());
    }

    @Test
    @DisplayName("update - Doit permettre la mise à jour sans catégories")
    void update_ShouldAllowUpdateWithoutCategories() {
        // Given
        Ticket updateRequest = new Ticket();
        updateRequest.setTitle("Titre mis à jour sans catégories");
        updateRequest.setCategories(null);

        when(mockTicketDao.findById(1)).thenReturn(Optional.of(testTicket));
        when(mockTicketDao.save(any(Ticket.class))).thenReturn(testTicket);

        // When
        ResponseEntity<?> response = ticketController.update(1, updateRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(mockCategoryDao, never()).findAllById(anyList());
    }
}