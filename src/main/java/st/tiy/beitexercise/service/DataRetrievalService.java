package st.tiy.beitexercise.service;

import org.springframework.stereotype.Service;
import st.tiy.beitexercise.model.*;
import st.tiy.beitexercise.repository.MeasurementTimestampRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

		return new ChartData(measurements, calculateConsumptions(chronoGrouped), granularity);
	}

	private List<ConsumptionInterval> calculateConsumptions(Map<OffsetDateTime, List<MeasurementTimestamp>> measurements) {
		return measurements.entrySet().stream()
				.map(this::transformToConsumptionInterval)
				.toList();
	}

	private ConsumptionInterval transformToConsumptionInterval(Map.Entry<OffsetDateTime, List<MeasurementTimestamp>> measurements) {
		List<MeasurementTimestamp> timestamps = measurements.getValue();

		MeasurementTimestamp firstMeasurement = timestamps.get(0);
		MeasurementTimestamp lastMeasurement = timestamps.get(timestamps.size()-1);
		BigDecimal intervalConsumption = lastMeasurement.getConsumption().subtract(firstMeasurement.getConsumption());

		Interval interval = new Interval(firstMeasurement, lastMeasurement);
		return new ConsumptionInterval(interval, intervalConsumption);
	}

}
