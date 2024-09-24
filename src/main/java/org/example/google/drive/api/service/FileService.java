package org.example.google.drive.api.service;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import jakarta.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileService {
	FileList getFiles(int pageSize, String nextPageToken, String sortBy) throws IOException;

	File uploadFile(@Nullable MultipartFile file, String name, @Nullable String mimeType, @Nullable List<String> parentIds)
			throws IOException;

	void deleteFile(String fileId) throws IOException;

	File updateFile(String fileId, @Nullable MultipartFile file, String name, @Nullable String mimeType, @Nullable List<String> parentIds)
			throws IOException;

	InputStream downloadFile(String fileId) throws IOException;

	File getFile(String fileId) throws IOException;
}
