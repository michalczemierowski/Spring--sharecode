package io.github.michalczemierowski.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RequestMapping("api/v1/user")
@RestController
public class UserController {

    @RequestMapping("/get-name")
    @ResponseBody
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes();
        //return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @RequestMapping("/get-email")
    @ResponseBody
    public Map<String, Object> userEmail(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("email", principal.getAttribute("email"));
    }
}
