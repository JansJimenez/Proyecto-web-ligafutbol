package pe.ligacopaperu.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Configuration
public class FirebaseConfig {

    private final String serviceAccountResource;
    private final String firebaseConfigJson;

    public FirebaseConfig(
            @Value("${app.firebase.service-account:firebase-service-account.json}") String serviceAccountResource,
            @Value("${FIREBASE_CONFIG_JSON:}") String firebaseConfigJson) {
        this.serviceAccountResource = serviceAccountResource;
        this.firebaseConfigJson = firebaseConfigJson;
    }

    @Bean
    @SuppressWarnings("null")
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }
        try (InputStream credentialsStream = resolveCredentialsStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .build();
            return FirebaseApp.initializeApp(options);
        }
    }

    private InputStream resolveCredentialsStream() throws IOException {
        ClassPathResource resource = new ClassPathResource(
                java.util.Objects.requireNonNull(serviceAccountResource));
        if (resource.exists()) {
            return resource.getInputStream();
        }
        String envJson = (firebaseConfigJson != null && !firebaseConfigJson.isBlank())
                ? firebaseConfigJson
                : System.getenv("FIREBASE_CONFIG_JSON");
        if (envJson != null && !envJson.isBlank()) {
            return new ByteArrayInputStream(envJson.trim().getBytes(StandardCharsets.UTF_8));
        }
        throw new IllegalStateException(
                "No se encontró el archivo '" + serviceAccountResource
                        + "' en resources ni la variable de entorno FIREBASE_CONFIG_JSON.");
    }

    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        return FirestoreClient.getFirestore(firebaseApp);
    }
}
