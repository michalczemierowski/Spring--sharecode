package io.github.michalczemierowski.api;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.RoomMessage;
import io.github.michalczemierowski.service.RoomService;
import io.github.michalczemierowski.service.SsePushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("api/v1/room")
@RestController
public class RoomController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private SsePushNotificationService ssePushNotificationService;

    @GetMapping(path = "/get/{id}/messages")
    public ResponseEntity<List<RoomMessage>> getRoomMsg(@AuthenticationPrincipal OAuth2User principal,
                                                        @PathVariable("id") @NotNull UUID id) {
        String authUserID = principal.getAttribute("email");
        Optional<List<RoomMessage>> roomMessages = roomService.getRoomMessages(id, authUserID);

        return roomMessages.isPresent()
                ? ResponseEntity.ok(roomMessages.get())
                : ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/get/{id}")
    public ResponseEntity<Room> getRoom(@AuthenticationPrincipal OAuth2User principal,
                                        @PathVariable("id") UUID id) {
        String authUserID = principal.getAttribute("email");
        Optional<Room> optionalRoom = roomService.getRoom(id, authUserID);

        return ResponseEntity.of(optionalRoom);
    }

    @GetMapping(path = "/get-owned")
    public List<Room> getOwnedRooms(@AuthenticationPrincipal OAuth2User principal) {
        String authUserID = principal.getAttribute("email");

        return roomService.getOwnedRooms(authUserID);
    }

    @GetMapping(path = "/get-available")
    public List<Room> getAvailableRooms(@AuthenticationPrincipal OAuth2User principal) {
        String authUserID = principal.getAttribute("email");

        return roomService.getAvailableRoom(authUserID);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<Room> createRoom(@AuthenticationPrincipal OAuth2User principal,
                                           @RequestParam(name = "room_name", defaultValue = "new room") String roomName) {
        String authUserID = principal.getAttribute("email");
        Optional<Room> optionalRoom = roomService.createRoom(authUserID, roomName);

        return ResponseEntity.of(optionalRoom);
    }

    @DeleteMapping(path = "/delete/{id}")
    public HttpStatus deleteRoom(@AuthenticationPrincipal OAuth2User principal,
                                 @PathVariable("id") UUID id) {
        String authUserID = principal.getAttribute("email");

        return roomService.deleteRoom(id, authUserID)
                ? HttpStatus.OK
                : HttpStatus.NOT_FOUND;
    }

    @PostMapping(path = "/update/{id}/add-access")
    public HttpStatus addRoomAccesForUser(@AuthenticationPrincipal OAuth2User principal,
                                          @PathVariable("id") UUID id,
                                          @RequestParam(name = "target_user_id") String targetUserId) {
        String authUserId = principal.getAttribute("email");

        return roomService.addRoomAccessForUser(id, authUserId, targetUserId)
                ? HttpStatus.OK
                : HttpStatus.NOT_FOUND;
    }

    @PostMapping(path = "/update/{id}/remove-access")
    public HttpStatus removeRoomAccessFromUser(@AuthenticationPrincipal OAuth2User principal,
                                               @PathVariable("id") UUID id,
                                               @RequestParam(name = "target_user_id") String targetUserId) {
        String authUserId = principal.getAttribute("email");

        return roomService.removeRoomAccessFromUser(id, authUserId, targetUserId)
                ? HttpStatus.OK
                : HttpStatus.NOT_FOUND;
    }

    @PostMapping(path = "/update/{id}/set-content")
    public HttpStatus updateRoomContent(@AuthenticationPrincipal OAuth2User principal,
                                        @PathVariable("id") UUID id,
                                        @RequestParam(name = "content") String content) {
        String authUserID = principal.getAttribute("email");
        Optional<Room> optionalRoom = roomService.updateRoomContent(id, authUserID, content);

        if (optionalRoom.isPresent()) {
            // send SSE to clients
            ssePushNotificationService.sendRoomUpdateNotification(optionalRoom.get());

            return HttpStatus.OK;
        }

        return HttpStatus.NOT_FOUND;
    }

    @PostMapping(path = "/update/{id}/add-message")
    public ResponseEntity<RoomMessage> addRoomMessage(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable("id") @NotNull UUID id,
            @RequestParam(name = "content") @Size(min = 1, max = 1024) String content) {
        String authUserID = principal.getAttribute("email");
        Optional<RoomMessage> roomMessage = roomService.addRoomMessage(id, authUserID, content);

        return roomMessage.isPresent()
                ? ResponseEntity.ok(roomMessage.get())
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping(path = "/update/{id}/delete-message")
    public HttpStatus deleteRoomMessage(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable("id") @NotNull UUID id,
            @RequestParam(name = "msg_id") @NotNull int messageId) {
        String authUserID = principal.getAttribute("email");

        return roomService.deleteRoomMessage(id, authUserID, messageId)
                ? HttpStatus.OK
                : HttpStatus.NOT_FOUND;
    }
}
