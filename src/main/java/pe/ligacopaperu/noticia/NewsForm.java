package pe.ligacopaperu.noticia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class NewsForm {
    @NotBlank(message = "Ingresa el titular del comunicado o nota.")
    @Size(max = 180, message = "El titular no debe superar 180 caracteres.")
    private String title;

    @NotBlank(message = "Ingresa una bajada o resumen informativo.")
    @Size(max = 300, message = "El resumen no debe superar 300 caracteres.")
    private String summary;

    @NotBlank(message = "Escribe el contenido completo de la nota o resolución.")
    @Size(max = 20000, message = "El contenido no debe superar 20000 caracteres.")
    private String content;

    @Size(max = 80, message = "La categoría no debe superar 80 caracteres.")
    private String category = "Copa Perú";

    private MultipartFile imageFile;

    private boolean removeImage;

    private String currentImageUrl;

    @Size(max = 180, message = "La descripción SEO no debe superar 180 caracteres.")
    private String seoDescription;

    @NotNull(message = "Selecciona el estado de publicación.")
    private NewsStatus status = NewsStatus.DRAFT;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public MultipartFile getImageFile() { return imageFile; }
    public void setImageFile(MultipartFile imageFile) { this.imageFile = imageFile; }
    public boolean isRemoveImage() { return removeImage; }
    public void setRemoveImage(boolean removeImage) { this.removeImage = removeImage; }
    public String getCurrentImageUrl() { return currentImageUrl; }
    public void setCurrentImageUrl(String currentImageUrl) { this.currentImageUrl = currentImageUrl; }
    public String getSeoDescription() { return seoDescription; }
    public void setSeoDescription(String seoDescription) { this.seoDescription = seoDescription; }
    public NewsStatus getStatus() { return status; }
    public void setStatus(NewsStatus status) { this.status = status; }
}
