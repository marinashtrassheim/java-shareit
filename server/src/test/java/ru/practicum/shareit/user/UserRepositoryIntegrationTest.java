package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private UserEntity existingUser;

    @BeforeEach
    void setUp() {
        existingUser = UserEntity.builder()
                .name("Existing User")
                .email("existing@example.com")
                .build();
        entityManager.persist(existingUser);
        entityManager.flush();
    }

    @Test
    void existsByEmail_shouldReturnTrueForExistingEmail() {
        boolean exists = userRepository.existsByEmail("existing@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalseForNonExistingEmail() {
        boolean exists = userRepository.existsByEmail("nonexisting@example.com");
        assertThat(exists).isFalse();
    }

    @Test
    void saveUser_shouldGenerateId() {
        UserEntity newUser = UserEntity.builder()
                .name("New User")
                .email("new@example.com")
                .build();

        UserEntity saved = userRepository.save(newUser);
        entityManager.flush();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New User");
        assertThat(saved.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void findById_shouldReturnUserWhenExists() {
        UserEntity found = userRepository.findById(existingUser.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(existingUser.getId());
        assertThat(found.getName()).isEqualTo("Existing User");
        assertThat(found.getEmail()).isEqualTo("existing@example.com");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        boolean exists = userRepository.findById(999L).isPresent();
        assertThat(exists).isFalse();
    }
}