package pe.ligacopaperu.archivo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArchivoStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArchivoStorageService.class);
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final String FILE_NAME_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(png|jpg|pdf)";

    private final Path root;

    public ArchivoStorageService(@Value("${app.uploads.location:./data/uploads}") String location) {
        root = Path.of(location).toAbsolutePath().normalize();
    }

    public String storePublicImage(MultipartFile upload) {
        return store(upload, false);
    }

    public String storePrivateDocument(MultipartFile upload) {
        return store(upload, true);
    }

    public String replacePublicImage(String previousReference, MultipartFile upload, boolean remove) {
        return replace(previousReference, upload, remove, false);
    }

    public String replacePrivateDocument(String previousReference, MultipartFile upload, boolean remove) {
        return replace(previousReference, upload, remove, true);
    }

    public Resource publicImage(String fileName) {
        return resource(root.resolve("public"), fileName);
    }

    public Resource privateDocument(String fileName) {
        return resource(root.resolve("private"), fileName);
    }

    public String publicContentType(String fileName) {
        requireSafeFileName(fileName);
        return fileName.endsWith(".png") ? "image/png" : "image/jpeg";
    }

    public String privateContentType(String fileName) {
        requireSafeFileName(fileName);
        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        }
        return publicContentType(fileName);
    }

    public void scheduleReplacement(String previousReference, String nextReference) {
        if (java.util.Objects.equals(previousReference, nextReference)) {
            return;
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("La actualización del archivo requiere una transacción activa.");
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_COMMITTED) {
                    deleteManagedFile(previousReference);
                } else {
                    deleteManagedFile(nextReference);
                }
            }
        });
    }

    private String store(MultipartFile upload, boolean privateDocument) {
        if (upload == null || upload.isEmpty()) {
            return null;
        }
        if (upload.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Cada archivo debe pesar como máximo 5 MB.");
        }
        byte[] content;
        try {
            content = upload.getBytes();
        } catch (IOException exception) {
            throw new IllegalArgumentException("No se pudo leer el archivo seleccionado.", exception);
        }
        if (content.length == 0 || content.length > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo está vacío o supera el límite de 5 MB.");
        }

        String extension = validateFileContent(content, privateDocument);
        byte[] storedContent = extension.equals("pdf") ? content : normalizeImage(content, extension);
        String fileName = UUID.randomUUID() + "." + extension;
        Path destinationDirectory = root.resolve(privateDocument ? "private" : "public");
        Path destination = destinationDirectory.resolve(fileName).normalize();
        if (!destination.startsWith(destinationDirectory)) {
            throw new IllegalStateException("La ubicación del archivo no es válida.");
        }
        try {
            Files.createDirectories(destinationDirectory);
            Files.write(destination, storedContent, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (IOException exception) {
            LOGGER.error("No se pudo guardar un archivo cargado en el almacenamiento local.", exception);
            throw new IllegalStateException("No se pudo guardar el archivo. Inténtalo nuevamente.", exception);
        }
        return privateDocument ? "/admin/documentos/" + fileName : "/media/" + fileName;
    }

    private String replace(String previousReference, MultipartFile upload, boolean remove, boolean privateDocument) {
        if (remove) {
            scheduleReplacement(previousReference, null);
            return null;
        }
        if (upload == null || upload.isEmpty()) {
            return previousReference;
        }
        String nextReference = store(upload, privateDocument);
        scheduleReplacement(previousReference, nextReference);
        return nextReference;
    }

    private String validateFileContent(byte[] content, boolean privateDocument) {
        String extension = imageExtension(content);
        if (extension != null) {
            return extension;
        }
        if (privateDocument && content.length >= 5
                && content[0] == '%' && content[1] == 'P' && content[2] == 'D'
                && content[3] == 'F' && content[4] == '-') {
            return "pdf";
        }
        throw new IllegalArgumentException(privateDocument
                ? "El documento debe ser una imagen PNG/JPG o un archivo PDF válido."
                : "La imagen debe ser un archivo PNG o JPG válido.");
    }

    private String imageExtension(byte[] content) {
        if (content.length >= 8 && (content[0] & 0xff) == 0x89
                && content[1] == 'P' && content[2] == 'N' && content[3] == 'G'
                && content[4] == 0x0d && content[5] == 0x0a
                && content[6] == 0x1a && content[7] == 0x0a) {
            return "png";
        }
        if (content.length >= 3 && (content[0] & 0xff) == 0xff
                && (content[1] & 0xff) == 0xd8 && (content[2] & 0xff) == 0xff) {
            return "jpg";
        }
        return null;
    }

    private byte[] normalizeImage(byte[] content, String extension) {
        try (ImageInputStream imageInput = ImageIO.createImageInputStream(new ByteArrayInputStream(content))) {
            if (imageInput == null) {
                throw new IllegalArgumentException("No se pudo interpretar la imagen.");
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInput);
            if (!readers.hasNext()) {
                throw new IllegalArgumentException("La imagen está dañada o no se puede abrir.");
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(imageInput, true, true);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width < 1 || height < 1 || width > 6000 || height > 6000
                        || (long) width * height > 12_000_000L) {
                    throw new IllegalArgumentException("La imagen supera las dimensiones permitidas.");
                }
                BufferedImage decoded = reader.read(0);
                if (decoded == null) {
                    throw new IllegalArgumentException("La imagen está dañada o no se puede abrir.");
                }
                ByteArrayOutputStream normalized = new ByteArrayOutputStream();
                if (!ImageIO.write(decoded, extension, normalized)) {
                    throw new IllegalArgumentException("No se pudo procesar la imagen seleccionada.");
                }
                return normalized.toByteArray();
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("La imagen está dañada o no se puede abrir.", exception);
        }
    }

    private Resource resource(Path directory, String fileName) {
        requireSafeFileName(fileName);
        Path file = directory.resolve(fileName).normalize();
        if (!file.startsWith(directory) || !Files.isRegularFile(file)) {
            throw new IllegalArgumentException("El archivo solicitado no está disponible.");
        }
        return new FileSystemResource(file);
    }

    private void deleteManagedFile(String reference) {
        if (reference == null) {
            return;
        }
        String prefix = reference.startsWith("/media/") ? "/media/"
                : reference.startsWith("/admin/documentos/") ? "/admin/documentos/" : null;
        if (prefix == null) {
            return;
        }
        String fileName = reference.substring(prefix.length());
        try {
            requireSafeFileName(fileName);
            Files.deleteIfExists(root.resolve(prefix.equals("/media/") ? "public" : "private")
                    .resolve(fileName).normalize());
        } catch (IOException | IllegalArgumentException exception) {
            LOGGER.warn("No se pudo retirar un archivo reemplazado del almacenamiento local: {}", fileName, exception);
        }
    }

    private void requireSafeFileName(String fileName) {
        if (fileName == null || !fileName.matches(FILE_NAME_PATTERN)) {
            throw new IllegalArgumentException("El nombre del archivo no es válido.");
        }
    }
}
