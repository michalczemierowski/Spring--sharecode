package io.github.michalczemierowski;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.User;
import io.github.michalczemierowski.repository.RoomRepository;
import io.github.michalczemierowski.repository.UserRepository;
import io.github.michalczemierowski.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MainController {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;

    private final DatabaseService databaseService;

    @Autowired
    public MainController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping("/tes")
    public String tes(@RequestParam(defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "test";
    }

    @RequestMapping("/")
    public String mainPage(@AuthenticationPrincipal OAuth2User principal) {
        //if(databaseService.)
        return "index";
    }

    @RequestMapping("/sign-in")
    public String login() {
        return "sign-in";
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
            System.out.println(email + "   " + user.getRooms().size());

            Room room = new Room(UUID.randomUUID(), "testroom", user);
            databaseService.saveRoom(room);
            //databaseService.saveUser(user);

            return "ok";
        }
        return "nie ok";
    }
}
