package io.github.tryferos.spring_server.Services;

import io.github.tryferos.spring_server.Repositories.UserRepo;
import io.github.tryferos.spring_server.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;
    public User getUser(long id){
        return repo.getReferenceById(id);
    }

    public User saveUser(User user){
        return repo.save(user);
    }

    public User getUserByFullName(String name){return repo.findByFullnameIsIgnoreCase(name);}

}
