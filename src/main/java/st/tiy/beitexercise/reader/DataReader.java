package st.tiy.beitexercise.reader;

import st.tiy.beitexercise.model.request.RequestMeasurement;

import java.util.List;

public interface DataReader {

	List<RequestMeasurement> parseDataFromFile(String fileName);

}
