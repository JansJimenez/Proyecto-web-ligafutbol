package pe.ligacopaperu.noticia;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.firestore.Firestore;

import pe.ligacopaperu.archivo.ArchivoStorageService;
import pe.ligacopaperu.auditoria.AuditService;
import pe.ligacopaperu.seguridad.UserAccount;
import pe.ligacopaperu.seguridad.UserRepository;

@Service
public class NewsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewsService.class);

    private final NewsRepository news;
    private final UserRepository users;
    private final AuditService audit;
    private final ArchivoStorageService storage;
    private final Firestore firestore;

    @Autowired
    public NewsService(NewsRepository news, UserRepository users, AuditService audit, ArchivoStorageService storage,
            Firestore firestore) {
        this.news = news;
        this.users = users;
        this.audit = audit;
        this.storage = storage;
        this.firestore = firestore;
    }

    public NewsService(NewsRepository news, UserRepository users, AuditService audit, ArchivoStorageService storage) {
        this(news, users, audit, storage, null);
    }

    @Transactional(readOnly = true)
    public List<NewsArticle> administrationList() {
        return news.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<NewsArticle> publicList() {
        return news.findByStatusOrderByPublishedAtDesc(NewsStatus.PUBLISHED);
    }

    @Transactional(readOnly = true)
    public long countPublished() {
        return news.countByStatus(NewsStatus.PUBLISHED);
    }

    @Transactional(readOnly = true)
    public NewsArticle publicArticle(String slug) {
        return news.findBySlugAndStatus(slug, NewsStatus.PUBLISHED)
                .orElseThrow(() -> new IllegalArgumentException("La noticia solicitada no está disponible."));
    }

    @Transactional(readOnly = true)
    public NewsForm formFor(long id) {
        NewsArticle article = find(id);
        NewsForm form = new NewsForm();
        form.setTitle(article.getTitle());
        form.setSummary(article.getSummary());
        form.setContent(article.getContent());
        form.setCategory(article.getCategory());
        form.setCurrentImageUrl(article.getImageUrl());
        form.setSeoDescription(article.getSeoDescription());
        form.setStatus(article.getStatus());
        return form;
    }

    @Transactional
    public NewsArticle save(Long id, NewsForm form, String username) {
        NewsArticle article = id == null ? new NewsArticle() : find(id);
        UserAccount author = users.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el autor autenticado."));
        article.setTitle(form.getTitle().trim());
        article.setSummary(form.getSummary().trim());
        article.setContent(form.getContent().trim());
        article.setCategory(clean(form.getCategory()));
        article.setImageUrl(storage.replacePublicImage(article.getImageUrl(), form.getImageFile(),
                form.isRemoveImage()));
        article.setSeoDescription(clean(form.getSeoDescription()));
        article.setAuthor(author);
        if (form.getStatus() == NewsStatus.PUBLISHED && article.getPublishedAt() == null) {
            article.setPublishedAt(LocalDateTime.now());
        }
        if (form.getStatus() == NewsStatus.DRAFT) {
            article.setPublishedAt(null);
        }
        article.setStatus(form.getStatus());
        String slug = slugify(form.getTitle());
        if (news.existsBySlug(slug) && (id == null || !article.getSlug().equals(slug))) {
            slug += "-" + UUID.randomUUID().toString().substring(0, 8);
        }
        article.setSlug(slug);
        NewsArticle saved = news.save(article);
        syncToFirestore(saved);
        audit.record(username, id == null ? "NEWS_CREATED" : "NEWS_UPDATED",
                "NEWS", saved.getId(), saved.getTitle() + " (" + saved.getStatus() + ")");
        return saved;
    }

    @Transactional
    public void archive(long id, String username) {
        NewsArticle article = find(id);
        article.setStatus(NewsStatus.ARCHIVED);
        NewsArticle saved = news.save(article);
        syncToFirestore(saved);
        audit.record(username, "NEWS_ARCHIVED", "NEWS", id, saved.getTitle());
    }

    private NewsArticle find(long id) {
        return news.findById(id).orElseThrow(() -> new IllegalArgumentException("Noticia no encontrada."));
    }

    @SuppressWarnings("null")
    private void syncToFirestore(NewsArticle article) {
        if (firestore == null || article == null || article.getId() == null) {
            return;
        }
        try {
            Map<String, Object> doc = new LinkedHashMap<>();
            doc.put("id", article.getId());
            doc.put("title", article.getTitle());
            doc.put("slug", article.getSlug());
            doc.put("summary", article.getSummary());
            doc.put("content", article.getContent());
            doc.put("category", article.getCategory());
            doc.put("imageUrl", article.getImageUrl());
            doc.put("seoDescription", article.getSeoDescription());
            doc.put("authorId", article.getAuthor() != null ? article.getAuthor().getId() : null);
            doc.put("status", article.getStatus() != null ? article.getStatus().name() : null);
            doc.put("createdAt", article.getCreatedAt() != null ? article.getCreatedAt().toString() : null);
            doc.put("publishedAt", article.getPublishedAt() != null ? article.getPublishedAt().toString() : null);
            firestore.collection("news_articles").document(java.util.Objects.requireNonNull(String.valueOf(article.getId()))).set(doc);
        } catch (Exception exception) {
            LOGGER.warn("No se pudo sincronizar la noticia en Firestore.", exception);
        }
    }

    private String slugify(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized.isBlank() ? "noticia" : normalized;
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
