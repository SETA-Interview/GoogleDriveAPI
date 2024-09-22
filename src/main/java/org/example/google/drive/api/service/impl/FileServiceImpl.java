package org.example.google.drive.api.service.impl;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.example.google.drive.api.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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

		InputStreamContent mediaContent = new InputStreamContent(mimeType, multipartFile.getInputStream());
		return drive.files().create(googleFile, mediaContent).setFields("id, name, parents, mimeType").execute();
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
		InputStreamContent mediaContent = new InputStreamContent(mimeType, multipartFile.getInputStream());
		return drive.files().update(fileId, googleFile, mediaContent).setFields("id, name, parents, mimeType").execute();
	}

	@Override
	public InputStream downloadFile(String fileId) throws IOException {
		 return drive.files().get(fileId).executeMediaAsInputStream();
	}

	@Override
	public File getFile(String fileId) throws IOException {
		return drive.files().get(fileId).setFields("id, name, parents, mimeType").execute();
	}
}
