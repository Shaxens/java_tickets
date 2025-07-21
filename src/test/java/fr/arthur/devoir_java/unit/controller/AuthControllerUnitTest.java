package fr.arthur.devoir_java.unit.controller;


import fr.arthur.devoir_java.controller.AuthController;
import fr.arthur.devoir_java.dao.UserDao;
import fr.arthur.devoir_java.model.User;
import fr.arthur.devoir_java.security.AppUserDetails;
import fr.arthur.devoir_java.security.JwtUtils;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires du AuthController")
class AuthControllerUnitTest {

    @Mock
    private UserDao mockUserDao;

    @Mock
    private AuthenticationProvider mockAuthenticationProvider;

    @Mock
    private JwtUtils mockJwtUtils;

    @Mock
    private PasswordEncoder mockPasswordEncoder;

    @InjectMocks
    private AuthController authController;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setPseudo("testuser");
        testUser.setPassword("plainPassword");
        testUser.setAdmin(false);
    }

    @Test
    @DisplayName("register - Doit créer un utilisateur avec succès quand données valides")
    void register_ShouldCreateUser_WhenValidData() {
        // Given
        when(mockUserDao.findByPseudo("testuser")).thenReturn(Optional.empty());
        when(mockPasswordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(mockUserDao.save(any(User.class))).thenReturn(testUser);

        // When
        ResponseEntity<?> response = authController.register(testUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("message")).isEqualTo("Utilisateur créé avec succès");
        assertThat(body.get("pseudo")).isEqualTo("testuser");

        verify(mockUserDao).findByPseudo("testuser");
        verify(mockPasswordEncoder).encode("plainPassword");
        verify(mockUserDao).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPseudo()).isEqualTo("testuser");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("register - Doit rejeter si le pseudo existe déjà")
    void register_ShouldRejectConflict_WhenPseudoExists() {
        // Given
        User existingUser = new User();
        existingUser.setPseudo("testuser");
        when(mockUserDao.findByPseudo("testuser")).thenReturn(Optional.of(existingUser));

        // When
        ResponseEntity<?> response = authController.register(testUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("error")).isEqualTo("Ce pseudo est déjà utilisé");

        verify(mockUserDao).findByPseudo("testuser");
        verify(mockUserDao, never()).save(any(User.class));
        verify(mockPasswordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("register - Doit gérer les exceptions lors de la sauvegarde")
    void register_ShouldHandleException_WhenSaveThrows() {
        // Given
        when(mockUserDao.findByPseudo("testuser")).thenReturn(Optional.empty());
        when(mockPasswordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(mockUserDao.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<?> response = authController.register(testUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("error")).isEqualTo("Erreur lors de la création de l'utilisateur");
    }

    @Test
    @DisplayName("login - Doit authentifier avec succès quand identifiants valides")
    void login_ShouldAuthenticateSuccessfully_WhenValidCredentials() {
        // Given
        User loginUser = new User();
        loginUser.setPseudo("testuser");
        loginUser.setPassword("plainPassword");

        AppUserDetails mockUserDetails = new AppUserDetails(testUser);
        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken(
                mockUserDetails, null, mockUserDetails.getAuthorities());

        when(mockAuthenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(mockJwtUtils.generateToken(mockUserDetails)).thenReturn("mock-jwt-token");

        // When
        ResponseEntity<?> response = authController.login(loginUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.get("token")).isEqualTo("mock-jwt-token");
        assertThat(body.get("type")).isEqualTo("Bearer");
        assertThat(body.get("pseudo")).isEqualTo("testuser");
        assertThat(body.get("admin")).isEqualTo(false);

        verify(mockAuthenticationProvider).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(mockJwtUtils).generateToken(mockUserDetails);
    }

    @Test
    @DisplayName("login - Doit rejeter avec des identifiants invalides")
    void login_ShouldRejectUnauthorized_WhenInvalidCredentials() {
        // Given
        User loginUser = new User();
        loginUser.setPseudo("testuser");
        loginUser.setPassword("wrongpassword");

        when(mockAuthenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When
        ResponseEntity<?> response = authController.login(loginUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("error")).isEqualTo("Identifiants invalides");

        verify(mockAuthenticationProvider).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(mockJwtUtils, never()).generateToken(any());
    }

    @Test
    @DisplayName("login - Doit gérer les exceptions génériques")
    void login_ShouldHandleGenericException_WhenAuthenticationThrows() {
        // Given
        User loginUser = new User();
        loginUser.setPseudo("testuser");
        loginUser.setPassword("password");

        when(mockAuthenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When
        ResponseEntity<?> response = authController.login(loginUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("error")).isEqualTo("Identifiants invalides");
    }

    @Test
    @DisplayName("register - Doit gérer un pseudo null")
    void register_ShouldHandleNullPseudo() {
        // Given
        User userWithNullPseudo = new User();
        userWithNullPseudo.setPseudo(null);
        userWithNullPseudo.setPassword("password");

        // When
        ResponseEntity<?> response = authController.register(userWithNullPseudo);

        // Then
        assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("register - Doit correctement encoder différents types de mots de passe")
    void register_ShouldEncodeVariousPasswords() {
        // Given
        String[] passwords = {"simple", "complex!@#$", "très-long-mot-de-passe-avec-beaucoup-de-caractères", "123", ""};

        for (String password : passwords) {
            User user = new User();
            user.setPseudo("user_" + password.hashCode());
            user.setPassword(password);

            when(mockUserDao.findByPseudo(anyString())).thenReturn(Optional.empty());
            when(mockPasswordEncoder.encode(password)).thenReturn("encoded_" + password);
            when(mockUserDao.save(any(User.class))).thenReturn(user);

            // When
            ResponseEntity<?> response = authController.register(user);

            // Then
            if (!password.isEmpty()) {
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                verify(mockPasswordEncoder).encode(password);
            }

            reset(mockUserDao, mockPasswordEncoder);
        }
    }

    @Test
    @DisplayName("login - Doit créer le bon token d'authentification")
    void login_ShouldCreateCorrectAuthenticationToken() {
        // Given
        User loginUser = new User();
        loginUser.setPseudo("testuser");
        loginUser.setPassword("password");

        AppUserDetails mockUserDetails = new AppUserDetails(testUser);
        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken(
                mockUserDetails, null, mockUserDetails.getAuthorities());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        when(mockAuthenticationProvider.authenticate(tokenCaptor.capture()))
                .thenReturn(mockAuthentication);
        when(mockJwtUtils.generateToken(any())).thenReturn("token");

        // When
        authController.login(loginUser);

        // Then
        UsernamePasswordAuthenticationToken capturedToken = tokenCaptor.getValue();
        assertThat(capturedToken.getPrincipal()).isEqualTo("testuser");
        assertThat(capturedToken.getCredentials()).isEqualTo("password");
    }
}