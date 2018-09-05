package hmvv.gui.adminFrames;

import java.awt.Rectangle;
import java.util.TreeMap;

import javax.swing.JFrame;

//import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.io.DatabaseCommands;
import hmvv.model.AmpliconTrend;
import hmvv.model.GeneAmpliconTrend;

public class QualityControlFrame {
	public static void showQCChart(SampleListFrame parent, String assay) throws Exception {
		TreeMap<String, GeneAmpliconTrend> ampliconTrends = DatabaseCommands.getAmpliconQCData(assay);
		
		XYChart chart = new XYChartBuilder().title("Coverage depth over time").xAxisTitle("Sample ID").yAxisTitle("Coverage Depth").build();
	    
		for(String gene : ampliconTrends.keySet()) {
			GeneAmpliconTrend geneAmpliconTrend = ampliconTrends.get(gene);
			
			for(String ampliconName : geneAmpliconTrend.getAmpliconTrends().keySet()) {
				AmpliconTrend ampliconTrend = geneAmpliconTrend.getAmpliconTrends().get(ampliconName);
				
				int[] xData = ampliconTrend.getSampleIDs();
				int[] yData = ampliconTrend.getReadDepths();

//				double[] xData = new double[amplicons.size()];
//				double[] yData = new double[amplicons.size()];

//				SummaryStatistics ss = new SummaryStatistics();
//				for(int i = 0; i < xData.length; i++) {
//					xData[i] = i + 1;
//					try {
//						yData[i] = (double)(amplicons.get(i).readDepth);
//						ss.addValue(yData[i]);
//					}catch(Exception e) {
//						yData[i] = -1;
//					}
//				}
//
//				double mean = ss.getMean();
//				double stddev = ss.getStandardDeviation();
//
//				double[] meanData = new double[amplicons.size()];
//				double[] SD_P1 = new double[amplicons.size()];
//				double[] SD_P2 = new double[amplicons.size()];
//				double[] SD_N1 = new double[amplicons.size()];
//				double[] SD_N2 = new double[amplicons.size()];
//
//				for(int i = 0; i < xData.length; i++) {
//					meanData[i] = mean;
//					SD_P1[i] = mean + stddev;
//					SD_P2[i] = mean + 2*stddev;
//					SD_N1[i] = mean - stddev;
//					SD_N2[i] = mean - 2*stddev;
//				}

				String seriesName = gene + ":" + ampliconName;
				chart.addSeries(seriesName, xData, yData);
			}
		}
		
		final XYChart finalChart = chart;
		new Thread(new Runnable() {
			@Override
			public void run() {
				SwingWrapper<XYChart> sw = new SwingWrapper<XYChart>(finalChart);
				JFrame swFrame = sw.displayChart();
				swFrame.setTitle("HMVV Coverage Depth Chart - " + assay);
				swFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				Rectangle bounds = GUICommonTools.getBounds(parent);
				swFrame.setSize((int)(bounds.width*.5), (int)(bounds.height*.5));
				swFrame.setLocationRelativeTo(parent);
			}
		}).start();
	}
}
