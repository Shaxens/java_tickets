package fr.arthur.devoir_java.unit.controller;

import fr.arthur.devoir_java.controller.UserController;
import fr.arthur.devoir_java.dao.UserDao;
import fr.arthur.devoir_java.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires du UserController")
class UserControllerUnitTest {

    @Mock
    private UserDao mockUserDao;

    @Mock
    private PasswordEncoder mockPasswordEncoder;

    @InjectMocks
    private UserController userController;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User testUser;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setPseudo("testuser");
        testUser.setPassword("plainPassword");
        testUser.setAdmin(false);

        testUser2 = new User();
        testUser2.setId(2);
        testUser2.setPseudo("testuser2");
        testUser2.setPassword("plainPassword2");
        testUser2.setAdmin(true);
    }

    @Test
    @DisplayName("getAll - Doit retourner tous les utilisateurs")
    void getAll_ShouldReturnAllUsers() {
        // Given
        List<User> expectedUsers = Arrays.asList(testUser, testUser2);
        when(mockUserDao.findAll()).thenReturn(expectedUsers);

        // When
        List<User> result = userController.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testUser, testUser2);
        verify(mockUserDao).findAll();
    }

    @Test
    @DisplayName("getAll - Doit retourner une liste vide quand aucun utilisateur")
    void getAll_ShouldReturnEmptyList_WhenNoUsers() {
        // Given
        when(mockUserDao.findAll()).thenReturn(Collections.emptyList());

        // When
        List<User> result = userController.getAll();

        // Then
        assertThat(result).isEmpty();
        verify(mockUserDao).findAll();
    }

    @Test
    @DisplayName("get - Doit retourner l'utilisateur quand ID existe")
    void get_ShouldReturnUser_WhenIdExists() {
        // Given
        when(mockUserDao.findById(1)).thenReturn(Optional.of(testUser));

        // When
        ResponseEntity<User> response = userController.get(1);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testUser);
        verify(mockUserDao).findById(1);
    }

    @Test
    @DisplayName("get - Doit retourner NOT_FOUND quand ID n'existe pas")
    void get_ShouldReturnNotFound_WhenIdDoesNotExist() {
        // Given
        when(mockUserDao.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<User> response = userController.get(999);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(mockUserDao).findById(999);
    }

    @Test
    @DisplayName("add - Doit créer un utilisateur avec mot de passe encodé")
    void add_ShouldCreateUser_WithEncodedPassword() {
        // Given
        User newUser = new User();
        newUser.setPseudo("newuser");
        newUser.setPassword("plainPassword");

        when(mockPasswordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(mockUserDao.save(any(User.class))).thenReturn(newUser);

        // When
        ResponseEntity<User> response = userController.add(newUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(newUser);

        verify(mockPasswordEncoder).encode("plainPassword");
        verify(mockUserDao).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getPseudo()).isEqualTo("newuser");
    }

    @Test
    @DisplayName("delete - Doit supprimer l'utilisateur quand ID existe")
    void delete_ShouldDeleteUser_WhenIdExists() {
        // Given
        when(mockUserDao.findById(1)).thenReturn(Optional.of(testUser));

        // When
        ResponseEntity<?> response = userController.delete(1);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();

        verify(mockUserDao).findById(1);
        verify(mockUserDao).deleteById(1);
    }

    @Test
    @DisplayName("delete - Doit retourner NOT_FOUND quand ID n'existe pas")
    void delete_ShouldReturnNotFound_WhenIdDoesNotExist() {
        // Given
        when(mockUserDao.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = userController.delete(999);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(mockUserDao).findById(999);
        verify(mockUserDao, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("update - Doit mettre à jour l'utilisateur quand ID existe")
    void update_ShouldUpdateUser_WhenIdExists() {
        // Given
        User updatedUser = new User();
        updatedUser.setPseudo("updateduser");
        updatedUser.setPassword("newPassword"); // Ce mot de passe sera ignoré
        updatedUser.setAdmin(true);

        when(mockUserDao.findById(1)).thenReturn(Optional.of(testUser));
        when(mockUserDao.save(any(User.class))).thenReturn(updatedUser);

        // When
        ResponseEntity<?> response = userController.update(1, updatedUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedUser);

        verify(mockUserDao).findById(1);
        verify(mockUserDao).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getId()).isEqualTo(1); // ID défini par la méthode
        assertThat(savedUser.getPseudo()).isEqualTo("updateduser");
        assertThat(savedUser.getPassword()).isEqualTo("plainPassword"); // Mot de passe original préservé
        assertThat(savedUser.isAdmin()).isTrue();
    }


    @Test
    @DisplayName("update - Doit préserver le mot de passe original")
    void update_ShouldPreserveOriginalPassword() {
        // Given
        User updatedUser = new User();
        updatedUser.setPseudo("updateduser");
        updatedUser.setPassword("nouveauMotDePasse"); // Sera ignoré

        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setPseudo("olduser");
        existingUser.setPassword("ancienMotDePasse");

        when(mockUserDao.findById(1)).thenReturn(Optional.of(existingUser));
        when(mockUserDao.save(any(User.class))).thenReturn(updatedUser);

        // When
        userController.update(1, updatedUser);

        // Then
        verify(mockUserDao).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo("ancienMotDePasse");
    }

    @Test
    @DisplayName("get - Doit gérer les ID négatifs")
    void get_ShouldHandleNegativeIds() {
        // Given
        when(mockUserDao.findById(-1)).thenReturn(Optional.empty());

        // When
        ResponseEntity<User> response = userController.get(-1);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(mockUserDao).findById(-1);
    }

    @Test
    @DisplayName("delete - Doit gérer les ID négatifs")
    void delete_ShouldHandleNegativeIds() {
        // Given
        when(mockUserDao.findById(-1)).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = userController.delete(-1);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(mockUserDao).findById(-1);
    }

    @Test
    @DisplayName("add - Doit gérer les utilisateurs avec des propriétés nulles")
    void add_ShouldHandleUsersWithNullProperties() {
        // Given
        User userWithNulls = new User();
        userWithNulls.setPseudo(null);
        userWithNulls.setPassword("password");

        when(mockPasswordEncoder.encode(anyString())).thenReturn("encoded");
        when(mockUserDao.save(any(User.class))).thenReturn(userWithNulls);

        // When
        ResponseEntity<User> response = userController.add(userWithNulls);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(mockPasswordEncoder).encode("password");
    }
}
