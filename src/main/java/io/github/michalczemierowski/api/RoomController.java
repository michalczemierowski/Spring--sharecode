package io.github.michalczemierowski.api;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.RoomMessage;
import io.github.michalczemierowski.service.DatabaseService;
import io.github.michalczemierowski.service.SsePushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequestMapping("api/v1/room")
@RestController
public class RoomController {
    @Autowired
    private DatabaseService dbService;

    /**
     * TODO: remove later
     */
    @GetMapping("get")
    public List<Room> getRooms() {
        return dbService.getAllRooms();
    }

    @GetMapping(path = "getmsg/{id}")
    public Set<RoomMessage> getRoomMsg(@AuthenticationPrincipal OAuth2User principal, @PathVariable("id") UUID id) {
        Optional<Room> optionalRoom = dbService.findRoomByID(id);

        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            String authUserID = principal.getAttribute("email");

            // if user has access to view room
            if (room.canUserViewRoom(authUserID))
                return room.getMessages();
            else
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Read access forbidden");
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
    }

    /**
     * Get room if exists and user has access to it
     *
     * @param principal auth principal
     * @param id        room id
     * @return room if exists and user has access to it, else throw 404
     */
    @GetMapping(path = "get/{id}")
    public Room getRoom(@AuthenticationPrincipal OAuth2User principal, @PathVariable("id") UUID id) {
        Optional<Room> optionalRoom = dbService.findRoomByID(id);

        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            String authUserID = principal.getAttribute("email");

            // if user has access to view room
            if (room.canUserViewRoom(authUserID))
                return room;
            else
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access forbidden");
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
    }

    @PostMapping(path = "update/{id}/content")
    public HttpStatus updateRoomContent(@AuthenticationPrincipal OAuth2User principal, @PathVariable("id") UUID id, @RequestParam(name = "content") String content) {
        Optional<Room> optionalRoom = dbService.findRoomByID(id);

        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            String authUserID = principal.getAttribute("email");

            // if user has access to view room
            if (room.canUserViewRoom(authUserID)) {
                room.setContent(content);
                dbService.saveRoom(room);

                return HttpStatus.OK;
            } else
                return HttpStatus.FORBIDDEN;
        }

        return HttpStatus.NOT_FOUND;
    }

    /**
     * Get list of rooms where authenticated user is owner
     *
     * @param principal auth principal
     * @return list of owned rooms
     */
    @RequestMapping("/get-owned")
    public List<Room> getOwnedRooms(@AuthenticationPrincipal OAuth2User principal) {
        String authUserID = principal.getAttribute("email");

        return dbService.findRoomsByOwnerUser(authUserID);
    }

    /**
     * Get list of rooms where authenticated user is in room access list (not containing owned rooms)
     *
     * @param principal auth principal
     * @return list of available rooms (not containing owned rooms)
     */
    @RequestMapping("/get-available")
    public List<Room> getAvailableRooms(@AuthenticationPrincipal OAuth2User principal) {
        String authUserID = principal.getAttribute("email");

        return dbService.findByUsersWithAccess_Id(authUserID);
    }
}
