package io.github.tryferos.spring_server.Repositories;

import io.github.tryferos.spring_server.User.User_Conference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface User_ConferenceRepo extends JpaRepository<User_Conference, Integer> {
    User_Conference[] findUser_ConferencesByPcChairTrueAndConferenceIdAndPcMemberIn(long id, long[] pc_chairs_id);
    User_Conference[] findUser_ConferencesByPcMemberTrueAndConferenceIdAndPcMemberIn(long id, long[] pc_members_id);
    User_Conference[] findUser_ConferencesByPcChairTrueAndConferenceId(long id);
    User_Conference[] findUser_ConferencesByPcMemberTrueAndConferenceId(long id);
    User_Conference[] findUser_ConferencesByConferenceId(long conf_id);

    User_Conference findUser_ConferenceByConferenceIdAndUserId(long conf_id, long user_id);

    void removeUser_ConferencesByConferenceId(long id);
    void removeUser_ConferencesByConferenceIdAndPcChairTrue(long id);
    void removeUser_ConferencesByConferenceIdAndPcMemberTrue(long id);




}
