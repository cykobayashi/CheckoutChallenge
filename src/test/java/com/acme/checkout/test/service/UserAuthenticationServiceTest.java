package com.acme.checkout.test.service;

import com.acme.checkout.domain.model.User;
import com.acme.checkout.domain.repositories.UserRepository;
import com.acme.checkout.service.UserAuthenticationService;
import com.acme.checkout.test.config.BusinessLayerTestSupport;
import com.acme.checkout.test.config.BusinessTestConfiguration;
import com.acme.checkout.test.config.MongoTestConfig;
import com.mongodb.Mongo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static com.acme.checkout.test.config.MongoTestConfig.DATABASE_TEST_NAME;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        MongoTestConfig.class,
        BusinessTestConfiguration.class
})
public class UserAuthenticationServiceTest extends BusinessLayerTestSupport {

	@Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private UserAuthenticationService userAuthenticationService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private Mongo mongo;

	private static final String USER_EMAIL = "user@email.com";
	private static final String USER_GUID = "2edef4f9-7f58-4ab0-bbd3-a4297b9dd14c";
    private static final String NONEXISTING_USER_EMAIL = "new@email.com";

	@Before
	public void setUp() {
		userRepository.save(User.builder().email(USER_EMAIL).guid(USER_GUID).build());
	}

	@After
	public void down() {
		mongo.dropDatabase(DATABASE_TEST_NAME);
	}

	@Test
	public void findByToken_shouldReturnExistingUser() {
        Optional<User> user =  userAuthenticationService.findByToken(USER_EMAIL);
        assertEquals(USER_EMAIL, user.get().getEmail());
        assertEquals(USER_GUID, user.get().getGuid());
	}

    @Test
    public void findByToken_shouldCreateNewUser() {
        Optional<User> user =  userAuthenticationService.findByToken(NONEXISTING_USER_EMAIL);
        assertEquals(NONEXISTING_USER_EMAIL, user.get().getEmail());
        assertNotEquals(USER_GUID, user.get().getGuid());
    }

    @Test
    public void findByToken_shouldRefuseInvalidEmails() {
        Optional<User> user =  userAuthenticationService.findByToken("invalid emal");
        assertFalse(user.isPresent());
    }

}
