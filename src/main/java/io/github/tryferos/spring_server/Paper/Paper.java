package io.github.tryferos.spring_server.Paper;

import io.github.tryferos.spring_server.Conference.Conference;
import io.github.tryferos.spring_server.Conference.ConferenceState;
import io.github.tryferos.spring_server.ErrorMessages;
import io.github.tryferos.spring_server.Services.PaperService;
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
import org.springframework.beans.factory.annotation.Autowired;
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

    public PaperState forwardState(PaperForwardStateRecord data) throws PaperException,PaperStateException{
        PaperState newState = this.getState();
        switch (state){
            case CREATED -> {
                if(!conference.getState().equals(ConferenceState.SUBMISSION))
                    throw new PaperStateException(ErrorMessages.PaperWrongStateConference, conference.getId(), ConferenceState.SUBMISSION);
                if(this.content==null || this.content.isEmpty())
                    throw new PaperStateException(ErrorMessages.NoNullConstraint, "content");
                newState = PaperState.SUBMITTED;
            }
            case SUBMITTED ->{
                if(!conference.getState().equals(ConferenceState.ASSIGNMENT))
                    throw new PaperStateException(ErrorMessages.PaperWrongStateConference, conference.getId(), ConferenceState.ASSIGNMENT);
                newState = PaperState.REVIEWED; //Assignment of reviewers to papers starts.
            }
            //ConfeState: created -> submission -> assignment
            //                                  -> review (papers reviewed)
            //                                             -> decision (papers approved)                      -> final (papers accepted)
            //PaperState: created -> submitted -> reviewed -> rejected (conference decision)
            //                                             -> approved  (conference decision) -> submit final paper (final_submission)
            //                                                                                                -> accepted
            case APPROVED ->{
                if(!conference.getState().equals(ConferenceState.FINAL))
                    throw new PaperStateException(ErrorMessages.PaperWrongStateConference, conference.getId(), ConferenceState.FINAL);
                newState = PaperState.ACCEPTED; //Assignment of reviewers to papers starts.
            }
            case REJECTED -> {
                throw new PaperException("Paper with id %s was rejected by the conference.", this.getId());
            }
            case ACCEPTED -> {
                throw new PaperException("Paper with id %s is already accepted by the conference.", this.getId());
            }
        }
        this.setState(newState);
        return newState;
    }

}
