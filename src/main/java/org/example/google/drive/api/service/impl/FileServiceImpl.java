package org.example.google.drive.api.service.impl;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.example.google.drive.api.service.FileService;
import org.example.google.drive.api.util.FileConverterUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
	private final Drive drive;

	public FileServiceImpl(Drive drive) {
		this.drive = drive;
	}

	@Override
	public FileList getFiles(int pageSize, String nextPageToken, String sortBy) throws IOException {
		return drive.files().list()
					.setFields("nextPageToken, files(id, name, parents, mimeType)")
					.setPageSize(pageSize)
					.setPageToken(nextPageToken)
					.setOrderBy(sortBy)
					.execute();
	}

	@Override
	public File uploadFile(@Nullable MultipartFile multipartFile, String name, @Nullable String mimeType, @Nullable List<String> parentIds)
			throws IOException {
		File googleFile = new File();
		googleFile.setName(name);
		if (!CollectionUtils.isEmpty(parentIds)) {
			googleFile.setParents(parentIds);
		}
		// if file is null then it is a folder
		if (multipartFile == null) {
			googleFile.setMimeType("application/vnd.google-apps.folder");
			return drive.files().create(googleFile).setFields("id, name, parents, mimeType").execute();
		}

		java.io.File file = FileConverterUtil.toTempFile(multipartFile);
		FileContent mediaContent = new FileContent(mimeType, file);

		File uploadedFile = drive.files().create(googleFile, mediaContent).setFields("id, name, parents, mimeType").execute();
		if (!file.delete()) {
			log.error("Failed to temp delete file after uploaded {}", file.getName());
		}
		return uploadedFile;
	}

	@Override
	public void deleteFile(String fileId) throws IOException {
		drive.files().delete(fileId).execute();
	}

	@Override
	public File updateFile(@Nullable String fileId, @Nullable MultipartFile multipartFile, String name, @Nullable String mimeType,
						   @Nullable List<String> parentIds)
			throws IOException {
		File googleFile = new File();
		googleFile.setName(name);

		if (!CollectionUtils.isEmpty(parentIds)) {
			googleFile.setParents(parentIds);
		}
		// if file is null then it is a folder
		if (multipartFile == null) {
			googleFile.setMimeType("application/vnd.google-apps.folder");
			return drive.files().update(fileId, googleFile).setFields("id, name, parents, mimeType").execute();
		}
		java.io.File file = FileConverterUtil.toTempFile(multipartFile);
		FileContent mediaContent = new FileContent(mimeType, file);
		File updatedFile = drive.files().update(fileId, googleFile, mediaContent).setFields("id, name, parents, mimeType").execute();
		if (!file.delete()) {
			log.error("Failed to delete temp file after update {}", file.getName());
		}
		return updatedFile;
	}

	@Override
	public ByteArrayOutputStream downloadFile(String fileId) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		drive.files().get(fileId).executeMediaAndDownloadTo(outputStream);
		return outputStream;
	}

	@Override
	public File getFile(String fileId) throws IOException {
		return drive.files().get(fileId).setFields("id, name, parents, mimeType").execute();
	}
}
