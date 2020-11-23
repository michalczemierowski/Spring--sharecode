package io.github.michalczemierowski.service;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.RoomMessage;
import io.github.michalczemierowski.model.User;
import io.github.michalczemierowski.repository.RoomMessagesRepository;
import io.github.michalczemierowski.repository.RoomRepository;
import io.github.michalczemierowski.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomMessagesRepository roomMessagesRepository;

    public Optional<Room> getRoom(UUID roomId, String userId) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();

            // if user has access to view room
            if (room.canUserViewRoom(userId)) {
                // update date of last use
                room.dateOfLastUse = LocalDateTime.now();
                roomRepository.save(room);
                return Optional.of(room);
            }
        }

        return Optional.empty();
    }

    /**
     * Find list of rooms where owner id is equal to provided user id
     *
     * @param userId user id
     * @return list of rooms where owner id equals provided user id
     */
    public List<Room> getOwnedRooms(String userId) {
        return roomRepository.findByOwnerUserIdOrderByDateOfLastUseDesc(userId);
    }

    /**
     * Find list of rooms where provided user is in room access list (not containing owned rooms)
     *
     * @param userId user id
     * @return list of rooms where provided user is in room access list (not containing owned rooms)
     */
    public List<Room> getAvailableRoom(String userId) {
        return roomRepository.findByUsersWithAccess_Id(userId);
    }

    public Optional<Room> createRoom(String userId, String roomName) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Room newRoom = new Room(UUID.randomUUID(), roomName, optionalUser.get());

            Room savedRoom = roomRepository.save(newRoom);
            user.getOwnedRooms().add(savedRoom);
            userRepository.save(user);

            return Optional.of(savedRoom);
        }

        return Optional.empty();
    }

    public boolean deleteRoom(UUID roomId, String userId) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        if (optionalRoom.isPresent() && optionalRoom.get().getOwnerUser().getId().equals(userId)) {
            for (RoomMessage roomMessage : roomMessagesRepository.findByRoomId(roomId)) {
                roomMessagesRepository.delete(roomMessage);
            }

            roomRepository.deleteById(roomId);
            return true;
        }

        return false;
    }

    public Optional<Room> updateRoomContent(UUID roomId, String userId, String content) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();

            // if user has access to view room
            if (room.canUserViewRoom(userId)) {
                room.setContent(content);

                roomRepository.save(room);
                return Optional.of(room);
            }
        }

        return Optional.empty();
    }

    public boolean addRoomAccessForUser(UUID roomId, String userId, String targetUserId) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();

            // if user is owner and targetUserId is not same as userId
            if (room.getOwnerUser().getId().equals(userId) && !userId.equals(targetUserId)) {
                Optional<User> optionalUser = userRepository.findById(targetUserId);
                // if user exists and don't have access
                if (optionalUser.isPresent() && !room.getUsersWithAccess().contains(optionalUser.get())) {
                    // add access and save room
                    room.getUsersWithAccess().add(optionalUser.get());
                    roomRepository.save(room);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean removeRoomAccessFromUser(UUID roomId, String userId, String targetUserId) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();

            if (room.getOwnerUser().getId().equals(userId)) {
                Optional<User> optionalUser = userRepository.findById(targetUserId);
                // if user exists and have access
                if (optionalUser.isPresent() && room.getUsersWithAccess().contains(optionalUser.get())) {
                    // add access and save room
                    room.getUsersWithAccess().remove(optionalUser.get());
                    roomRepository.save(room);
                    return true;
                }
            }
        }

        return false;
    }

    public Optional<List<RoomMessage>> getRoomMessages(UUID roomId, String userId) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();

            // if user has access to view room
            if (room.canUserViewRoom(userId) && room.getMessages() != null) {
                return Optional.of(room.getMessages());
            }
        }

        return Optional.empty();
    }

    public Optional<RoomMessage> addRoomMessage(UUID roomId, String userId, String content) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();

            // if user has access to view room
            if (room.canUserViewRoom(userId)) {
                Optional<User> optionalUser = userRepository.findById(userId);
                if (optionalUser.isPresent()) {
                    RoomMessage newMessage = new RoomMessage(optionalUser.get(), room, content);
                    roomMessagesRepository.save(newMessage);
                    room.addMessage(newMessage);
                    return Optional.of(newMessage);
                }
            }
        }
        return Optional.empty();
    }

    public boolean deleteRoomMessage(UUID roomId, String userId, int messageId) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();

            if (room.canUserViewRoom(userId))
            {
                Optional<RoomMessage> optionalRoomMessage = roomMessagesRepository.findById(messageId);

                if (optionalRoomMessage.isPresent()) {
                    roomMessagesRepository.delete(optionalRoomMessage.get());
                    room.removeMessage(optionalRoomMessage.get());
                    return true;
                }
            }
        }

        return false;
    }
}
