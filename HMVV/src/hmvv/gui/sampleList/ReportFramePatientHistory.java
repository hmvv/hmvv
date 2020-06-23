package hmvv.gui.sampleList;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.io.LIS.LISConnection;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.main.HMVVFrame;
import hmvv.model.PatientHistory;
import hmvv.model.Sample;
import jdk.nashorn.internal.scripts.JD;

public class ReportFramePatientHistory extends JPanel{
	private static final long serialVersionUID = 1L;

	private ArrayList<PatientHistory> patientHistory;
	private String labOrderNumber;
	private JDialog parent;
	private JTree tree;
	private TreeSet<String> distinctOrderNumbers = new TreeSet<String>();
	private TreeSet<String> distinctTechnologies = new TreeSet<String>();
	private DefaultMutableTreeNode rootNode;
	private JTextArea interpretation;
	
	public ReportFramePatientHistory(JDialog parent, Sample sample, String labOrderNumber, ArrayList<PatientHistory> patientHistory) {
		this.parent = parent;
		this.labOrderNumber = labOrderNumber;
		this.patientHistory = patientHistory;
		for(PatientHistory ph : patientHistory) {
			distinctOrderNumbers.add(ph.orderNumber);
			distinctTechnologies.add(ph.orderNumber.substring(0,3));
		}
		rootNode = new DefaultMutableTreeNode(sample.getLastName() + ", " + sample.getFirstName());

		constructComponents();
		layoutComponents();
		activateComponents();
	}

	private void constructComponents() {
		tree = new JTree(rootNode);
		interpretation = new JTextArea();
		interpretation.setEditable(false);
		interpretation.setWrapStyleWord(true);
		
		createNodes();
		createLegacyButton();
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}

	private void layoutComponents() {
		setLayout(new BorderLayout());
		add(tree, BorderLayout.WEST);
		expandAllNodes(tree, 0, tree.getRowCount());
		
		interpretation.setBackground(GUICommonTools.LIGHT_GRAY);
		add(interpretation, BorderLayout.CENTER);
	}

	private void activateComponents() {
		tree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (node == null) {   
					return;
				}
				
				if (node.isLeaf()) {
					Object nodeInfo = node.getUserObject();
					PatientHistory patientHistory = (PatientHistory)nodeInfo;
					interpretation.setText(patientHistory.interpretation);
				}
			}
		});
	}

	private void createNodes() {
		for(String technology : distinctTechnologies) {
			DefaultMutableTreeNode tech = new DefaultMutableTreeNode(technology);
			rootNode.add(tech);
			
			for(String orderNumber : distinctOrderNumbers) {
				if(orderNumber.startsWith(technology)) {
					DefaultMutableTreeNode order = new DefaultMutableTreeNode(orderNumber);
					tech.add(order);
	
					for(PatientHistory ph : patientHistory) {
						if(ph.orderNumber.equals(orderNumber)) {
							DefaultMutableTreeNode report = new DefaultMutableTreeNode(ph);
							order.add(report);
						}
					}
				}
			}
		}
	}

	private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
	    for(int i = startingIndex; i<rowCount; ++i){
	        tree.expandRow(i);
	    }

	    if(tree.getRowCount()!=rowCount){
	        expandAllNodes(tree, rowCount, tree.getRowCount());
	    }
	}	

	private void createLegacyButton() {
		JButton getLegacyDataButton = new JButton("Load legacy LIS data");
		getLegacyDataButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
					@Override
					public Boolean doInBackground() {
						try {
							getLegacyDataButton.setEnabled(false);
							getLegacyDataButton.setText("Loading...");
							ArrayList<PatientHistory> history = LISConnection.getLegacyPatientHistory(labOrderNumber);
							if(history.size() > 0) {
								patientHistory.addAll(history);
							}
						} catch (SQLException e) {
							HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e);
							return false;
						}
						return true;
					}

					@Override
					public void done() {
						getLegacyDataButton.setText("Loaded");
					}
				};
				worker.execute();
			}
		});
	}
}
