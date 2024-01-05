package io.github.tryferos.spring_server.Paper;

public record ReviewRecord(int score, String comment, long paper_id, Review[] author_comment, String newContent) {
}
