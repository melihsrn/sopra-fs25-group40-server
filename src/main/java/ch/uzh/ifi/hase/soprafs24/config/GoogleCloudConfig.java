package ch.uzh.ifi.hase.soprafs24.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class GoogleCloudConfig {

    @Value("${GCP_SERVICE_CREDENTIALS}")
    private String credentialsJson;

    @Bean
    public Storage googleStorage() throws IOException {
        System.out.println("🔍 Checking GCP_SERVICE_CREDENTIALS...");
        if (credentialsJson == null || credentialsJson.isEmpty()) {
            System.err.println("🚨 ERROR: GCP_SERVICE_CREDENTIALS environment variable is missing or empty!");
            throw new IllegalStateException("Missing GCP_SERVICE_CREDENTIALS environment variable");
        } else {
            System.out.println("✅ GCP_SERVICE_CREDENTIALS is set. Length: " + credentialsJson.length());
        }

        return StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(
                        new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))))
                .build()
                .getService();
    }
}


