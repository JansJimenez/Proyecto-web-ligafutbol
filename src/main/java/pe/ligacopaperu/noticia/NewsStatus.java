package pe.ligacopaperu.noticia;

public enum NewsStatus {
    DRAFT("Borrador"),
    PUBLISHED("Publicada"),
    ARCHIVED("Archivada");

    private final String label;

    NewsStatus(String label) { this.label = label; }

    public String getLabel() { return label; }
}
