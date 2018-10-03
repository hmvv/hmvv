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
import hmvv.model.QCTrend;
import hmvv.model.GeneQCDataElementTrend;

public class QualityControlFrame {
	public static void showQCChart(SampleListFrame parent, TreeMap<String, GeneQCDataElementTrend> qualityControlTrends, String assay, String title, String xAxis, String yAxis){
		
		
		XYChart chart = new XYChartBuilder().title(title).xAxisTitle(xAxis).yAxisTitle(yAxis).build();
	    
		for(String gene : qualityControlTrends.keySet()) {
			GeneQCDataElementTrend geneQualityControlTrend = qualityControlTrends.get(gene);
			
			for(String qualityControlTrendName : geneQualityControlTrend.getQualityControlTrends().keySet()) {
				QCTrend qualityControlTren = geneQualityControlTrend.getQualityControlTrends().get(qualityControlTrendName);
				
				int[] xData = qualityControlTren.getSampleIDs();
				int[] yData = qualityControlTren.getReadDepths();
				
				String seriesName = gene + ":" + qualityControlTrendName;
				chart.addSeries(seriesName, xData, yData);
			}
		}
		
		final XYChart finalChart = chart;
		new Thread(new Runnable() {
			@Override
			public void run() {
				SwingWrapper<XYChart> sw = new SwingWrapper<XYChart>(finalChart);
				JFrame swFrame = sw.displayChart();
				swFrame.setTitle("HMVV QC Chart - " + assay);
				swFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				Rectangle bounds = GUICommonTools.getBounds(parent);
				swFrame.setSize((int)(bounds.width*.5), (int)(bounds.height*.5));
				swFrame.setLocationRelativeTo(parent);
			}
		}).start();
	}
}
