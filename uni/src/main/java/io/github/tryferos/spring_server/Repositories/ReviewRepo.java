package io.github.tryferos.spring_server.Repositories;

import io.github.tryferos.spring_server.Paper.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepo extends JpaRepository<Review, Long>{
    Review[] getReviewsByPaperId(long paperId);

    Review getReviewByPaperIdAndId(long paperId, long review);

    Review getReviewByReviewerId(long reviewer_id);
}
