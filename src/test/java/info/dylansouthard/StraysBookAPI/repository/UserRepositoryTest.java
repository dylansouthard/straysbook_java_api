package info.dylansouthard.StraysBookAPI.repository;

import info.dylansouthard.StraysBookAPI.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // --- HELPER METHODS ---
    private User createValidUser() {
        return new User("Bing Bong", "bing@bong.com");
    }

    // --- TEST METHODS ---

    // 1. Basic CRUD Operations

    //Create
    @Test
    public void When_SavingValidUser_Expect_UserPersistedSuccessfully() {
        User user = createValidUser();
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId(), "ID should not be null after saving");
        assertEquals("Bing Bong", savedUser.getDisplayName(), "Display name should match");
    }


    @Test
    public void When_Finding_ValidUser_Expect_UserFoundSuccessfully() {
        User savedUser = userRepository.save(createValidUser());
        assertNotNull(userRepository.findById(savedUser.getId()).orElse(null));
    }

    @Test
    public void When_Finding_InvalidUser_Expect_UserNotFoundSuccessfully() {
        Optional<User> user = userRepository.findById(999L);
        assertTrue(user.isEmpty());
    }

    //2. Validation
    @Test
    public void When_SavingInvalidUser_Expect_UserNotPersistedSuccessfully() {
        User user = new User();
        assertThrows(Exception.class, () -> userRepository.save(user));
    }
    @Test
    public void When_Given_Duplicate_Email_Expect_ThrowException() {
        User user = userRepository.save(createValidUser());
        User newUser = new User("BobDole", user.getEmail());
        assertThrows(Exception.class, () -> userRepository.save(newUser));
    }

//    @Test
//    public void whenSavingUser_thenItIsPersistedAndRetrievable() {
//        // Arrange: Create a new User object
//        User user = new User("Bob Dole", "bob@dole.com");
//
//        // Act: Save the user
//        User savedUser = userRepository.save(user);
//
//        // Assert: Validate the saved user
//        assertNotNull(savedUser, "Saved user should not be null");
//        assertNotNull(savedUser.getId(), "User ID should not be null");
//
//        // Act: Retrieve the user by ID
//        User retrievedUser = userRepository.findById(savedUser.getId()).orElse(null);
//
//        // Assert: Validate the retrieved user
//        assertNotNull(retrievedUser, "Retrieved user should not be null");
//        assertEquals("Bob Dole", retrievedUser.getDisplayName(), "Display name should match");
//        assertEquals("bob@dole.com", retrievedUser.getEmail(), "Email should match");
//    }


}
