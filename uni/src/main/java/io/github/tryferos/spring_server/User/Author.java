package io.github.tryferos.spring_server.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.tryferos.spring_server.Paper.Paper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="author")
@IdClass(AuthorId.class)
public class Author {

    @ManyToOne
    @Id
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne
    @Id
    @JoinColumn(name = "paper_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Paper paper;

    @Column(nullable = false)
    private boolean isCreator;
}
