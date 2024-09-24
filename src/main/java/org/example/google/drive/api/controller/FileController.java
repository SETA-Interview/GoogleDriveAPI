package org.example.google.drive.api.controller;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.example.google.drive.api.service.FileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {
	private final FileService fileService;

	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@GetMapping("")
	@PreAuthorize("hasAnyAuthority('Viewer', 'Admin')")
	public ResponseEntity<FileList> deleteFile(@RequestParam @Valid @Max(100) @Min(0) int pageSize,
											   @RequestParam(required = false) String nextPageToken,
											   @RequestParam(required = false, defaultValue = "name asc") String sortBy) throws IOException {
		return ResponseEntity.ok(fileService.getFiles(pageSize, nextPageToken, sortBy));
	}

	@PostMapping("")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<File> uploadFile(@RequestPart(value = "file", required = false) MultipartFile file,
										   @RequestPart("name") @Valid @Size(max = 100) String name,
										   @RequestPart(value = "mimeType", required = false) @Valid @Size(max = 100) String mineType,
										   @RequestPart(value = "parentIds", required = false) @Valid @Size(
												   max = 100) List<String> parentIds) throws IOException {
		return ResponseEntity.ok(fileService.uploadFile(file, name, mineType, parentIds));
	}

	@PutMapping("")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<File> updateFile(@RequestPart("fileId") String fileId,
										   @RequestPart(value = "file", required = false) MultipartFile file,
										   @RequestPart("name") @Valid @Size(max = 100) String name,
										   @RequestPart(value = "mimeType", required = false) @Valid @Size(max = 100) String mineType,
										   @RequestPart(value = "parentIds", required = false) @Valid @Size(
												   max = 100) List<String> parentIds) throws IOException {
		return ResponseEntity.ok(fileService.updateFile(fileId, file, name, mineType, parentIds));
	}

	@DeleteMapping("/{fileId}")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<Void> deleteFile(@PathVariable String fileId) throws IOException {
		fileService.deleteFile(fileId);
		return ResponseEntity.ok().build();
	}

	@SuppressWarnings("resource")
	@GetMapping("/{fileId}/download")
	@PreAuthorize("hasAnyAuthority('Admin', 'Viewer')")
	public StreamingResponseBody downloadFile(@PathVariable String fileId, HttpServletResponse response) throws IOException {
		File file = fileService.getFile(fileId);
		response.setContentType(file.getMimeType());
		response.setHeader(
				HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + file.getName() + "\"");

		return outputStream -> {
			int bytesRead;
			final int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			InputStream inputStream = fileService.downloadFile(fileId);
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		};
	}
}
