package st.tiy.beitexercise.service;

import org.springframework.stereotype.Service;
import st.tiy.beitexercise.model.Interval;
import st.tiy.beitexercise.model.MeasurementTimestamp;
import st.tiy.beitexercise.model.request.RequestMeasurement;
import st.tiy.beitexercise.repository.MeasurementTimestampRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MeasurementProcessingService {

	public static final ZoneOffset PRAGUE_OFFSET = ZoneOffset.ofHours(1);

	private final MeasurementTimestampRepository repository;
	private final FileReaderService fileReaderService;

	public MeasurementProcessingService(MeasurementTimestampRepository repository, FileReaderService fileReaderService) {
		this.repository = repository;
		this.fileReaderService = fileReaderService;
	}

	public Map<OffsetDateTime, MeasurementTimestamp> processFile(String filePath) {
		return processData(this.fileReaderService.processFile(filePath));
	}

	public Map<OffsetDateTime, MeasurementTimestamp> processData(List<RequestMeasurement> requestMeasurements) {
		Map<OffsetDateTime, MeasurementTimestamp> measurements = transformMeasurements(requestMeasurements);
		this.interpolateMissingData(measurements);

		Iterable<MeasurementTimestamp> savedMeasurements = saveMeasurements(measurements.values());

		return StreamSupport.stream(savedMeasurements.spliterator(), false)
				.collect(Collectors.toMap(MeasurementTimestamp::getDateTime,
						measure -> measure,
						(m1, m2) -> m2,
						TreeMap::new));
	}

	private Map<OffsetDateTime, MeasurementTimestamp> transformMeasurements(List<RequestMeasurement> measurements) {
		return measurements.stream()
				.map(measurement -> new MeasurementTimestamp(parseTimestamp(measurement.ts()),
						BigDecimal.valueOf(measurement.value())))
				.collect(Collectors.toMap(MeasurementTimestamp::getDateTime,
						measurement -> measurement,
						(n, m) -> m,
						TreeMap::new)
				);
	}

	public Iterable<MeasurementTimestamp> saveMeasurements(Collection<MeasurementTimestamp> measurements) {
		List<MeasurementTimestamp> existingRecords = this.repository.findByDateTimeIn(new ArrayList<>(measurements.stream().map(MeasurementTimestamp::getDateTime).toList()));

		Set<MeasurementTimestamp> newMeasurements = measurements.stream()
				.filter(measure -> existingRecords.stream()
						.map(MeasurementTimestamp::getDateTime)
						.noneMatch(existing -> measure.getDateTime().equals(existing)))
				.collect(Collectors.toSet());

		return this.repository.saveAll(newMeasurements);
	}

	private OffsetDateTime parseTimestamp(String requestTimestamp) {
		return OffsetDateTime.parse(requestTimestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
							 .withOffsetSameInstant(PRAGUE_OFFSET);
	}

	public void interpolateMissingData(Map<OffsetDateTime, MeasurementTimestamp> measurements) {
		List<Interval> missingIntervals = identifyMissingIntervals(measurements);

		List<MeasurementTimestamp> missingTimestamps = missingIntervals.stream()
				.flatMap(interval -> this.interpolateMissingMeasurements(interval).stream())
				.toList();

		measurements.putAll(missingTimestamps.stream()
				.collect(Collectors.toMap(MeasurementTimestamp::getDateTime, timestamp -> timestamp)));
	}

	private List<Interval> identifyMissingIntervals(Map<OffsetDateTime, MeasurementTimestamp> measurements) {
		ArrayList<OffsetDateTime> timestamps = new ArrayList<>(measurements.keySet());

		List<Interval> missingIntervals = new ArrayList<>();

		for (int i = 0; i < timestamps.size() - 1; i++) {
			if (ChronoUnit.HOURS.between(timestamps.get(i), timestamps.get(i + 1)) > 1) {
				missingIntervals.add(new Interval(
						measurements.get(timestamps.get(i)),
						measurements.get(timestamps.get(i + 1)))
				);
			}
		}

		return missingIntervals;
	}

	private List<MeasurementTimestamp> interpolateMissingMeasurements(Interval missingInterval) {
		MeasurementTimestamp start = missingInterval.start();
		MeasurementTimestamp end = missingInterval.end();

		long missingHours = ChronoUnit.HOURS.between(start.getDateTime(), end.getDateTime()) - 1; //start is inclusive
		BigDecimal consumptionAtStart = start.getConsumption();

		BigDecimal product = end.getConsumption().subtract(consumptionAtStart);
		BigDecimal productPerHour = product.divide(BigDecimal.valueOf(missingHours + 1), RoundingMode.HALF_EVEN);

		List<MeasurementTimestamp> missingTimestamps = new ArrayList<>();
		for (int i = 1; i <= missingHours; i++) {
			OffsetDateTime missingTimestamp = start.getDateTime().plusHours(i);
			BigDecimal consumption = consumptionAtStart.add(productPerHour.multiply(BigDecimal.valueOf(i)));
			missingTimestamps.add(new MeasurementTimestamp(missingTimestamp, consumption, true));
		}

		return missingTimestamps;
	}
}


