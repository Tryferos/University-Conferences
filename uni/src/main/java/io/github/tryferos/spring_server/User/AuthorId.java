package io.github.tryferos.spring_server.User;

import io.github.tryferos.spring_server.Paper.Paper;
import lombok.Data;

import java.io.Serializable;

@Data
public class AuthorId implements Serializable {
    private User user;
    private Paper paper;
}
