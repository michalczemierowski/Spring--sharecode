package io.github.michalczemierowski.tests.services;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.RoomMessage;
import io.github.michalczemierowski.model.User;
import io.github.michalczemierowski.service.RoomService;
import io.github.michalczemierowski.util.ValidationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoomServiceIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @SpyBean
    private RoomService roomService;

    @Test
    public void getRoomById()
    {
        UUID testUUID = UUID.fromString("d674ad24-85c4-4301-88ce-c2565f4b6851");

        User testOwner = new User("test@owner.com", "test");
        entityManager.persist(testOwner);

        Room testRoom = new Room(testUUID, "testRoom", null, testOwner);
        // change date to to check if it will update correctly when using getRoom method
        testRoom.setDateOfLastUse(LocalDateTime.now().minusYears(1));
        entityManager.persist(testRoom);

        Optional<Room> optionalRoom = roomService.getRoom(testUUID, "test@owner.com");

        // check if room found
        assertTrue(optionalRoom.isPresent());
        // check if room is the same
        assertEquals(testRoom, optionalRoom.get());
        // check if date of last use was correctly updated
        Duration diff = Duration.between(LocalDateTime.now(), optionalRoom.get().getDateOfLastUse());
        assertTrue(diff.toMillis() < 10);
    }

    @Test
    public void createRoom()
    {
        User testOwner = new User("test@owner.com", "test");
        entityManager.persist(testOwner);

        Optional<Room> optionalRoom = roomService.createRoom(testOwner.getId(), "testRoom", null);

        // assert that room was created
        assertTrue(optionalRoom.isPresent());
        // assert that owner is correct
        assertEquals(testOwner.getId(), optionalRoom.get().getOwnerUser().getId());
        assertTrue(testOwner.getOwnedRooms().contains(optionalRoom.get()));
    }

    @Test
    public void updateRoomName()
    {
        String defaultName = "testRoom";
        String newName = "updateRoomName";

        User testOwner = new User("test@owner.com", "test");
        entityManager.persist(testOwner);

        Optional<Room> optionalRoom = roomService.createRoom(testOwner.getId(), defaultName, null);

        // assert that room was created
        assertTrue(optionalRoom.isPresent());
        Room createdRoom = optionalRoom.get();

        // assert that name is the same before change
        assertEquals(defaultName, createdRoom.getName());

        // testOwner should be able to change name
        Optional<Room> renamedRoom = roomService.updateRoomName(createdRoom.getId(), testOwner.getId(), newName);
        assertTrue(renamedRoom.isPresent());
        assertEquals(newName, renamedRoom.get().getName());
    }

    @Test
    public void updateRoomLanguage()
    {
        String validLanguage = "java";
        String invalidLanguage = "invalid_language";

        User testOwner = new User("test@owner.com", "test");
        entityManager.persist(testOwner);

        Optional<Room> optionalRoom = roomService.createRoom(testOwner.getId(), "testRoom", null);

        // assert that room was created
        assertTrue(optionalRoom.isPresent());
        Room createdRoom = optionalRoom.get();

        // testOwner should be able to change name
        Optional<Room> renamedRoom = roomService.updateRoomLanguage(createdRoom.getId(), testOwner.getId(), validLanguage);
        assertTrue(renamedRoom.isPresent());
        assertEquals(validLanguage, renamedRoom.get().getLanguage());

        renamedRoom = roomService.updateRoomLanguage(createdRoom.getId(), testOwner.getId(), invalidLanguage);
        assertTrue(renamedRoom.isPresent());
        // invalid language should be replaced with ValidationUtils.DEFAULT_LANGUAGE
        assertEquals(ValidationUtils.DEFAULT_LANGUAGE, renamedRoom.get().getLanguage());
    }

    @Test
    public void addRemoveRoomAccess()
    {
        User testOwner = new User("test@owner.com", "test");
        User testUser = new User("test@user.com", "test");

        entityManager.persist(testOwner);
        entityManager.persist(testUser);

        Optional<Room> optionalRoom = roomService.createRoom(testOwner.getId(), "testRoom", null);

        // assert that room was created successfully
        assertTrue(optionalRoom.isPresent());
        Room createdRoom = optionalRoom.get();

        // add access to testUser
        boolean accessWasAdded = roomService.addRoomAccessForUser(createdRoom.getId(), testOwner.getId(), testUser.getId());

        assertTrue(accessWasAdded);
        assertFalse(createdRoom.getUsersWithAccess().contains(testOwner));
        assertTrue(createdRoom.getUsersWithAccess().contains(testUser));

        assertTrue(testOwner.getOwnedRooms().contains(createdRoom));
        assertTrue(testUser.getAvailableRooms().contains(createdRoom));

        boolean accessWasRemoved = roomService.removeRoomAccessFromUser(createdRoom.getId(), testOwner.getId(), testUser.getId());

        assertTrue(accessWasRemoved);
        assertFalse(createdRoom.getUsersWithAccess().contains(testUser));

        assertFalse(testUser.getAvailableRooms().contains(createdRoom));
    }

    @Test
    public void roomMessages()
    {
        User testOwner = new User("test@owner.com", "test");
        entityManager.persist(testOwner);

        Optional<Room> optionalRoom = roomService.createRoom(testOwner.getId(), "testRoom", null);

        // assert that room was created successfully
        assertTrue(optionalRoom.isPresent());
        Room room = optionalRoom.get();

        Optional<RoomMessage> createdRoomMessage = roomService.addRoomMessage(room.getId(), testOwner.getId(), "test");

        // check if message was created, and content is the same
        assertTrue(createdRoomMessage.isPresent());
        assertEquals(createdRoomMessage.get().getContent(), "test");

        // try to find message by id
        Optional<List<RoomMessage>> roomMessages = roomService.getRoomMessages(room.getId(), testOwner.getId());

        // check if list contains created room
        assertTrue(roomMessages.isPresent());
        assertTrue(roomMessages.get().stream().anyMatch(msg -> msg.getId() == createdRoomMessage.get().getId()));

        // check if message was deleted successfully
        boolean messageWasDeleted = roomService.deleteRoomMessage(room.getId(), testOwner.getId(), createdRoomMessage.get().getId());
        assertTrue(messageWasDeleted);

        // get all messages from room
        roomMessages = roomService.getRoomMessages(room.getId(), testOwner.getId());

        // now list shouldn't contain created room
        assertTrue(roomMessages.isPresent());
        assertFalse(roomMessages.get().stream().anyMatch(msg -> msg.getId() == createdRoomMessage.get().getId()));
    }

}
