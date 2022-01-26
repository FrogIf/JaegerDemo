package sch.frog.jaeger.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JaegerSimpleController {

    @RequestMapping("/hello")
    public String hello(){
        return "hello";
    }

}
