package io.github.michalczemierowski.api;

import io.github.michalczemierowski.objects.RoomSseEmitter;
import io.github.michalczemierowski.service.DatabaseService;
import io.github.michalczemierowski.service.SsePushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RequestMapping("notifications")
@RestController
@CrossOrigin(origins = "*")
public class NotificationsController {
    private final SsePushNotificationService ssePushNotificationService;

    @Autowired
    public NotificationsController(DatabaseService dbService, SsePushNotificationService ssePushNotificationService)
    {
        this.ssePushNotificationService = ssePushNotificationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomSseEmitter> registerNotifications(@PathVariable("id") UUID id) throws InterruptedException, IOException {
        // create emitter and assign room id
        final RoomSseEmitter emitter = new RoomSseEmitter();
        emitter.setTargetRoomId(id);

        // add emitter to list
        ssePushNotificationService.addEmitter(emitter);

        emitter.onCompletion(() -> ssePushNotificationService.removeEmitter(emitter));
        emitter.onTimeout(() -> ssePushNotificationService.removeEmitter(emitter));

        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }
}
