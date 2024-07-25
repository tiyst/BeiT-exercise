package st.tiy.beitexercise.model;

import java.util.List;

public record ChartData(List<MeasurementTimestamp> measurements,
                        List<ConsumptionInterval> consumptions,
                        TimeUnit timeUnit) {

}
