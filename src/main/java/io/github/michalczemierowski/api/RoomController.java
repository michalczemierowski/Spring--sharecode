package io.github.michalczemierowski.api;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.RoomMessage;
import io.github.michalczemierowski.service.RoomService;
import io.github.michalczemierowski.service.SsePushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.HtmlUtils;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("api/v1/room")
@RestController
@Validated
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

        return roomMessages.isPresent() && roomMessages.get().size() > 0
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
                                           @RequestParam(name = "name") @Size(min = 5, max = 100) String name,
                                           @RequestParam(name = "language", required = false) @Size(min = 1, max = 32) String language) {
        String authUserID = principal.getAttribute("email");

        name = HtmlUtils.htmlEscape(name);
        Optional<Room> optionalRoom = roomService.createRoom(authUserID, name, language);

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
                                          @RequestParam(name = "target_user_id") @NotNull String targetUserId) {
        String authUserId = principal.getAttribute("email");

        return roomService.addRoomAccessForUser(id, authUserId, targetUserId)
                ? HttpStatus.OK
                : HttpStatus.NOT_FOUND;
    }

    @PostMapping(path = "/update/{id}/remove-access")
    public HttpStatus removeRoomAccessFromUser(@AuthenticationPrincipal OAuth2User principal,
                                               @PathVariable("id") UUID id,
                                               @RequestParam(name = "target_user_id") @NotNull String targetUserId) {
        String authUserId = principal.getAttribute("email");
        boolean accessWasRemoved = roomService.removeRoomAccessFromUser(id, authUserId, targetUserId);

        if (accessWasRemoved) {
            // send SSE to clients
            ssePushNotificationService.sendRoomRemoveAccessNotification(id, targetUserId);
            return HttpStatus.OK;
        }

        return HttpStatus.NOT_FOUND;
    }

    @PostMapping(path = "/update/{id}/set-content")
    public HttpStatus updateRoomContent(@AuthenticationPrincipal OAuth2User principal,
                                        @PathVariable("id") UUID id,
                                        @RequestParam(name = "content") String content) {
        String authUserID = principal.getAttribute("email");
        Optional<Room> optionalRoom = roomService.updateRoomContent(id, authUserID, content);

        if (optionalRoom.isPresent()) {
            // send SSE to clients
            ssePushNotificationService.sendRoomContentUpdateNotification(optionalRoom.get());

            return HttpStatus.OK;
        }

        return HttpStatus.BAD_REQUEST;
    }

    @PostMapping(path = "/update/{id}/set-language")
    public ResponseEntity<String> updateRoomLanguage(@AuthenticationPrincipal OAuth2User principal,
                                        @PathVariable("id") UUID id,
                                        @RequestParam(name = "language") @Size(min = 1, max = 32) String language) {
        String authUserID = principal.getAttribute("email");
        Optional<Room> optionalRoom = roomService.updateRoomLanguage(id, authUserID, language);

        return optionalRoom.isPresent()
                ? ResponseEntity.ok(optionalRoom.get().getLanguage())
                : ResponseEntity.badRequest().build();
    }

    @PostMapping(path = "/update/{id}/set-name")
    public ResponseEntity<String> updateRoomName(@AuthenticationPrincipal OAuth2User principal,
                                         @PathVariable("id") UUID id,
                                         @RequestParam(name = "name") @Size(min = 5, max = 100) String name) {
        String authUserID = principal.getAttribute("email");

        name = HtmlUtils.htmlEscape(name);
        Optional<Room> optionalRoom =  roomService.updateRoomName(id, authUserID, name);

        return optionalRoom.isPresent()
                ? ResponseEntity.ok(optionalRoom.get().getName())
                : ResponseEntity.badRequest().build();
    }

    @PostMapping(path = "/update/{id}/add-message")
    public ResponseEntity<RoomMessage> addRoomMessage(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable("id") @NotNull UUID id,
            @RequestParam(name = "content") @Size(min = 1, max = 1024) String content) {
        String authUserID = principal.getAttribute("email");

        content = HtmlUtils.htmlEscape(content);
        Optional<RoomMessage> optionalRoomMessage = roomService.addRoomMessage(id, authUserID, content);

        if (optionalRoomMessage.isPresent()) {
            RoomMessage roomMessage = optionalRoomMessage.get();
            ssePushNotificationService.sendRoomMessagesUpdateNotification(roomMessage);

            return ResponseEntity.ok(roomMessage);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping(path = "/update/{id}/delete-message")
    public HttpStatus deleteRoomMessage(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable("id") @NotNull UUID id,
            @RequestParam(name = "msg_id") @NotNull int messageId) {
        String authUserID = principal.getAttribute("email");
        boolean messageWasDeleted = roomService.deleteRoomMessage(id, authUserID, messageId);

        if (messageWasDeleted) {
            ssePushNotificationService.sendRoomMessagesDeleteNotification(id, messageId);
            return HttpStatus.OK;
        }

        return HttpStatus.NOT_FOUND;
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<String> handleValidationExceptions(Exception exception, WebRequest request) {
        return new ResponseEntity<String>(exception.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
