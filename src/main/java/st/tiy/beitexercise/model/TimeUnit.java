package st.tiy.beitexercise.model;

import java.time.temporal.ChronoUnit;

public enum TimeUnit {
	MONTH(ChronoUnit.MONTHS),
	DAY(ChronoUnit.DAYS),
	HOUR(ChronoUnit.HOURS);

	public final ChronoUnit chronoUnit;

	TimeUnit(ChronoUnit chronoUnit) {
		this.chronoUnit = chronoUnit;
	}
}
