package io.github.tryferos.spring_server.Paper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public record PaperUpdateRecord(
        @Nullable String title,
        @Nullable String vAbstract,
        @Nullable String[] authors,
        @Nullable String[] keywords,
        @Nullable String content,
        @Nonnull long id)  {
}
