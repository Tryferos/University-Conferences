package io.github.tryferos.spring_server.Repositories;

import io.github.tryferos.spring_server.User.Author;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuthorRepo extends JpaRepository<Author, Long> {
    Author findAuthorByPaperIdAndUserId(long paper_id, long user_id);

}
