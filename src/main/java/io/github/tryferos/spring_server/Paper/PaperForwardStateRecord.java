
package io.github.tryferos.spring_server.Paper;

public record PaperForwardStateRecord(long paper_id, long[] reviewer_id, Review review) {

}
