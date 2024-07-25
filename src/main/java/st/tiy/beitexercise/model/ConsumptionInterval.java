package st.tiy.beitexercise.model;

import java.math.BigDecimal;

public record ConsumptionInterval(Interval consumptionInterval,
                                  BigDecimal consumption) implements Comparable<ConsumptionInterval> {

	@Override
	public int compareTo(ConsumptionInterval interval) {
		return this.consumptionInterval.compareTo(interval.consumptionInterval);
	}

}
