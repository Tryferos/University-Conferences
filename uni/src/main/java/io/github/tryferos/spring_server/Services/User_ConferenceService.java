package io.github.tryferos.spring_server.Services;

import io.github.tryferos.spring_server.Conference.Conference;
import io.github.tryferos.spring_server.Conference.ConferenceException;
import io.github.tryferos.spring_server.ErrorMessages;
import io.github.tryferos.spring_server.Repositories.UserRepo;
import io.github.tryferos.spring_server.Repositories.User_ConferenceRepo;
import io.github.tryferos.spring_server.User.User;
import io.github.tryferos.spring_server.User.User_Conference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class User_ConferenceService {

    @Autowired
    private User_ConferenceRepo repo;

    @Autowired
    private UserRepo userRepo;

    public User_Conference[] getPCChairs(long conf_id){
        return repo.findUser_ConferencesByPcChairTrueAndConferenceId(conf_id);
    }
    public User_Conference[] getPCMembers(long conf_id){
        return repo.findUser_ConferencesByPcMemberTrueAndConferenceId(conf_id);
    }

    public User_Conference[] getConferenceMembers(long conf_id){
        return repo.findUser_ConferencesByConferenceId(conf_id);
    }

    public User_Conference getById(long conf_id, long user_id){
        return repo.findUser_ConferenceByConferenceIdAndUserId(conf_id, user_id);
    }

    public void saveUser_Conf(User_Conference user){
        repo.save(user);
    }

    public void saveUsers(User_Conference[] users){
        ArrayList<User_Conference> list = new ArrayList<>(List.of(users));
        repo.saveAll(list);
    }

    public void deleteUser(User_Conference user){
        repo.delete(user);
    }

    @Transactional
    public void addPcChairs(Conference conf, long[] chairs_id, boolean onlyAdd) throws ConferenceException {
        if(chairs_id==null) return;
        if(!onlyAdd) repo.removeUser_ConferencesByConferenceIdAndPcChairTrue(conf.getId());
        for (int i = 0; i < chairs_id.length; i++) {
            long id = chairs_id[i];
            User_Conference user = repo.findUser_ConferenceByConferenceIdAndUserId(conf.getId(), id);
            checkRoles(id, user, conf.getCreator().getId());
            User_Conference tmp = new User_Conference();
            tmp.setUser(userRepo.getReferenceById(id));
            tmp.setConference(conf);
            tmp.setPcChair(true);
            repo.save(tmp);
        }
    }

    private void checkRoles(long id, User_Conference user, long creator_id) throws ConferenceException{
        if(user!=null && user.isPcChair()) throw new ConferenceException(String.format(ErrorMessages.UserHasRole, id, "pc chair"));
        if(user!=null && user.isPcMember()) throw new ConferenceException(String.format(ErrorMessages.UserHasRole, id, "pc member"));
        if(creator_id==id) throw new ConferenceException(String.format(ErrorMessages.UserCreator, id));
    }
    @Transactional
    public void addPcMembers(Conference conf, long[] members_id, boolean onlyAdd) throws ConferenceException {
        if(members_id==null) return;
        if(!onlyAdd)repo.removeUser_ConferencesByConferenceIdAndPcMemberTrue(conf.getId());
        for (int i = 0; i < members_id.length; i++) {
            long id = members_id[i];
            User_Conference user = repo.findUser_ConferenceByConferenceIdAndUserId(conf.getId(), id);
            checkRoles(id, user, conf.getCreator().getId());
            User_Conference tmp = new User_Conference();
            User vUser = userRepo.getReferenceById(id);
            if(vUser==null) throw new ConferenceException(String.format(ErrorMessages.UserNotExist,id));
            tmp.setUser(vUser);
            tmp.setConference(conf);
            tmp.setPcMember(true);
            repo.save(tmp);
        }
    }
    @Transactional
    public void updateUsers(Conference conf, long[] member_ids, long[] chair_ids) throws ConferenceException {
        addPcMembers(conf, member_ids, false);
        addPcChairs(conf, chair_ids, false);
    }
}
