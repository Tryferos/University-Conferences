package io.github.tryferos.spring_server.Conference;

import io.github.tryferos.spring_server.User.User_Conference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConferenceData {

    private Conference conference;
    private User_Conference[] users;
}
