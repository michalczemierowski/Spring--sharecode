package io.github.michalczemierowski.components.scheduled;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RemoveUnusedRoomsTask {
    @Autowired
    private RoomRepository roomRepository;

    // second, minute, hour, day of month, month, day(s) of week
    // this method runs every midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void removeUnusedRooms()
    {
        int daysToDeleteRoom = 14;

        LocalDateTime minDate = LocalDateTime.now().minusDays(daysToDeleteRoom);

        List<Room> allRooms = roomRepository.findAll();
        allRooms.stream()
                .filter(room -> room.getDateOfLastUse().isBefore(minDate))
                .forEach(room -> {
                    roomRepository.deleteById(room.getId());
                });
    }
}
