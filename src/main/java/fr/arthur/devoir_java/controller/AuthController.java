package fr.arthur.devoir_java.controller;

import fr.arthur.devoir_java.dao.UserDao;
import fr.arthur.devoir_java.model.User;
import fr.arthur.devoir_java.security.AppUserDetails;
import fr.arthur.devoir_java.security.JwtUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Gestion de l'authentification")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        try {
            if (userDao.findByPseudo(user.getPseudo()).isPresent()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Ce pseudo est déjà utilisé");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            if (user.getPseudo() == null || user.getPseudo().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            user.setAdmin(false);

            userDao.save(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Utilisateur créé avec succès");
            response.put("pseudo", user.getPseudo());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de la création de l'utilisateur");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody User user) {
        try {
            Authentication authentication = authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getPseudo(), user.getPassword()
                    ));

            AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
            String jwt = jwtUtils.generateToken(userDetails);

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("type", "Bearer");
            response.put("pseudo", userDetails.getUsername());
            response.put("admin", userDetails.getUser().isAdmin());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Identifiants invalides");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Identifiants invalides");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof AppUserDetails userDetails) {
                User user = userDetails.getUser();

                Map<String, Object> profile = new HashMap<>();
                profile.put("pseudo", user.getPseudo());
                profile.put("admin", user.isAdmin());

                return ResponseEntity.ok(profile);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
