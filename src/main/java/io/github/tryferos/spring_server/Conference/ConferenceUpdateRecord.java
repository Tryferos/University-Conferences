package io.github.tryferos.spring_server.Conference;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public record ConferenceUpdateRecord(
        @Nonnull long id,
        @Nullable String name,
        @Nullable String description,
        @Nullable long[] pc_members_id,
        @Nullable long[] pc_chairs_id
) {
}
