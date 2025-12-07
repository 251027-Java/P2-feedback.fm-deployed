package com.revature.ExpenseReport.Controller;

// Spring uses REFLECTION to recognize our ANNOTATIONS

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api") // domain:port/api
class HelloController {

    @GetMapping("/hello") // domain:port/api/hello
    String hello (@RequestParam(value = "name", defaultValue = "World" ) String name) {
        // query parameter: key value pair that is added to the url
        // DO NOT send a password by query param!
        // domain:port/api/hello?name=Richard
        return "Hello %s!".formatted(name);
    }
}