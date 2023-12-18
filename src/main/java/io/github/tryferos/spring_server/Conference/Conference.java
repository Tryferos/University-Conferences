package io.github.tryferos.spring_server.Conference;

import io.github.tryferos.spring_server.ErrorMessages;
import io.github.tryferos.spring_server.Stateful;
import io.github.tryferos.spring_server.User.User;
import io.github.tryferos.spring_server.User.User_Conference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.context.annotation.Primary;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

@Data
@AllArgsConstructor
@Entity
@Table(name ="conference")
public class Conference{
    private static final String[] states = {
            "CREATED", "SUBMISSION", "ASSIGNMENT", "REVIEW", "DECISION", "FINAL_SUBMISSION", "FINAL"
    };

    @Column(unique = true, nullable = false)
    private String name;
    @Column
    private String description;

    public Conference(){
        this.date = new Date();
        this.state = ConferenceState.values()[0];
    }

    @Column(columnDefinition = "ENUM(\"CREATED\", \"SUBMISSION\", \"ASSIGNMENT\", \"REVIEW\", \"DECISION\", \"FINAL_SUBMISSION\", \"FINAL\") default \"CREATED\"")
    @Enumerated(EnumType.STRING)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private ConferenceState state;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="created_at", columnDefinition = "DATETIME default now()")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne(optional = false)
    @JoinColumn( nullable = false, unique = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User creator;

    public void forwardState() throws ConferenceException{
        ConferenceState newState = this.getState();
        switch (state){
            case CREATED ->
                newState = ConferenceState.SUBMISSION; //Submission of papers to the conference starts.
            case SUBMISSION ->
                newState = ConferenceState.ASSIGNMENT; //Assignment of reviewers to papers starts.
            case ASSIGNMENT ->
                newState = ConferenceState.REVIEW; //Review of submitted papers is allowed.
            case REVIEW ->
                newState = ConferenceState.DECISION; //Approval or rejection of papers is allowed.
            case DECISION ->
                newState = ConferenceState.FINAL_SUBMISSION; //Approved papers' final submission is allowed.
            case FINAL_SUBMISSION ->
                newState = ConferenceState.FINAL; //Mark approved papers as approved and rejected as rejected.
            case FINAL -> throw new ConferenceException(ErrorMessages.ConferenceInFinalState);        }
        this.setState(newState);
    }


}
