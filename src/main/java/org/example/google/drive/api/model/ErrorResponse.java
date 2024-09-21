package org.example.google.drive.api.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ErrorResponse {
	private String message;
	private String path;
	private Instant timestamp;
}
