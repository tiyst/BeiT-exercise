package st.tiy.beitexercise.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import st.tiy.beitexercise.model.MeasurementTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface MeasurementTimestampRepository extends CrudRepository<MeasurementTimestamp, Long> {

	List<MeasurementTimestamp> findAllByDateTimeBetween(OffsetDateTime dateFrom, OffsetDateTime dateTo);

	List<MeasurementTimestamp> findByDateTimeIn(List<OffsetDateTime> date);

}
