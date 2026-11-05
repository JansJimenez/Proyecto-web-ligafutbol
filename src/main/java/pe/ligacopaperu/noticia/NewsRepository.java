package pe.ligacopaperu.noticia;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<NewsArticle, Long> {
    @EntityGraph(attributePaths = "author")
    List<NewsArticle> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "author")
    List<NewsArticle> findByStatusOrderByPublishedAtDesc(NewsStatus status);

    @EntityGraph(attributePaths = "author")
    Optional<NewsArticle> findBySlugAndStatus(String slug, NewsStatus status);

    boolean existsBySlug(String slug);
}
