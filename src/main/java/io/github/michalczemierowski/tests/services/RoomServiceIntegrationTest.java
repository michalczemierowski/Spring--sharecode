package io.github.michalczemierowski.tests.services;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.User;
import io.github.michalczemierowski.service.RoomService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoomServiceIntegrationTest {
    private final UUID testUUID = UUID.fromString("d674ad24-85c4-4301-88ce-c2565f4b6851");

    @Autowired
    private TestEntityManager entityManager;

    @SpyBean
    private RoomService roomService;

    @Test
    public void createRoom()
    {
        User testOwner = new User("test@owner.com", "test");
        entityManager.persist(testOwner);

        Optional<Room> optionalRoom = roomService.createRoom(testOwner.getId(), "testRoom-create");

        // check if room was created
        assertTrue(optionalRoom.isPresent());
        // check if owner is correct
        assertEquals(testOwner.getId(), optionalRoom.get().getOwnerUser().getId());;
    }

    @Test
    public void getOwnedRooms()
    {
        User testOwner = new User("test-get-owned@owner.com", "test");
        User testUser = new User("test-get-owned@user.com", "test");

        entityManager.persist(testOwner);
        entityManager.persist(testUser);

        Optional<Room> optionalRoom = roomService.createRoom(testOwner.getId(), "testRoom-get-available");

        // check if room was created
        assertTrue(optionalRoom.isPresent());

        Room createdRoom = optionalRoom.get();

        // TODO
    }

    @Test
    public void addRoomAccess()
    {
        User testOwner = new User("test-get-available@owner.com", "test");
        User testUser = new User("test-get-available@user.com", "test");

        entityManager.persist(testOwner);
        entityManager.persist(testUser);

        Optional<Room> optionalRoom = roomService.createRoom(testOwner.getId(), "testRoom-get-available");

        // check if room was created
        assertTrue(optionalRoom.isPresent());

        Room createdRoom = optionalRoom.get();

        // add access to testUser
        boolean accessAdded = roomService.addRoomAccessForUser(createdRoom.getId(), testOwner.getId(), testUser.getId());
        assertTrue(accessAdded);

        assertFalse(createdRoom.getUsersWithAccess().contains(testOwner));
        assertTrue(createdRoom.getUsersWithAccess().contains(testUser));
    }

    @Test
    public void getRoomById()
    {
        User testOwner = new User("test-get@owner.com", "test");
        entityManager.persist(testOwner);

        Room testRoom = new Room(testUUID, "testRoom-get", testOwner);
        entityManager.persist(testRoom);

        Optional<Room> foundRoom = roomService.getRoom(testUUID, testOwner.getId());

        // check if room found
        assertTrue(foundRoom.isPresent());
        // check if room is the same
        assertEquals(testRoom, foundRoom.get());
    }
}
