package io.github.michalczemierowski.api;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("api/v1/room")
@RestController
public class RoomController {
    private final DatabaseService dbService;

    @Autowired
    public RoomController(DatabaseService dbService) {
        this.dbService = dbService;
    }

    @GetMapping("get")
    public List<Room> getRooms()
    {
        return dbService.getAllRooms();
    }

    @GetMapping(path = "get/{id}")
    public Room getRoom(@PathVariable("id") UUID id)
    {
        Optional<Room> room = dbService.findRoomByID(id);

        if (room.isPresent())
            return room.get();
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
    }

    @RequestMapping("/get-owned")
    public List<Room> getOwnedRooms(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");

        return dbService.findRoomsByOwnerUser(email);
    }

    @RequestMapping("/get-avaliable")
    public List<Room> getAvaliableRooms(@AuthenticationPrincipal OAuth2User principal)
    {
        String email = principal.getAttribute("email");

        return dbService.findByRoomAccess_Id(email);
    }
}
