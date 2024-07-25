package st.tiy.beitexercise.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import st.tiy.beitexercise.model.request.RequestMeasurement;
import st.tiy.beitexercise.reader.DataReader;
import st.tiy.beitexercise.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class FileReaderService {

	private static final Logger logger = LoggerFactory.getLogger(FileReaderService.class);

	private final Map<String, DataReader> dataReaders;

	public FileReaderService(Map<String, DataReader> dataReaders) {
		this.dataReaders = dataReaders;
	}

	public List<RequestMeasurement> processFile(String fileName) {
		String extension = FileUtils.getFileExtension(fileName);

		if (extension == null || extension.isEmpty()) {
			logger.info("Unable to process file name: {}", fileName);
			return Collections.emptyList();
		}

		if (!dataReaders.containsKey(extension)) {
			logger.info("Unsupported file format: {}", extension);
			return Collections.emptyList();
		}

		DataReader dataReader = dataReaders.get(extension);

		return dataReader.parseDataFromFile(fileName);
	}

	public Optional<File> saveFile(MultipartFile file) throws IOException {
		if (!ensureDownloadFolderExists()) {
			return Optional.empty();
		}

		String fileExtension = FileUtils.getFileExtension(file);
		UUID newFileName = UUID.randomUUID();

		Path path = Files.createFile(Paths.get("downloads", newFileName + "." + fileExtension));
		File convFile = path.toFile();

		try (FileOutputStream fos = new FileOutputStream(convFile)){
			fos.write(file.getBytes());
		} catch (IOException e) {
			return Optional.empty();
		}

		return Optional.of(convFile);
	}

	private boolean ensureDownloadFolderExists() {
		File downloadFolder = new File( "downloads");
		if (!downloadFolder.exists()) {
			return downloadFolder.mkdir();
		}

		return true;
	}


}
