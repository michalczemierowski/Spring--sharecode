package io.github.michalczemierowski.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.RoomMessage;
import io.github.michalczemierowski.objects.RoomSseEmitter;
import net.minidev.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@EnableScheduling
public class SsePushNotificationService {
    private final HashMap<UUID, List<RoomSseEmitter>> roomContentSseEmitters = new HashMap<>();

    private void sendToAllUsersInRoom(UUID roomId, JSONObject json) {
        List<RoomSseEmitter> deadEmitters = new ArrayList<>();

        for (RoomSseEmitter emitter : roomContentSseEmitters.get(roomId)) {
            try {
                emitter.send(SseEmitter.event()
                        .data(json.toJSONString()));
            } catch (Exception e) {
                // remove emitter if there is problem with it
                deadEmitters.add(emitter);
            }
        }

        roomContentSseEmitters.get(roomId).removeAll(deadEmitters);
    }

    public void addRoomContentSSEEmitter(UUID roomId, final RoomSseEmitter emitter) {
        if (!roomContentSseEmitters.containsKey(roomId))
            roomContentSseEmitters.put(roomId, new ArrayList<>());

        roomContentSseEmitters.get(roomId).add(emitter);
    }

    public void removeRoomContentSSEEmitter(UUID roomId, final RoomSseEmitter emitter) {
        if (roomContentSseEmitters.containsKey(roomId))
            roomContentSseEmitters.get(roomId).remove(emitter);
    }

    @Async
    public void sendRoomContentUpdateNotification(Room room) {
        if (!roomContentSseEmitters.containsKey(room.getId()))
            return;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "content_update");
        jsonObject.put("content", room.getContent());

        sendToAllUsersInRoom(room.getId(), jsonObject);
    }

    @Async
    public void sendRoomMessagesUpdateNotification(RoomMessage newMessage) {
        UUID roomId = newMessage.getRoom().getId();
        if (!roomContentSseEmitters.containsKey(roomId))
            return;

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", "add_message");
        jsonObject.put("id", newMessage.getId());

        JSONObject user = new JSONObject();
        user.put("id", newMessage.getUser().getId());
        user.put("name", newMessage.getUser().getName());

        jsonObject.put("user", user);
        jsonObject.put("content", newMessage.getContent());
        jsonObject.put("send_datetime", newMessage.getSendDateTime().toString());

        sendToAllUsersInRoom(roomId, jsonObject);
    }

    @Async
    public void sendRoomMessagesDeleteNotification(UUID roomId, int messageId) {
        if (!roomContentSseEmitters.containsKey(roomId))
            return;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "delete_message");
        jsonObject.put("id", messageId);

        sendToAllUsersInRoom(roomId, jsonObject);
    }

    @Async
    public void sendRoomRemoveAccessNotification(UUID roomId, String userId) {
        if (!roomContentSseEmitters.containsKey(roomId))
            return;

        Optional<RoomSseEmitter> optionalRoomSseEmitter = roomContentSseEmitters.get(roomId).stream()
                .filter(x -> x.getUserId().equals(userId)).findFirst();

        if(optionalRoomSseEmitter.isPresent()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "remove_access");
            jsonObject.put("id", userId);

            RoomSseEmitter roomSseEmitter = optionalRoomSseEmitter.get();
            try {
                roomSseEmitter.send(SseEmitter.event()
                        .data(jsonObject.toJSONString()));
            } catch (IOException ignored) { }

            roomContentSseEmitters.get(roomId).remove(roomSseEmitter);
        }
    }
}
