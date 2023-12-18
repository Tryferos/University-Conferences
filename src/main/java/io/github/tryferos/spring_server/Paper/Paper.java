package io.github.tryferos.spring_server.Paper;

import io.github.tryferos.spring_server.Conference.Conference;
import io.github.tryferos.spring_server.Conference.ConferenceState;
import io.github.tryferos.spring_server.User.User;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@Entity
@Table(name ="paper")
public class Paper{

    private static final String[] states = {
            "CREATED", "SUBMITTED", "REVIEWED", "REJECTED", "APPROVED", "ACCEPTED"
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false)
    private String title;
    @Column(nullable = false)
    private String vAbstract;
    @Column
    private String content;
    @Column
    private String keywords;

    public Paper(){
        this.date = new Date();
        this.state = PaperState.values()[0];
    }

    @Column(columnDefinition = "ENUM(\"CREATED\", \"SUBMITTED\", \"REVIEWED\", \"REJECTED\", \"APPROVED\", \"ACCEPTED\") default \"CREATED\"")
    @Enumerated(EnumType.STRING)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private PaperState state;

    @Column(name="created_at", columnDefinition = "DATETIME default now()")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="conference_id", nullable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Conference conference;




    //Initialization


    public boolean proceed() {
        return false;
    }

}
