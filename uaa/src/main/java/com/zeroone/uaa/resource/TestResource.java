package com.zeroone.uaa.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestResource {




    @GetMapping("/say")
    public String say(){
        return "hello";
    }
}
