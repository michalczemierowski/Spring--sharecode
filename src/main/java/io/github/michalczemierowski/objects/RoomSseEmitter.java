package io.github.michalczemierowski.objects;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

public class RoomSseEmitter extends SseEmitter {
    private UUID targetRoomId;

    public UUID getTargetRoomId() {
        return targetRoomId;
    }

    public void setTargetRoomId(UUID targetRoomId) {
        this.targetRoomId = targetRoomId;
    }
}
