package io.github.tryferos.spring_server.User;

import jakarta.persistence.*;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String fullname;
    @Column
    private String username;

    public User(String fullname, String username) {
        this.fullname = fullname;
        this.username = username;
    }
}


