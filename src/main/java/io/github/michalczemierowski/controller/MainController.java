package io.github.michalczemierowski.controller;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.service.RoomService;
import io.github.michalczemierowski.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Controller
public class MainController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @GetMapping("/sign-in")
    public String login() {
        return "sign-in";
    }

    @GetMapping("/dashboard")
    public String viewDashboard(Model model, @AuthenticationPrincipal OAuth2User principal) {
        String authUserID = principal.getAttribute("email");

        if (authUserID == null || authUserID.isEmpty())
            return "redirect:/sign-in";

        Optional<String> optionalUserName = userService.getNameById(authUserID);
        if (optionalUserName.isPresent()) {
            model.addAttribute("user_id", authUserID);
            model.addAttribute("user_name", optionalUserName.get());

            return "dashboard";
        }

        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/room/{id}")
    public String viewRoom(Model model, @AuthenticationPrincipal OAuth2User principal, @PathVariable("id") UUID id) {
        String authUserID = principal.getAttribute("email");
        Optional<Room> optionalRoom = roomService.getRoom(id, authUserID);

        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();

            model.addAttribute("room_id", id);
            model.addAttribute("room_name", room.getName());
            model.addAttribute("user_id", authUserID);
            model.addAttribute("is_owner", room.isOwnedBy(authUserID));

            return "room";

        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
