package mywild.wildguide.user.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import mywild.wildguide.framework.security.jwt.JwtUtils;
import mywild.wildguide.framework.web.BaseController;
import mywild.wildguide.user.logic.UserService;

// TODO: logout (keep track of active tokens and revoke ones that are logged out)

@Tag(name = "User Authentication", description = "Manage Users.")
@RestController
public class UserController extends BaseController {

    @Autowired
    private UserService service;

    @Operation(summary = "Register (create) a new User.")
    @PostMapping("/users/register")
    public @Valid Tokens register(@RequestBody User user) {
        return service.register(user);
    }

    @Operation(summary = "Login as an existing User in order to get a pair of access and refresh tokens.")
    @PostMapping("/users/login")
    public @Valid Tokens login(@RequestBody UserLogin login) {
        return service.login(login);
    }

    @Operation(summary = "Request a new pair of access and refresh tokens.")
    @PostMapping("/users/refresh")
    public @Valid Tokens refresh(JwtAuthenticationToken jwtToken) {
        return service.refresh(JwtUtils.getUserIdFromJwt(jwtToken));
    }

    @Operation(summary = "Find User information (in particular the ID) based on the provided Username.")
    @GetMapping("/users")
    public @Valid UserInfo findUserInfo(@RequestParam @Valid @NotBlank String username) {
        return service.findUserInfo(username);
    }

}
