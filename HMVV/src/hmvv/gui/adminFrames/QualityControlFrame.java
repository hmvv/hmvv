package hmvv.gui.adminFrames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

//import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;

import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.model.QCTrend;
import hmvv.model.GeneQCDataElementTrend;

public class QualityControlFrame extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TreeMap<String, GeneQCDataElementTrend> qualityControlTrends;
	private String chartTitle;
	private String xAxis;
	private String yAxis;
	
	private XYChart chart;
	private XChartPanel<XYChart> panel;
	private JPanel checkboxPanel;
	private HashMap<String, Color> seriesColors;
	
	public QualityControlFrame(SampleListFrame parent, TreeMap<String, GeneQCDataElementTrend> qualityControlTrends, String jframeTitle, String chartTitle, String xAxis, String yAxis) {
		super(parent, "HMVV QC Chart - " + jframeTitle);
		this.qualityControlTrends = qualityControlTrends;
		this.chartTitle = chartTitle;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		seriesColors = new HashMap<String, Color>();
		
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.6), (int)(bounds.height*.5));

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		createComponents();
		layoutComponents();
		setLocationRelativeTo(parent);
	}
	
	private void createComponents() {
		chart = new XYChartBuilder().title(chartTitle).xAxisTitle(xAxis).yAxisTitle(yAxis).build();
		panel = new XChartPanel<XYChart>(chart);
		checkboxPanel = new JPanel();
		
		for(String gene : qualityControlTrends.keySet()) {
			GeneQCDataElementTrend geneQualityControlTrend = qualityControlTrends.get(gene);
			
			for(String qualityControlTrendName : geneQualityControlTrend.getQualityControlTrends().keySet()) {
				QCTrend qualityControlTren = geneQualityControlTrend.getQualityControlTrends().get(qualityControlTrendName);
				
				int[] xData = qualityControlTren.getSampleIDs();
				int[] yData = qualityControlTren.getReadDepths();
				
				String seriesName = gene + ":" + qualityControlTrendName;
				JCheckBox seriesCheckBox = new JCheckBox(seriesName);
				seriesCheckBox.setSelected(true);
				addItemListener(seriesCheckBox, seriesName, xData, yData);
				checkboxPanel.add(seriesCheckBox);
				chart.addSeries(seriesName, xData, yData);
			}
		}
	}
	
	private void layoutComponents() {
		checkboxPanel.setLayout(new GridLayout(0,1));
		
		JPanel theMainPanel = new JPanel();
  		theMainPanel.setLayout(new BorderLayout());
  		theMainPanel.add(panel, BorderLayout.CENTER);
  		theMainPanel.add(checkboxPanel, BorderLayout.SOUTH);
  		add(theMainPanel);
	}
	
	private void addItemListener(JCheckBox seriesCheckBox, String seriesName, int[] xData, int[] yData) {
		seriesCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(seriesCheckBox.isSelected()) {
					XYSeries thisSeries = chart.addSeries(seriesName, xData, yData);
					Color color = seriesColors.get(seriesName);
					thisSeries.setLineColor(color);
					thisSeries.setFillColor(color);
					thisSeries.setMarkerColor(color);
				}else {
					XYSeries thisSeries = chart.removeSeries(seriesName);
					//The chart does not natively remember which color to use, so we store it here
					Color color = thisSeries.getLineColor();
					seriesColors.put(seriesName, color);
				}
				panel.repaint();
			}
		});
	}
}