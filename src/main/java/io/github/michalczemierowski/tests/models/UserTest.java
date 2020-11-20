package io.github.michalczemierowski.tests.models;

import io.github.michalczemierowski.model.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {

    @Test
    public void changeUserName()
    {
        String initialName = "testUser";
        String newName = "newName";
        User testUser1 = new User("user1@test.com", initialName);

        // check initial name
        assertEquals(testUser1.getName(), initialName);

        // update name
        testUser1.setName(newName);

        // check updated name
        assertEquals(testUser1.getName(), newName);
    }

    @Test
    public void changeUserEmail()
    {
        String initialEmail = "user1@test.com";
        String newEmail = "newuser1@test.com";
        User testUser1 = new User("user1@test.com", "testUser");

        // check initial email
        assertEquals(testUser1.getId(), initialEmail);

        // update name
        testUser1.setId(newEmail);

        // check updated email
        assertEquals(testUser1.getId(), newEmail);
    }
}
