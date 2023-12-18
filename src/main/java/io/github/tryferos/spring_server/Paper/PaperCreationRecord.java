package io.github.tryferos.spring_server.Paper;

public record PaperCreationRecord(String title, String vAbstract, String[] authors,long conf_id) {
}
