package io.github.michalczemierowski.tests.models;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.User;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class RoomTest {
    private final UUID testUUID = UUID.fromString("d674ad24-85c4-4301-88ce-c2565f4b6851");

    @Test
    public void createRoom() {
        User testUser1 = new User("user1@test.com", "testUser1");
        User testUser2 = new User("user2@test.com", "testUser2");

        Room room = new Room(testUUID, "testRoom", testUser1, testUser2);

        // check if user roomAccess contains room
        assertFalse(testUser1.getAvailableRooms().contains(room));
        assertTrue(testUser2.getAvailableRooms().contains(room));

        // check if owner is correct
        assertEquals(room.getOwnerUser(), testUser1);

        // check if room access contains users (owner shouldn't be in roomAccess set)
        assertTrue(room.getUsersWithAccess().contains(testUser2));
        assertFalse(room.getUsersWithAccess().contains(testUser1));
    }

    @Test
    public void addAccessToUser()
    {
        User testUser1 = new User("user1@test.com", "testUser1");
        User testUser2 = new User("user2@test.com", "testUser2");
        Room room = new Room(testUUID, "testRoom", testUser1);

        // adding owner to room
        assertFalse(room.addUser(testUser1));

        // adding new user to room
        assertTrue(room.addUser(testUser2));
        assertTrue(room.getUsersWithAccess().contains(testUser2));
        assertTrue(testUser2.getAvailableRooms().contains(room));
    }

    @Test
    public void changeRoomOwner()
    {
        User testUser1 = new User("user1@test.com", "testUser1");
        User testUser2 = new User("user2@test.com", "testUser2");
        Room room = new Room(testUUID, "testRoom", testUser1, testUser2);

        // room access shouldn't contain owner
        assertFalse(room.getUsersWithAccess().contains(testUser1));
        assertTrue(room.getUsersWithAccess().contains(testUser2));

        // update owner
        room.setOwnerUser(testUser2);

        // check if owner is updated
        assertEquals(room.getOwnerUser(), testUser2);
        // room access shouldn't contain owner
        assertFalse(room.getUsersWithAccess().contains(testUser2));
        assertTrue(room.getUsersWithAccess().contains(testUser1));
    }

    @Test
    public void updateRoomName() {
        User testUser1 = new User("user1@test.com", "testUser1");

        String defaultName = "testRoom";
        String newName = "newName";
        Room room = new Room(testUUID, defaultName, testUser1);

        // check if initial name is correct
        assertEquals(room.getName(), defaultName);

        // update name
        room.setName(newName);

        // check if new name is correct
        assertEquals(room.getName(), newName);
    }

}
