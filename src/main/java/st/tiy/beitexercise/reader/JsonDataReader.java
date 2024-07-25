package st.tiy.beitexercise.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import st.tiy.beitexercise.model.request.RequestMeasurement;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service("json")
public class JsonDataReader implements DataReader {

	private final Logger logger = LoggerFactory.getLogger(JsonDataReader.class);

	private ObjectMapper mapper;

	public JsonDataReader() {
		this.mapper = new ObjectMapper();
	}

	@Override
	public List<RequestMeasurement> parseDataFromFile(String fileName) {
		try(InputStream inputStream = new FileInputStream("data.json")) {
			RequestMeasurement[] requestMeasurements = this.mapper.readValue(inputStream, RequestMeasurement[].class);

			return new ArrayList<>(Arrays.asList(requestMeasurements));
		} catch (FileNotFoundException e) {
			logger.warn("File {} not found", fileName);
			return Collections.emptyList();
		} catch (IOException e) {
			logger.warn("Error while reading file {}", fileName);
			return Collections.emptyList();
		}
	}
}
