package hmvv.gui.sampleList;

import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import hmvv.gui.GUICommonTools;
import hmvv.io.DatabaseCommands;
import hmvv.model.Amplicon;

public class QualityControlFrame {
	public static void showQCChart(SampleListFrame parent) throws Exception {
		ArrayList<Amplicon> amplicons = DatabaseCommands.getAmpliconQCData();
		
		SummaryStatistics ss = new SummaryStatistics();
	    
	    
		double[] xData = new double[amplicons.size()];
		double[] yData = new double[amplicons.size()];
		
		for(int i = 0; i < xData.length; i++) {
			xData[i] = i + 1;
			try {
				yData[i] = Double.parseDouble(amplicons.get(i).readDepth);
				ss.addValue(yData[i]);
			}catch(Exception e) {
				e.printStackTrace();//TODO figure out what to do here
				yData[i] = -1;
			}
		}
		
		double mean = ss.getMean();
		double stddev = ss.getStandardDeviation();
		
		double[] meanData = new double[amplicons.size()];
		double[] SD_P1 = new double[amplicons.size()];
		double[] SD_P2 = new double[amplicons.size()];
		double[] SD_N1 = new double[amplicons.size()];
		double[] SD_N2 = new double[amplicons.size()];
		
		for(int i = 0; i < xData.length; i++) {
			meanData[i] = mean;
			SD_P1[i] = mean + stddev;
			SD_P2[i] = mean + 2*stddev;
			SD_N1[i] = mean - stddev;
			SD_N2[i] = mean - 2*stddev;
		}
		
	    XYChart chart = QuickChart.getChart("Coverage depth over time", "Sample Run", "Coverage Depth", "coverage depth", xData, yData);
	    chart.addSeries(String.format("Mean (%s)", (int)mean), meanData);
	    chart.addSeries("1SD", SD_P1);
	    chart.addSeries("2SD", SD_P2);
	    chart.addSeries("-1SD", SD_N1);
	    chart.addSeries("-2SD", SD_N2);
	    
	    new Thread(new Runnable() {
			@Override
			public void run() {
				SwingWrapper<XYChart> sw = new SwingWrapper<XYChart>(chart);
				JFrame swFrame = sw.displayChart();
				swFrame.setTitle("Levy-Jennings Chart");
				swFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				Rectangle bounds = GUICommonTools.getBounds(parent);
				swFrame.setSize((int)(bounds.width*.5), (int)(bounds.height*.5));
				swFrame.setLocationRelativeTo(parent);
			}
	    }).start();
	}
}
