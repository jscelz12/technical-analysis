package datacollection.volume;

import java.util.List;
import java.util.Map;

import org.patriques.AlphaVantageConnector;
import org.patriques.TimeSeries;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.timeseries.Daily;
import org.patriques.output.timeseries.data.StockData;

public class RetrieveStocksWithHighestVolume {
	public static void main(String[] args) {
		String apiKey = "ERWJH2Q6550HFABI";
		int timeout = 3000;
		AlphaVantageConnector apiConnector = new AlphaVantageConnector(apiKey, timeout);
		TimeSeries stockTimeSeries = new TimeSeries(apiConnector);

		try {
			Daily response = stockTimeSeries.daily("AAPL", OutputSize.COMPACT);

			Map<String, String> metaData = response.getMetaData();
			System.out.println("Information: " + metaData.get("1. Information"));
			System.out.println("Stock: " + metaData.get("2. Symbol"));

			List<StockData> stockData = response.getStockData();
			List<Long> volumes;
		} catch (AlphaVantageException e) {
			System.out.println("something went wrong");
		}
	}
}
