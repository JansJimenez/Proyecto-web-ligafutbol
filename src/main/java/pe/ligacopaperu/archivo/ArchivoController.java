package pe.ligacopaperu.archivo;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ArchivoController {
    private final ArchivoStorageService storage;

    public ArchivoController(ArchivoStorageService storage) {
        this.storage = storage;
    }

    @GetMapping("/media/{fileName:.+}")
    public ResponseEntity<Resource> publicImage(@PathVariable String fileName) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(storage.publicContentType(fileName)))
                .cacheControl(CacheControl.maxAge(java.time.Duration.ofDays(7)).cachePublic())
                .header("X-Content-Type-Options", "nosniff")
                .body(storage.publicImage(fileName));
    }

    @GetMapping("/admin/documentos/{fileName:.+}")
    public ResponseEntity<Resource> privateDocument(@PathVariable String fileName) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(storage.privateContentType(fileName)))
                .cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(fileName).build().toString())
                .body(storage.privateDocument(fileName));
    }
}
