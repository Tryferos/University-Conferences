package io.github.tryferos.spring_server.Conference;

import io.github.tryferos.spring_server.ErrorMessages;
import io.github.tryferos.spring_server.Services.ConferenceService;
import io.github.tryferos.spring_server.Services.UserService;
import io.github.tryferos.spring_server.Services.User_ConferenceService;
import io.github.tryferos.spring_server.SuccessMessages;
import io.github.tryferos.spring_server.User.User;
import io.github.tryferos.spring_server.User.User_Conference;
import org.apache.catalina.util.ToStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/conference")
public class ConferenceController {

    @Autowired
    UserService userServ;

    @Autowired
    ConferenceService confService;

    @Autowired
    User_ConferenceService user_confService;

    @PostMapping(path = "/create")
    public ResponseEntity<HttpStatus> conferenceCreation(
            @RequestBody(required = true)ConferenceCreationRecord conf
    ) {
        System.out.println(conf);
        try{
            confService.saveConference(conf);
            return new ResponseEntity(HttpStatus.OK);
        }catch(ConferenceException e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/update")
    public ResponseEntity<HttpStatus> conferenceUpdate(
            @RequestBody(required = false)ConferenceUpdateRecord conf
    ){
        //EDIT: name-description-pc members- pc chairs
        //MUST: conf_id: id
        //EXCEPT: creator
        System.out.println(conf);
        try{
            confService.updateConference(conf);
            return ResponseEntity.ok(HttpStatus.OK);
        }catch(ConferenceException e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/addition/pc_chairs")
    public ResponseEntity<HttpStatus> conferencePcChairsAddition(
            @RequestBody(required = true)ConferenceUpdateRecord conf
    ){
        try{
            user_confService.addPcChairs(confService.getConference(conf.id()), conf.pc_chairs_id(), true);
            return new ResponseEntity(HttpStatus.OK);
        }catch (ConferenceException e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping(path = "/addition/pc_members")
    public ResponseEntity<HttpStatus> conferencePcMembersAddition(
            @RequestBody(required = true)ConferenceUpdateRecord conf
    ){
        try{
            user_confService.addPcMembers(confService.getConference(conf.id()), conf.pc_members_id(), true);
            return new ResponseEntity(HttpStatus.OK);
        }catch (ConferenceException e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/create/user")
    public ResponseEntity<HttpStatus> createUser(
            @RequestBody User user
            ){
        System.out.println(user);
        userServ.saveUser(user);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<List<ConferenceData>> getConferences(
            @RequestParam String name,
            @RequestParam(required = false) String description
    ){
        System.out.println(name);
        System.out.println(description);
        return new ResponseEntity<>(confService.getConferencesByQuery(name, description, 3),HttpStatus.ACCEPTED);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<HttpStatus> getConferences(
            @RequestBody ConferenceUpdateRecord conf
    ){
        long id = conf.id();
        try{
            confService.deleteConference(id, 3);
            return new ResponseEntity(String.format(SuccessMessages.ConferenceDeleteOperationSuccess, id),HttpStatus.ACCEPTED);
        }catch(ConferenceException e){
            return new ResponseEntity(String.format(e.getMessage(), id),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/state/forward")
    public ResponseEntity<HttpStatus> forwardConferenceState(
            @RequestBody ConferenceUpdateRecord conf
    ){
        long id = conf.id();
        try{
            ConferenceState newState = confService.forwardConference(id);
            System.out.printf(String.valueOf(newState));
            return new ResponseEntity(String.format(SuccessMessages.ConferenceForwardOperationSuccess, id, newState),HttpStatus.ACCEPTED);
        }catch(ConferenceException e){
            return new ResponseEntity(String.format(e.getMessage(), id),HttpStatus.BAD_REQUEST);
        }
    }
}

