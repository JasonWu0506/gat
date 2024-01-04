package com.pingcap.gat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageRouteController {
    @GetMapping("/index")
    public String indexPage() {
        return "index"; // Return the index.html Thymeleaf template
    }
}
