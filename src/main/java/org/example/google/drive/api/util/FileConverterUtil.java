package org.example.google.drive.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileConverterUtil {

	public static File toTempFile(MultipartFile multipartFile) throws IOException {
		File file = File.createTempFile("temp-file", null);
		multipartFile.transferTo(file);
		file.deleteOnExit();
		return file;
	}
}

