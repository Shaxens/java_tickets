package fr.arthur.devoir_java.integration.repository;

import fr.arthur.devoir_java.config.TestDataBuilder;
import fr.arthur.devoir_java.dao.UserDao;
import fr.arthur.devoir_java.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests du UserDao")
class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Doit sauvegarder et récupérer un utilisateur")
    void save_ShouldPersistUser_WhenValidUser() {
        // Given
        User user = TestDataBuilder.createUser("testuser", false);

        // When
        User savedUser = userDao.save(user);
        entityManager.flush();

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getPseudo()).isEqualTo("testuser");
        assertThat(savedUser.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("Doit trouver un utilisateur par pseudo")
    void findByPseudo_ShouldReturnUser_WhenExists() {
        // Given
        User user = TestDataBuilder.createUser("findme", true);
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userDao.findByPseudo("findme");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getPseudo()).isEqualTo("findme");
        assertThat(found.get().isAdmin()).isTrue();
    }

    @Test
    @DisplayName("Doit retourner vide si utilisateur n'existe pas")
    void findByPseudo_ShouldReturnEmpty_WhenNotExists() {
        // When
        Optional<User> found = userDao.findByPseudo("inexistant");

        // Then
        assertThat(found).isEmpty();
    }
}