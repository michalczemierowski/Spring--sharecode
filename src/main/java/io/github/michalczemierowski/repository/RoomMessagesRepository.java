package io.github.michalczemierowski.repository;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.RoomMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomMessagesRepository extends JpaRepository<RoomMessage, Integer> {
    List<Room> findByRoomId(UUID id);
    List<Room> findByUserId(String id);
}