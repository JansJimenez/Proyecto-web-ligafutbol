CREATE TABLE news_articles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title VARCHAR(180) NOT NULL,
    slug VARCHAR(200) NOT NULL UNIQUE,
    summary VARCHAR(300) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(80),
    image_url VARCHAR(255),
    seo_description VARCHAR(180),
    author_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at DATETIME2 NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at DATETIME2,
    CONSTRAINT fk_news_author FOREIGN KEY (author_id) REFERENCES app_users(id) ON DELETE NO ACTION,
    CONSTRAINT ck_news_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED'))
);

CREATE INDEX ix_news_status_published ON news_articles (status, published_at);
