package io.github.tryferos.spring_server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class StudentController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    private int counter2 = 0;

    @GetMapping("/student")
    public Student student(@RequestParam(value = "name", defaultValue = "World") String name){
        return new Student(counter2++, String.format(template, name), "Mazar");
    }

    @RequestMapping(path="/test")
    public String test(){
        return "index.html";
    }
}
