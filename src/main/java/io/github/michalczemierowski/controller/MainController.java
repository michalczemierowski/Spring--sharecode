package io.github.michalczemierowski.controller;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.User;
import io.github.michalczemierowski.service.DatabaseService;
import io.github.michalczemierowski.service.SsePushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@Controller
public class MainController {
    private final DatabaseService databaseService;

    @Autowired
    public MainController(DatabaseService databaseService, SsePushNotificationService ssePushNotificationService) {
        this.databaseService = databaseService;
    }

    @RequestMapping("/sign-in")
    public String login() {
        return "sign-in";
    }

    @RequestMapping("/room/{id}")
    public String viewRoom(Model model, @PathVariable("id") UUID id) {
        model.addAttribute("id", id);
        return "room";
    }

    @RequestMapping("/getactiveusername")
    @ResponseBody
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes();
        //return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @RequestMapping("/getactiveuseremail")
    @ResponseBody
    public Map<String, Object> userEmail(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("email", principal.getAttribute("email"));
    }

    @RequestMapping("/getactiveuserrooms")
    @ResponseBody
    public List<Room> userRooms(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        return databaseService.findRoomsByOwnerUser(email);
    }

    @RequestMapping("/createroom")
    @ResponseBody
    public String createuserRooms(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        Optional<User> optionalUser = databaseService.findUserByID(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            System.out.println(email + "   " + user.getAvailableRooms().size());

            Room room = new Room(UUID.randomUUID(), "testroom", user);
            databaseService.saveRoom(room);
            //databaseService.saveUser(user);

            return "ok";
        }
        return "nie ok";
    }
}
