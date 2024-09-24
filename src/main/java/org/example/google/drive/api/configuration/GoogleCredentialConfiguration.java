package org.example.google.drive.api.configuration;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
public class GoogleCredentialConfiguration {
	@Bean
	public GoogleCredential googleCredential(@Value("${application.google.api.json.credential}") Resource resource)
			throws IOException {
		return GoogleCredential.fromStream(resource.getInputStream())
							   .createScoped(DriveScopes.all());
	}

	@Bean
	public Drive drive(GoogleCredential googleCredential, @Value("${spring.application.name}") String applicationName)
			throws GeneralSecurityException, IOException {
		return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), googleCredential)
				.setApplicationName(applicationName)
				.build();
	}
}
