package io.github.tryferos.spring_server.Paper;

import io.github.tryferos.spring_server.Services.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paper")
public class PaperController {

    @Autowired
    private PaperService service;

    @PutMapping(path = "/edit")
    public ResponseEntity<HttpStatus> paperEdit(
            @RequestBody PaperUpdateRecord body
    ){
        try{
            Paper paper = service.editPaper(body, 3);
            return new ResponseEntity(paper,HttpStatus.OK);
        }catch(PaperException e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/create")
    public ResponseEntity<HttpStatus> paperCreation(
            @RequestBody(required = true)PaperCreationRecord data
    ){
        try{
            service.savePaper(data, 3);
        }catch(PaperException e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }



}
