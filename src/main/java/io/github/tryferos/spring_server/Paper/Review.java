package io.github.tryferos.spring_server.Paper;

import io.github.tryferos.spring_server.User.User;
import io.github.tryferos.spring_server.User.User_Conference;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="review")
public class Review {

    @Id
    private long id;
    @Column
    private String comment;
    @Column
    private int score;
    @OneToOne
    @JoinColumn(name="reviewer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User_Conference reviewer;

    @ManyToOne
    @Nullable
    @JoinColumn(name="paper_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Paper paper;

}
