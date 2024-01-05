package io.github.tryferos.spring_server.Repositories;

import io.github.tryferos.spring_server.Paper.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperRepo extends JpaRepository<Paper, Long> {
    Paper[] getPapersByTitleIgnoreCaseOrVAbstractIgnoreCaseOrderByTitle(String title, String vAbstract);
}
