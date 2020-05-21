package com.airmind.demo;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.database.annotations.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Value("${firebase.credential.resource-path}")
    private String keyPath;

    @Bean
    @Primary
    public void firebaseInitialization() throws IOException {

        FileInputStream serviceAccount = new FileInputStream(this.keyPath);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }

    @PostMapping("/verify")
    public boolean verify(@RequestBody VerifyModel model) {

        FirebaseToken decodedToken;
        try {
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(model.getIdToken());

            if (decodedToken != null) {
                return model.getUid().equals(decodedToken.getUid());
            }
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }

        return false;
    }

}

@Data
class VerifyModel {
    @NotNull
    private String uid;
    @NotNull
    private String idToken;
}