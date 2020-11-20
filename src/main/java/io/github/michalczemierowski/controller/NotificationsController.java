package io.github.michalczemierowski.controller;

import io.github.michalczemierowski.objects.RoomSseEmitter;
import io.github.michalczemierowski.service.SsePushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RequestMapping("notifications")
@RestController
@CrossOrigin(origins = "*")
public class NotificationsController {
    @Autowired
    private SsePushNotificationService ssePushNotificationService;

    @GetMapping("/{id}")
    public ResponseEntity<RoomSseEmitter> registerNotifications(@PathVariable("id") UUID id) throws InterruptedException, IOException {
        // create emitter and assign room id
        final RoomSseEmitter emitter = new RoomSseEmitter();
        emitter.setTargetRoomId(id);

        // add emitter to list
        ssePushNotificationService.addRoomSSEEmitter(emitter);

        emitter.onCompletion(() -> ssePushNotificationService.removeRoomSSEEmitter(emitter));
        emitter.onTimeout(() -> ssePushNotificationService.removeRoomSSEEmitter(emitter));

        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }
}
