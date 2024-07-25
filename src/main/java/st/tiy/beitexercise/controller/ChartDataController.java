package st.tiy.beitexercise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import st.tiy.beitexercise.model.ChartData;
import st.tiy.beitexercise.model.TimeUnit;
import st.tiy.beitexercise.service.DataRetrievalService;

import java.time.OffsetDateTime;

import static st.tiy.beitexercise.service.MeasurementProcessingService.PRAGUE_OFFSET;

@RestController
@RequestMapping("/api/data")
public class ChartDataController {

	private final DataRetrievalService dataRetrievalService;

	public ChartDataController(DataRetrievalService dataRetrievalService) {
		this.dataRetrievalService = dataRetrievalService;
	}

	@GetMapping
	public ChartData getChartData(@RequestParam OffsetDateTime from,
	                              @RequestParam OffsetDateTime to,
	                              @RequestParam(required = false, defaultValue = "HOUR") TimeUnit granularity) {
		from = from.withOffsetSameInstant(PRAGUE_OFFSET);
		to = to.withOffsetSameInstant(PRAGUE_OFFSET);

		return this.dataRetrievalService.getChartData(from, to, granularity);
	}


}
