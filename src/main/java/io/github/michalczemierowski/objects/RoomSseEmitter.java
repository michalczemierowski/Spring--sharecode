package io.github.michalczemierowski.objects;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class RoomSseEmitter extends SseEmitter {
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
