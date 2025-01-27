package info.dylansouthard.StraysBookAPI.repository;

import info.dylansouthard.StraysBookAPI.exception.DuplicateOAuthProviderException;
import info.dylansouthard.StraysBookAPI.model.enums.AuthTokenType;
import info.dylansouthard.StraysBookAPI.model.enums.OAuthProviderType;
import info.dylansouthard.StraysBookAPI.model.user.AuthToken;
import info.dylansouthard.StraysBookAPI.model.user.OAuthProvider;
import info.dylansouthard.StraysBookAPI.model.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    private User validUser;

    // --- HELPER METHODS ---

    @BeforeEach
    public void createValidUser() {
        this.validUser = new User("Bing Bong", "bing@bong.com");
    }

    public AuthToken createAuthToken(String token) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusHours(1);

        // Add the first token
       return new AuthToken(AuthTokenType.ACCESS, token, now, later, "device123");
    }


//    @AfterEach
//    public void tearDown() {
//        this.userRepository.deleteAll();
//        entityManager.clear();
//        this.validUser = null;
//    }

    // --- TEST METHODS ---

    // 1. Basic CRUD Operations

    //Create
    @Test
    public void When_SavingValidUser_Expect_UserPersistedSuccessfully() {

        User savedUser = userRepository.save(validUser);
        assertNotNull(savedUser.getId(), "ID should not be null after saving");
        assertEquals("Bing Bong", savedUser.getDisplayName(), "Display name should match");
    }


    //Retrieve
    @Test
    public void When_Finding_ValidUser_Expect_UserFoundSuccessfully() {
        User savedUser = userRepository.save(validUser);
        assertTrue(userRepository.findActiveById(savedUser.getId()).isPresent(), "User should be found");
    }

    @Test
    public void When_Finding_InvalidUser_Expect_UserNotFoundSuccessfully() {
        Optional<User> user = userRepository.findActiveById(999L);
        assertTrue(user.isEmpty());
    }

    //Update
    @Test
    public void When_UpdatingValidUser_Expect_UserUpdatedSuccessfully() {
        User user = userRepository.save(validUser);
        user.setDisplayName("New");
        user.setEmail("new@new.com");
        User savedUser = userRepository.save(user);
        assertAll("User properties",
                () -> assertEquals("New", savedUser.getDisplayName(), "Display name should match"),
                () -> assertEquals("new@new.com", savedUser.getEmail(), "Email should match")
        );
    }

    //Delete
    @Test
    public void When_Deleting_ValidUser_Expect_UserDeletedSuccessfully() {

        User user = userRepository.save(validUser);
        Long id = user.getId();
        userRepository.delete(user);
        assertTrue(userRepository.findById(id).isEmpty(), "User should not exist after deletion");

        User secondUser = userRepository.save(new User("name", "email@email.com"));
        secondUser.setIsDeleted(true);
        userRepository.save(secondUser);
        userRepository.flush();

        assertTrue(userRepository.findActiveById(secondUser.getId()).isEmpty(), "User should not exist after deletion");
    }


    //2. Validation
    @Test
    public void When_SavingInvalidUser_Expect_UserNotPersistedSuccessfully() {
        User user = new User();
        assertThrows(Exception.class, () -> userRepository.save(user));
    }

    @Test
    public void When_Given_Duplicate_Email_Expect_ThrowException() {
        User user = userRepository.save(validUser);
        User newUser = new User("BobDole", user.getEmail());
        assertThrows(Exception.class, () -> userRepository.save(newUser));
    }

    @Test
    public void When_UpdatingDuplicateEmail_Expect_ThrowException() {
        User user = userRepository.save(validUser);
        User newUser = userRepository.save(new User("New", "new@new.com"));
        newUser.setEmail(user.getEmail());

        assertThrows(Exception.class, () -> {
            userRepository.save(newUser);
            userRepository.flush();
        });
    }

    @Test
    public void When_UpdatingNullDisplayName_Expect_ThrowException() {
        User user = userRepository.save(validUser);
        user.setDisplayName(null);
        assertThrows(Exception.class, () -> {
            userRepository.save(user);
            userRepository.flush();
        });
    }

    // 2. Relationships
    //User authentication

    @Test
    public void When_UpdatingOAuthProviders_Expect_OAthProviderCreatedSuccessfully() {
        User user = userRepository.save(validUser);
        user.addOAuthProvider(new OAuthProvider(OAuthProviderType.GOOGLE, "123google"));
        User savedUser = userRepository.save(user);
        assertEquals("123google", savedUser.getOAuthProviders().getFirst().getProviderUserId(), "OAuthProviderID should match saved id");
    }

    @Test
    public void When_RemovingOAuthProvider_Expect_ProviderRemovedFromDB() {
        //Save the user and add an OAuthProvider
        User user = userRepository.save(validUser);
        OAuthProvider provider = new OAuthProvider(OAuthProviderType.GOOGLE, "123google");
        user.addOAuthProvider(provider);
        userRepository.saveAndFlush(user);

        //Verify the provider exists in the database
        User savedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(1, savedUser.getOAuthProviders().size(), "User should have 1 OAuth provider");
        assertEquals("123google", savedUser.getOAuthProviders().getFirst().getProviderUserId(), "OAuthProviderID should match");

        // Remove the provider and save the user
        savedUser.getOAuthProviders().clear();
        userRepository.saveAndFlush(savedUser);

        //Verify the provider is removed from the database using EntityManager
        String jpql = "SELECT COUNT(p) FROM OAuthProvider p WHERE p.providerUserId = :providerUserId";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("providerUserId", "123google")
                .getSingleResult();

        assertEquals(0, count, "OAuthProvider should be removed from the database");
    }


    @Test
    public void When_AddingDuplicateOAuthProvider_Expect_ThrowException() {
        User user = userRepository.save(validUser);
        user.addOAuthProvider(new OAuthProvider(OAuthProviderType.GOOGLE, "123google"));
        User savedUser = userRepository.save(user);
        assertThrows(DuplicateOAuthProviderException.class, ()->savedUser.addOAuthProvider(new OAuthProvider(OAuthProviderType.GOOGLE, "456google")), "Should throw exception on duplicate OAuthProvider" );
    }



    @Test
    public void When_AddingAuthToken_Expect_TokenAddedSuccessfully() {
        User user = userRepository.save(validUser);


        // Create and add the token
        AuthToken authToken = createAuthToken("token123");
        user.addAuthToken(authToken);
        userRepository.save(user);

        // Reload the user to verify persistence
        User savedUser = userRepository.findById(user.getId()).orElseThrow();

        // Assertions
        assertEquals(1, savedUser.getAuthTokens().size(), "User should have 1 token after addition");
        assertEquals(authToken.getToken(), savedUser.getAuthTokens().getFirst().getToken(), "Token should match saved token");
        assertEquals("device123", savedUser.getAuthTokens().getFirst().getDeviceId(), "Device ID should match");
    }

    @Test
    public void When_AddingAuthTokenForSameDevice_Expect_TokenReplacedSuccessfully() {
        User user = userRepository.save(validUser);


        // Add the first token
        AuthToken authTokenOne = createAuthToken("token123");
        user.addAuthToken(authTokenOne);
        userRepository.save(user);

        // Add a second token for the same device
        AuthToken authTokenTwo = createAuthToken("token456");
        user.addAuthToken(authTokenTwo);
        userRepository.save(user);

        // Reload the user to verify persistence
        User savedUser = userRepository.findById(user.getId()).orElseThrow();

        // Assertions
        assertEquals(1, savedUser.getAuthTokens().size(), "User should only have 1 token after replacement");
        assertTrue(savedUser.getAuthTokens().stream()
                .filter(token -> token.getToken().equals(authTokenOne.getToken())).findFirst().isEmpty(), "User should not contain the first token");

        assertEquals(authTokenTwo.getToken(), savedUser.getAuthTokens().getFirst().getToken(), "Token should match the new token ID");
    }

    @Test
    public void When_RemovingAuthTokenFromUser_Expect_TokenRemovedFromDBSuccessfully() {
        User user = userRepository.save(validUser);
        AuthToken authToken = createAuthToken("token123");
        user.addAuthToken(authToken);
        userRepository.saveAndFlush(user);
        AuthToken savedAuthToken = user.getAuthTokens().getFirst();

        assertTrue(authTokenRepository.findById(savedAuthToken.getId()).isPresent(), "Added token should be found");

        user.getAuthTokens().clear();
        userRepository.saveAndFlush(user);

        assertFalse(authTokenRepository.findById(savedAuthToken.getId()).isPresent(), "Removed token should not be found");
    }


    //TODO: TEST OTHER RELATIONSHIPS
}
