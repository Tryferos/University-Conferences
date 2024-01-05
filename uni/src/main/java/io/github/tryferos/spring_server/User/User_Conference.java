package io.github.tryferos.spring_server.User;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.tryferos.spring_server.Conference.Conference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="user_conference")
public class User_Conference{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="conference_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Conference conference;
    @Column
    private boolean pcChair;
    @Column
    private boolean pcMember;

    public boolean hasNoRole(){
        return !pcChair&& !pcMember;
    }

    //We do not need to throw an error us this comes from the database, where we ensured this cannot happen.
//    public User_Conference(int id, String fullname, String username, boolean pc_chair, boolean pc_member) {
//        this.pc_chair = pc_chair;
//        this.pc_member = pc_member;
//    }
//
//    private void init(boolean pc_chair, boolean pc_member)throws UserException{
//        if(pc_chair==true && pc_member==true) throw new UserException("A user cannot be both pc chair and pc member");
//        this.pc_chair = pc_chair;
//        this.pc_member = pc_member;
//    }
//
//    public User_Conference(String fullname, String username, boolean pc_chair, boolean pc_member) throws UserException{
//        init(pc_chair, pc_member);
//    }
}
