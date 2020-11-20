package io.github.michalczemierowski.repository;

import io.github.michalczemierowski.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    List<Room> findByOwnerUserIdOrderByDateOfLastUseDesc(String id);
    List<Room> findByOwnerUserId(String id);
    List<Room> findByUsersWithAccess_Id(String id);
    boolean existsRoomByName(String name);
}
