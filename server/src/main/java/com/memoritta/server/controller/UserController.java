package com.memoritta.server.controller;

import com.memoritta.server.manager.UserAccessManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class UserController {
    private UserAccessManager userAccessManager;

    @GetMapping("/user")
    public String getUser() {
        return "user";
    }
}
