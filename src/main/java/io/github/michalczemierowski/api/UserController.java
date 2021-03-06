package io.github.michalczemierowski.api;

import io.github.michalczemierowski.model.User;
import io.github.michalczemierowski.service.UserService;
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
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.Optional;

@RequestMapping("api/v1/user")
@RestController
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/get-auth-data")
    @ResponseBody
    public Map<String, Object> getAuthData(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes();
    }

    @GetMapping("/get-auth-name")
    public ResponseEntity<String> getAuthName(@AuthenticationPrincipal OAuth2User principal) {
        String authName = principal.getAttribute("name");

        return (authName != null && !authName.isEmpty())
                ? ResponseEntity.ok(authName)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/get-email")
    public ResponseEntity<String> getEmail(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttribute("email");
    }

    @GetMapping("/get-data")
    @ResponseBody
    public ResponseEntity<User> getData(@AuthenticationPrincipal OAuth2User principal) {
        String authUserID = principal.getAttribute("email");
        Optional<User> optionalUser = userService.getUserById(authUserID);

        return optionalUser.isPresent()
                ? ResponseEntity.ok(optionalUser.get())
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/get-name")
    public ResponseEntity<String> getName(@AuthenticationPrincipal OAuth2User principal) {
        String authUserID = principal.getAttribute("email");
        Optional<String> optionalName = userService.getNameById(authUserID);

        return optionalName.isPresent()
                ? ResponseEntity.ok(optionalName.get())
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/set-name")
    public ResponseEntity<String> setName(@AuthenticationPrincipal OAuth2User principal,
                                          @RequestParam(name = "name") @Size(min = 5, max = 32) String name) {
        String authUserID = principal.getAttribute("email");

        name = HtmlUtils.htmlEscape(name);
        Optional<String> optionalName = userService.setUserName(authUserID, name);

        return optionalName.isPresent()
                ? ResponseEntity.ok(optionalName.get())
                : ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<String> handleValidationExceptions(Exception exception, WebRequest request) {
        return new ResponseEntity<String>(exception.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
