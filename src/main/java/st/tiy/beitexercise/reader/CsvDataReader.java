package st.tiy.beitexercise.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import st.tiy.beitexercise.model.request.RequestMeasurement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("csv")
public class CsvDataReader implements DataReader {

	public static final String COMMA = ",";

	private final Logger logger = LoggerFactory.getLogger(CsvDataReader.class);

	@Override
	public List<RequestMeasurement> parseDataFromFile(String fileName) {
		File file = new File(fileName);
		List<RequestMeasurement> parsedMeasurements = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(COMMA);
				if (values.length != 2) {
					throw new IOException("Unexpected line format: " + line);
				}
				double parsedValue = Double.parseDouble(values[1]);

				parsedMeasurements.add(new RequestMeasurement(values[0], parsedValue));
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		return parsedMeasurements;
	}

}
