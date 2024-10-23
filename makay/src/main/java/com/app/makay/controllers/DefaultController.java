package com.app.makay.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class DefaultController {
    @GetMapping("/")
    public RedirectView index(){
        return new RedirectView("/login");
    }
}
