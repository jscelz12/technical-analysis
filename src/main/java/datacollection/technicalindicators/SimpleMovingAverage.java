package datacollection.technicalindicators;

import java.util.List;
import java.util.Map;

import org.patriques.AlphaVantageConnector;
import org.patriques.TechnicalIndicators;
import org.patriques.input.technicalindicators.Interval;
import org.patriques.input.technicalindicators.SeriesType;
import org.patriques.input.technicalindicators.TimePeriod;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.technicalindicators.SMA;
import org.patriques.output.technicalindicators.data.IndicatorData;

public class SimpleMovingAverage {

	public static void main(String[] args) {
		String apiKey = "50M3AP1K3Y";
		int timeout = 3000;
		AlphaVantageConnector apiConnector = new AlphaVantageConnector(apiKey, timeout);
		TechnicalIndicators technicalIndicators = new TechnicalIndicators(apiConnector);

		try {
			SMA response = technicalIndicators.sma("AAPL", Interval.DAILY, TimePeriod.of(5283), SeriesType.CLOSE);
			Map<String, String> metaData = response.getMetaData();
			System.out.println("Symbol: " + metaData.get("1: Symbol"));
			System.out.println("Indicator: " + metaData.get("2: Indicator"));

			List<IndicatorData> macdData = response.getData();
			macdData.forEach(data -> {
				System.out.println("date:           " + data.getDateTime());
				System.out.println("Simple Moving Avg: " + data.getData());
			});
		} catch (AlphaVantageException e) {
			System.out.println("something went wrong");
		}
	}
}
