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
    @Column(nullable = true)
    private String comment;
    @Column(nullable = true)
    private int score;
    @Column(nullable = true)
    private String author_comment;

    @OneToOne
    @JoinColumn(name="reviewer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User_Conference reviewer;

    @ManyToOne
    @Nullable
    @JoinColumn(name="paper_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Paper paper;

    public boolean isCreated(){
        return this.paper!=null && this.reviewer!=null;
    }

}
