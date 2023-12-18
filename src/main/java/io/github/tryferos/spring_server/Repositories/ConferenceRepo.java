package io.github.tryferos.spring_server.Repositories;

import io.github.tryferos.spring_server.Conference.Conference;
import io.github.tryferos.spring_server.Conference.ConferenceState;
import io.github.tryferos.spring_server.Paper.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConferenceRepo extends JpaRepository<Conference, Long> {
    List<Conference> getConferencesByNameIgnoreCaseOrDescriptionIgnoreCaseOrderByName(String name, String description);
    int deleteConferenceByIdIsAndStateIs(long id, ConferenceState state);
}
