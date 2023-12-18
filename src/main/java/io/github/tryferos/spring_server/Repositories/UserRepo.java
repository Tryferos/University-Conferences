package io.github.tryferos.spring_server.Repositories;

import io.github.tryferos.spring_server.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByFullnameIsIgnoreCase(String name);

}
