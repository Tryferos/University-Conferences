package io.github.tryferos.spring_server.Paper;

import io.github.tryferos.spring_server.Services.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
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

    @PostMapping(path = "/forward")
    public ResponseEntity<HttpStatus> paperForward(
            @RequestBody(required = true) PaperForwardStateRecord data
    ){
        try{
            service.forwardPaper(data, 3);
        }catch(PaperException e){
            return new ResponseEntity(e.getMessage(), HttpStatus.OK);
        }catch (PaperStateException e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping(path = "/forward/reject")
    public ResponseEntity<HttpStatus> paperForwardReject(
            @RequestBody(required = true) PaperForwardStateRecord data
    ){
        try{
            service.rejectPaper(data.paper_id(), 3);
        }catch(PaperException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping(path = "/forward/approve")
    public ResponseEntity<HttpStatus> paperForwardAccept(
            @RequestBody(required = true) PaperForwardStateRecord data
    ){
        try{
            service.approvePaper(data.paper_id(), 3);
        }catch(PaperException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping(path = "/forward/final-submit")
    public ResponseEntity<HttpStatus> paperForwardFinalSubmit(
            @RequestBody(required = true) ReviewRecord data
    ){
        try{
            service.finalSubmissionPaper(data.paper_id(), 3, data.newContent(), data.author_comment());
        }catch(PaperException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping(path = "/forward/accept")
    public ResponseEntity<HttpStatus> paperForwardFinal(
            @RequestBody(required = true) ReviewRecord data
    ){
        try{
            service.acceptPaper(data.paper_id(), 3);
        }catch(PaperException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<HttpStatus> paperSearch(
            @RequestParam(required = true) String title,
            @RequestParam(required = true) String vAbstract,
            @RequestParam(required = true) long[] authors
    ){
        return new ResponseEntity(service.searchPapers(title, vAbstract, authors), HttpStatus.OK);

    }

    @PostMapping(path = "/review")
    public ResponseEntity<HttpStatus> paperReview(
            @RequestBody(required = true) ReviewRecord data
    ){
        try{
            service.reviewPaper(data, 3);
        }catch(PaperException e){
            return new ResponseEntity(e.getMessage(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }



}
