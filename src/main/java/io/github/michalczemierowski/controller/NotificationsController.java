package io.github.michalczemierowski.controller;

import io.github.michalczemierowski.objects.RoomSseEmitter;
import io.github.michalczemierowski.service.SsePushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("notifications")
@RestController
@CrossOrigin(origins = "*")
public class NotificationsController {
    @Autowired
    private SsePushNotificationService ssePushNotificationService;

    @GetMapping("/room/{id}/{email}")
    public ResponseEntity<RoomSseEmitter> registerContentNotifications(@PathVariable("id") UUID id,
                                                                       @PathVariable("email") String authUserId) {
        // create emitter and assign room id
        final RoomSseEmitter emitter = new RoomSseEmitter();
        emitter.setUserId(authUserId);

        // add emitter to list
        ssePushNotificationService.addRoomContentSSEEmitter(id, emitter);

        emitter.onCompletion(() -> ssePushNotificationService.removeRoomContentSSEEmitter(id, emitter));
        emitter.onTimeout(() -> ssePushNotificationService.removeRoomContentSSEEmitter(id, emitter));

        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }
}
