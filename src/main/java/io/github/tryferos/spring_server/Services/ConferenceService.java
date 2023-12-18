package io.github.tryferos.spring_server.Services;

import io.github.tryferos.spring_server.Conference.*;
import io.github.tryferos.spring_server.ErrorMessages;
import io.github.tryferos.spring_server.Repositories.ConferenceRepo;
import io.github.tryferos.spring_server.User.User_Conference;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ConferenceService {

    @Autowired
    private ConferenceRepo repo;

    @Autowired
    private UserService userService;

    @Autowired
    private User_ConferenceService user_conf;

    public Conference getConference(long id){
        if(!repo.existsById(id)) return null;
        return repo.getReferenceById(id);
    }



    public void saveConference(ConferenceCreationRecord data) throws ConferenceException {

        Conference conf = new Conference();
        conf.setName(data.name());
        conf.setDescription(data.description());
        conf.setCreator(userService.getUser(data.creator_id()));
        try{
            repo.save(conf);
//            user_conf.saveCreator(conf);
        }catch(DataIntegrityViolationException e){
            System.out.println(e.getMessage());
            if(e.getMessage().contains("not-null")){
                throw new ConferenceException(String.format(ErrorMessages.NoNullConstraint, "name"));
            }
            if(e.getMessage().contains("a foreign key constraint fails")){
                throw new ConferenceException(String.format(ErrorMessages.ForeignKeyConstraint, "creator_id"));
            }
            throw new ConferenceException(String.format(ErrorMessages.UniqueConstraint, "Conference", "name"));
        }
    }


    private boolean existsInNew(long[] ids, long id){
        if(ids==null) return false;
        for (int i = 0; i < ids.length; i++) {
            if(ids[i]==id) return true;
        }
        return false;
    }

    private void editExistingUsers(ConferenceUpdateRecord data){
        //! CONSTRAINT: PC CHAIR CANNOT BE AUTHOR OR PC MEMBER IN THE SAME CONFERENCE
        long creator_id = repo.getReferenceById(data.id()).getCreator().getId();

        User_Conference[] users = user_conf.getConferenceMembers(data.id());
        if(data.pc_chairs_id()==null && data.pc_members_id()==null) return;
        for (int i = 0; i < users.length; i++) {
            User_Conference user =  users[i];
            if(user.getUser().getId()==creator_id) continue;
            if(existsInNew(data.pc_chairs_id(), user.getId()) || user.isPcChair()){
                user.setPcChair(true);
                continue;
            }
            user.setPcChair(false);
            boolean isPcMember = existsInNew(data.pc_members_id(), user.getId());
            user.setPcMember(isPcMember);
            if(user.hasNoRole()){
                user_conf.deleteUser(user);
            }
        }
        user_conf.saveUsers(users);
    }

    public void updateConference(ConferenceUpdateRecord data) throws ConferenceException{
        Conference conf = repo.getReferenceById(data.id());
        conf.setName(data.name()==null ? conf.getName() : data.name());
        conf.setDescription(data.description()==null ? conf.getDescription() : data.description());

        user_conf.updateUsers(conf, data.pc_members_id(), data.pc_chairs_id());

        repo.save(conf);

//        editExistingUsers(data);
    }

    public List<ConferenceData> getConferencesByQuery(String name, String description, long user_id){
        List<Conference> conferences = repo.getConferencesByNameIgnoreCaseOrDescriptionIgnoreCaseOrderByName(
                name, description
        );

        List<ConferenceData> data = new ArrayList<>();

        conferences.forEach(c -> {
            User_Conference user = user_conf.getById(c.getId(), user_id);
            ConferenceData d = new ConferenceData();
            d.setConference(c);
            //CHECK IF THE USER HAS PERMISSION TO VIEW OTHER MEMBERS
            System.out.println(user);
            if(user!=null && (user.isPcChair() || c.getCreator().getId()==user_id)){
                User_Conference[] users = user_conf.getConferenceMembers(c.getId());
                d.setUsers(Arrays.stream(users).map(arg -> {arg.setConference(null);return arg;}).toArray(User_Conference[]::new));
                //TODO: FETCH PAPERS
            }else{
                c.setCreator(null);
                c.setId(0);
            }
            data.add(d);
        });

        return data;
    }

    @Transactional
    public void deleteConference(long id, long userId) throws ConferenceException{
        Conference conf = repo.getReferenceById(id);
        if(conf==null) throw new ConferenceException(ErrorMessages.ConferenceNotExist);
        long[] users = Arrays.stream(user_conf.getPCChairs(id)).map(item -> item.getUser().getId()).mapToLong(i->i).toArray();
        if(conf!=null && !(conf.getCreator().getId()==userId || existsInNew(users, userId))) throw new ConferenceException(ErrorMessages.ConferenceDeleteNoAccess);
        boolean deleted = repo.deleteConferenceByIdIsAndStateIs(id, ConferenceState.CREATED) > 0;
        if(!deleted) throw new ConferenceException(ErrorMessages.DeleteOperationWrongState);
    }

    public ConferenceState forwardConference(long id) throws ConferenceException{
        Conference conf = repo.getReferenceById(id);
        if(conf==null) throw new ConferenceException(ErrorMessages.ConferenceNotExist);
        conf.forwardState();
        repo.save(conf);
        return conf.getState();
    }
}
