package io.github.michalczemierowski.tests.services;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.User;
import io.github.michalczemierowski.service.RoomService;
import io.github.michalczemierowski.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.text.html.Option;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserServiceIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @SpyBean
    private UserService userService;

    @Test
    public void getUserById() {
        User testUser = new User("test@user.com", "test");
        entityManager.persist(testUser);

        Optional<User> optionalUser = userService.getUserById("test@user.com");

        // check if room found
        assertTrue(optionalUser.isPresent());
        // check if room is the same
        assertEquals(testUser, optionalUser.get());
    }

    @Test
    public void getNameById() {
        User testUser = new User("test@user.com", "test");
        entityManager.persist(testUser);

        Optional<String> optionalName = userService.getNameById(testUser.getId());

        assertTrue(optionalName.isPresent());
        // assert that names are the same
        assertEquals(testUser.getName(), optionalName.get());
    }
}
