package st.tiy.beitexercise.model;

public record Interval(MeasurementTimestamp start, MeasurementTimestamp end) implements Comparable<Interval> {

	@Override
	public int compareTo(Interval interval) {
		return this.start.getDateTime().compareTo(interval.start().getDateTime());
	}

}
