package io.github.michalczemierowski.tests.models;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.User;
import org.apache.tomcat.jni.Local;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.*;

public class RoomTest {
    private final UUID testUUID = UUID.fromString("d674ad24-85c4-4301-88ce-c2565f4b6851");

    @Test
    public void createRoom() {
        User testOwner = new User("test@owner.com", "test");
        User testUser = new User("test@user.com", "test");

        Room room = new Room(testUUID, "testRoom", null, testOwner, testUser);

        // assert that user room access list contains room
        assertFalse(testOwner.getAvailableRooms().contains(room));
        assertTrue(testUser.getAvailableRooms().contains(room));

        // assert that owner is correct
        assertEquals(room.getOwnerUser(), testOwner);

        // assert that room access list contains users (owner shouldn't be in roomAccess set)
        assertTrue(room.getUsersWithAccess().contains(testUser));
        assertFalse(room.getUsersWithAccess().contains(testOwner));
    }

    @Test
    public void addAccessToUser() {
        User testOwner = new User("test@owner.com", "test");
        User testUser = new User("test@user.com", "test");
        Room room = new Room(testUUID, "testRoom", null, testOwner);

        // adding access to owner shouldn't be possible
        assertFalse(room.addUser(testOwner));

        // adding new user to room
        assertTrue(room.addUser(testUser));
        assertTrue(room.getUsersWithAccess().contains(testUser));
        assertTrue(testUser.getAvailableRooms().contains(room));
    }

    @Test
    public void changeRoomOwner() {
        User testOwner = new User("test@owner.com", "test");
        User testUser = new User("test@user.com", "test");
        Room room = new Room(testUUID, "testRoom", null, testOwner, testUser);

        // room access shouldn't contain owner
        assertFalse(room.getUsersWithAccess().contains(testOwner));
        assertTrue(room.getUsersWithAccess().contains(testUser));

        // update owner
        room.setOwnerUser(testUser);

        // check if owner is updated
        assertEquals(room.getOwnerUser(), testUser);
        // room access shouldn't contain owner
        assertFalse(room.getUsersWithAccess().contains(testUser));
        assertTrue(room.getUsersWithAccess().contains(testOwner));
    }

    @Test
    public void setName() {
        User testOwner = new User("test@owner.com", "test");

        String defaultName = "testRoom";
        String newName = "newName";
        Room room = new Room(testUUID, defaultName, null, testOwner);

        // check if initial name is correct
        assertEquals(room.getName(), defaultName);

        // update name
        room.setName(newName);

        // check if new name is correct
        assertEquals(room.getName(), newName);
    }

    @Test
    public void canUserViewRoom() {
        User testOwner = new User("test@owner.com", "test");
        User testUser = new User("test@user.com", "test");
        Room room = new Room(testUUID, "testRoom", null, testOwner);

        // assert that owner can view room
        assertTrue(room.canBeViewedBy(testOwner.getId()));
        // testUser shouldn't be able to view room yet
        assertFalse(room.canBeViewedBy(testUser.getId()));

        // add access to testUser
        room.addUser(testUser);

        // now testUser should be able to view room
        assertTrue(room.canBeViewedBy(testUser.getId()));
    }
}
