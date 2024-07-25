package st.tiy.beitexercise.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import st.tiy.beitexercise.model.MeasurementTimestamp;
import st.tiy.beitexercise.model.request.RequestMeasurement;
import st.tiy.beitexercise.service.FileReaderService;
import st.tiy.beitexercise.service.MeasurementProcessingService;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/measurement")
public class MeasurementsController {

	private final FileReaderService fileReaderService;
	private final MeasurementProcessingService measurementProcessingService;

	public MeasurementsController(FileReaderService fileReaderService, MeasurementProcessingService measurementProcessingService) {
		this.fileReaderService = fileReaderService;
		this.measurementProcessingService = measurementProcessingService;
	}

	@PostMapping("/uploadFile")
	public Map<OffsetDateTime, MeasurementTimestamp> uploadMeasurements(@RequestBody MultipartFile file) throws IOException {
		Optional<File> savedFile = this.fileReaderService.saveFile(file);
		if (savedFile.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<OffsetDateTime, MeasurementTimestamp> measurements = this.measurementProcessingService.processFile(savedFile.get().getPath());
		// TODO remove file
		return measurements;
	}

	@PostMapping("/upload")
	public Map<OffsetDateTime, MeasurementTimestamp> uploadMeasurements(@RequestBody List<RequestMeasurement> measurements) {
		return this.measurementProcessingService.processData(measurements);
	}

	@GetMapping("/demo-full")
	public Map<OffsetDateTime, MeasurementTimestamp> demoFullData() {
		return this.measurementProcessingService.processFile("src/main/resources/static/full/data-full.csv");
	}

	@GetMapping("/demo-scarce")
	public Map<OffsetDateTime, MeasurementTimestamp> demoScarceData() {
		return this.measurementProcessingService.processFile("src/main/resources/static/scarce/data-scarce.json");
	}
}
