package charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.ta4j.core.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import ta4jexamples.loaders.CsvTradesLoader;
import ta4jexamples.strategies.MovingMomentumStrategy;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BuySellChart {

	private static org.jfree.data.time.TimeSeries buildChartTimeSeries(TimeSeries barseries, Indicator<Num> indicator, String name) {
		org.jfree.data.time.TimeSeries chartTimeSeries = new org.jfree.data.time.TimeSeries(name);
		for (int i = 0; i < barseries.getBarCount(); i++) {
			Bar bar = barseries.getBar(i);
			chartTimeSeries.add(new Minute(Date.from(bar.getEndTime()
					.toInstant())), indicator.getValue(i)
					.doubleValue());
		}
		return chartTimeSeries;
	}

	private static void addBuySellSignals(TimeSeries series, Strategy strategy, XYPlot plot) {
		// Running the strategy
		TimeSeriesManager seriesManager = new TimeSeriesManager(series);
		List<Trade> trades = seriesManager.run(strategy)
				.getTrades();
		// Adding markers to plot
		for (Trade trade : trades) {
			// Buy signal
			double buySignalBarTime = new Minute(Date.from(series.getBar(trade.getEntry()
					.getIndex())
					.getEndTime()
					.toInstant())).getFirstMillisecond();
			Marker buyMarker = new ValueMarker(buySignalBarTime);
			buyMarker.setPaint(Color.GREEN);
			buyMarker.setLabel("B");
			plot.addDomainMarker(buyMarker);
			// Sell signal
			double sellSignalBarTime = new Minute(Date.from(series.getBar(trade.getExit()
					.getIndex())
					.getEndTime()
					.toInstant())).getFirstMillisecond();
			Marker sellMarker = new ValueMarker(sellSignalBarTime);
			sellMarker.setPaint(Color.RED);
			sellMarker.setLabel("S");
			plot.addDomainMarker(sellMarker);
		}
	}

	/**
	 * Displays a chart in a frame.
	 *
	 * @param chart the chart to be displayed
	 */
	private static void displayChart(JFreeChart chart) {
		// Chart panel
		ChartPanel panel = new ChartPanel(chart);
		panel.setFillZoomRectangle(true);
		panel.setMouseWheelEnabled(true);
		panel.setPreferredSize(new Dimension(1024, 400));
		// Application frame
		ApplicationFrame frame = new ApplicationFrame("First Chart");
		frame.setContentPane(panel);
		frame.pack();
		RefineryUtilities.centerFrameOnScreen(frame);
		frame.setVisible(true);
	}

	public static void main(String[] args) {

		// Getting the time series
		TimeSeries series = CsvTradesLoader.loadBitstampSeries();
		// Building the trading strategy
		Strategy strategy = MovingMomentumStrategy.buildStrategy(series);

        /*
          Building chart datasets
         */
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(buildChartTimeSeries(series, new ClosePriceIndicator(series), "Bitstamp Bitcoin (BTC)"));

        /*
          Creating the chart
         */
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"Test Buy Sell Signals", // title
				"Date", // x-axis label
				"Price", // y-axis label
				dataset, // data
				true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
		);
		XYPlot plot = (XYPlot) chart.getPlot();
		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("MM-dd HH:mm"));

        /*
          Running the strategy and adding the buy and sell signals to plot
         */
		addBuySellSignals(series, strategy, plot);

        /*
          Displaying the chart
         */
		displayChart(chart);
	}

}