package io.github.michalczemierowski.service;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.objects.RoomSseEmitter;
import net.minidev.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@EnableScheduling
public class SsePushNotificationService {
    private final List<RoomSseEmitter> roomSseEmitters = new CopyOnWriteArrayList<>();

    public void addRoomSSEEmitter(final RoomSseEmitter emitter) {
        roomSseEmitters.add(emitter);
    }

    public void removeRoomSSEEmitter(final RoomSseEmitter emitter) {
        roomSseEmitters.remove(emitter);
    }

    @Async
    public void sendRoomUpdateNotification(Room room) {
        List<RoomSseEmitter> deadEmitters = new ArrayList<>();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("time", System.currentTimeMillis());
        jsonObject.put("content", room.getContent());

        roomSseEmitters.stream()
                // send update only to users in same room
                .filter(x -> x.getTargetRoomId().equals(room.getId()))
                .forEach(emitter -> {
                    try {
                        emitter.send(SseEmitter.event().data(jsonObject.toJSONString()));
                    } catch (Exception e) {
                        // remove emitter if there is problem with it
                        deadEmitters.add(emitter);
                    }
                });

        roomSseEmitters.removeAll(deadEmitters);
    }
}
