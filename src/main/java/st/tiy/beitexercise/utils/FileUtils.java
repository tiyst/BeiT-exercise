package st.tiy.beitexercise.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class FileUtils {

	private FileUtils() {
		//Utils class, not meant to be instantiated
	}

	public static String getFileExtension(File file) {
		String fileName = file.getName();
		return getFileExtension(fileName);
	}

	public static String getFileExtension(MultipartFile file) {
		String fileName = file.getOriginalFilename();
		if (fileName == null) {
			return null;
		}

		return getFileExtension(fileName);
	}

	public static String getFileExtension(String fileName) {
		return fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0
				? fileName.substring(fileName.lastIndexOf(".") + 1)
				: null;
	}

}
