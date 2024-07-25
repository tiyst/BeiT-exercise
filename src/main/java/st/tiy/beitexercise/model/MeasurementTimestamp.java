package st.tiy.beitexercise.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(indexes = { @Index(name = "dateTime", columnList = "dateTime")},
		uniqueConstraints = { @UniqueConstraint(columnNames = { "dateTime" })}
)
public class MeasurementTimestamp {

	@Id
	@GeneratedValue
	private Long measurementId;

//	private Long meterId;

	private OffsetDateTime dateTime;
	private BigDecimal consumption;
	private boolean interpolated;

	public MeasurementTimestamp(OffsetDateTime dateTime, BigDecimal consumption, boolean interpolated) {
		this.dateTime = dateTime;
		this.consumption = consumption;
		this.interpolated = interpolated;
	}

	public MeasurementTimestamp(OffsetDateTime dateTime, BigDecimal consumption) {
		this.dateTime = dateTime;
		this.consumption = consumption;
		this.interpolated = false;
	}

	public MeasurementTimestamp() {

	}

	public OffsetDateTime getDateTime() {
		return dateTime;
	}

	public BigDecimal getConsumption() {
		return consumption;
	}

	public boolean isInterpolated() {
		return interpolated;
	}

	public void setMeasurementId(Long id) {
		this.measurementId = id;
	}

	public Long getMeasurementId() {
		return measurementId;
	}

	@Override
	public int hashCode() {
		return this.dateTime.hashCode() + this.consumption.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof MeasurementTimestamp)) return false;

		MeasurementTimestamp o = (MeasurementTimestamp) obj;
		return (this.dateTime.equals(o.getDateTime()))
				&& this.consumption.equals(o.getConsumption());
	}
}
