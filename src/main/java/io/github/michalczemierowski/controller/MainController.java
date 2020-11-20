package io.github.michalczemierowski.controller;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Controller
public class MainController {
    @Autowired
    private RoomService roomService;

    @RequestMapping("/sign-in")
    public String login() {
        return "sign-in";
    }

    @RequestMapping("/room/{id}")
    public String viewRoom(Model model, @AuthenticationPrincipal OAuth2User principal, @PathVariable("id") UUID id) {
        String authUserID = principal.getAttribute("email");
        Optional<Room> optionalRoom = roomService.getRoom(id, authUserID);

        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();

            boolean isOwnerUser = room.getOwnerUser().getId().equals(authUserID);
            model.addAttribute("id", id);
            model.addAttribute("isOwner", isOwnerUser);

            return "room";

        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
