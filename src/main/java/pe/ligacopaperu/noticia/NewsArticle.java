package pe.ligacopaperu.noticia;

import java.time.LocalDateTime;

import pe.ligacopaperu.seguridad.UserAccount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "news_articles")
public class NewsArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, unique = true, length = 200)
    private String slug;

    @Column(nullable = false, length = 300)
    private String summary;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 80)
    private String category;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "seo_description", length = 180)
    private String seoDescription;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_news_author"))
    private UserAccount author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NewsStatus status = NewsStatus.DRAFT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @PrePersist
    void setCreatedAt() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getSeoDescription() { return seoDescription; }
    public void setSeoDescription(String seoDescription) { this.seoDescription = seoDescription; }
    public UserAccount getAuthor() { return author; }
    public void setAuthor(UserAccount author) { this.author = author; }
    public NewsStatus getStatus() { return status; }
    public void setStatus(NewsStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
}
