package io.github.michalczemierowski.repository;

import io.github.michalczemierowski.model.RoomMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomMessagesRepository extends JpaRepository<RoomMessage, Integer> {
    List<RoomMessage> findByRoomId(UUID id);
    List<RoomMessage> findByUserId(String id);
}