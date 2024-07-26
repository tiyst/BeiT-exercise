package st.tiy.beitexercise.service;

import org.springframework.stereotype.Service;
import st.tiy.beitexercise.model.*;
import st.tiy.beitexercise.repository.MeasurementTimestampRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DataRetrievalService {

	private final MeasurementTimestampRepository repository;

	public DataRetrievalService(MeasurementTimestampRepository repository) {
		this.repository = repository;
	}

	public ChartData getChartData(OffsetDateTime from, OffsetDateTime to, TimeUnit granularity) {
		List<MeasurementTimestamp> measurements = this.repository.findAllByDateTimeBetween(from, to);

		Map<OffsetDateTime, List<MeasurementTimestamp>> chronoGrouped = measurements.stream()
				.collect(Collectors.groupingBy(m -> m.getDateTime().truncatedTo(granularity.chronoUnit)));
		chronoGrouped = new TreeMap<>(chronoGrouped);

		return new ChartData(calculateMeasurements(chronoGrouped), calculateConsumptions(chronoGrouped), granularity);
	}

	private List<MeasurementTimestamp> calculateMeasurements(Map<OffsetDateTime, List<MeasurementTimestamp>> chronoGrouped) {
		return chronoGrouped.values().stream()
				.map(measurementTimestamps -> measurementTimestamps.get(0))
				.toList();
	}

	private List<ConsumptionInterval> calculateConsumptions(Map<OffsetDateTime, List<MeasurementTimestamp>> chronoGrouped) {
		List<Map.Entry<OffsetDateTime, List<MeasurementTimestamp>>> entries = chronoGrouped.entrySet().stream().toList();

		return IntStream.range(0, entries.size() - 1)
				.mapToObj(i -> {
					MeasurementTimestamp firstTimestamp = entries.get(i).getValue().get(0);
					MeasurementTimestamp lastTimestamp = entries.get(i + 1).getValue().get(0);
					BigDecimal firstConsumption = firstTimestamp.getConsumption();
					BigDecimal lastConsumption = lastTimestamp.getConsumption();

					return new ConsumptionInterval(
							new Interval(firstTimestamp, lastTimestamp),
							lastConsumption.subtract(firstConsumption)
					);
				})
				.toList();
	}

}
