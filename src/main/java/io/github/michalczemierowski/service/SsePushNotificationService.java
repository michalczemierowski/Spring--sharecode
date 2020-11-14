package io.github.michalczemierowski.service;

import io.github.michalczemierowski.objects.RoomSseEmitter;
import io.github.michalczemierowski.model.Room;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@EnableScheduling
public class SsePushNotificationService {
    private final List<RoomSseEmitter> emitters = new CopyOnWriteArrayList<>();

    public void addEmitter(final RoomSseEmitter emitter) {
        emitters.add(emitter);
    }

    public void removeEmitter(final RoomSseEmitter emitter) {
        emitters.remove(emitter);
    }

    @Async
    public void sendRoomUpdateNotification(Room room) throws IOException {
        List<RoomSseEmitter> deadEmitters = new ArrayList<>();
        emitters.stream()
                // send update only to users in same room
                .filter(x -> x.getTargetRoomId().equals(room.getId()))
                .forEach(emitter -> {
                    try {
                        emitter.send(SseEmitter.event().data(room));
                    } catch (Exception e) {
                        // remove emitter if there is problem with it
                        deadEmitters.add(emitter);
                    }
                });

        emitters.removeAll(deadEmitters);
    }
}
