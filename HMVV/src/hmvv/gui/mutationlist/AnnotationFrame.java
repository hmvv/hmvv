package hmvv.gui.mutationlist;

import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.ContextMenuMouseListener;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;
import hmvv.main.Configurations;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;

public class AnnotationFrame extends JDialog {
	private static final long serialVersionUID = 1L;

	private JButton okButton;
	private JButton cancelButton;
	private JButton previousGeneAnnotationButton;
	private JButton previousAnnotationButton;
	private JButton nextGeneAnnotationButton;
	private JButton nextAnnotationButton;
	private JButton draftButton;

	private JComboBox<String> pathogenicityComboBox;
	private JComboBox<String> mutationTypeComboBox;

	private JTextArea geneAnnotationTextArea;
	private JTextArea annotationTextArea;

	private JLabel historyLabelGeneAnnotation;
	private JLabel historyLabelAnnotation;

	private Boolean readOnly;
	private Integer currentGeneAnnotationIndex;
	private Integer currentAnnotationIndex;

	private ArrayList<GeneAnnotation> geneAnnotationHistory;
	private MutationCommon mutation;

	private final Color readOnlyColor = new Color(245,245,245);
	private final Color readWriteColor = Color.WHITE;

	/**
	 * Create the dialog.
	 * @throws Exception
	 */
	public AnnotationFrame(JDialog parent, MutationCommon mutation, ArrayList<GeneAnnotation> geneAnnotationHistory) throws Exception {
		super(parent, "Title Set Later", ModalityType.APPLICATION_MODAL);
		String title = "Annotation - " + mutation.getGene() + " - " + mutation.getCoordinate().getCoordinateAsString();
		setTitle(title);
		this.mutation = mutation;
		this.readOnly = !SSHConnection.isSuperUser(Configurations.USER_FUNCTION.ANNOTATE_MAIN);
		
		this.geneAnnotationHistory = geneAnnotationHistory;
		currentGeneAnnotationIndex = geneAnnotationHistory.size() - 1; 
		currentAnnotationIndex = mutation.getAnnotationHistorySize() - 1;
		
		createComponents();
		layoutComponents();
		activateComponents();
				
		if(readOnly){
			setTitle("Annotation (read only) - " + mutation.getGene() + " - " + mutation.getCoordinate().getCoordinateAsString());
			geneAnnotationTextArea.setEditable(false);
			annotationTextArea.setEditable(false);
			pathogenicityComboBox.setEnabled(false);
			mutationTypeComboBox.setEnabled(false);
			geneAnnotationTextArea.setBackground(new Color(234,240,240));
			annotationTextArea.setBackground(new Color(234,240,240));
		}
		
		pack();
		setResizable(true);
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.70), (int)(bounds.height*.70));
		setLocationRelativeTo(parent);
	}

	private void createComponents(){
		pathogenicityComboBox = new JComboBox<String>();
		pathogenicityComboBox.addItem("Not set");
		pathogenicityComboBox.addItem("Benign");
		pathogenicityComboBox.addItem("Likely Benign");
		pathogenicityComboBox.addItem("Unknown");
		pathogenicityComboBox.addItem("Likely Pathogenic");
		pathogenicityComboBox.addItem("Pathogenic");		
		
		mutationTypeComboBox = new JComboBox<String>();
		mutationTypeComboBox.addItem("Not set");
		mutationTypeComboBox.addItem("Somatic");
		mutationTypeComboBox.addItem("Germline");
		mutationTypeComboBox.addItem("Unknown");
		mutationTypeComboBox.addItem("Artifact");
		mutationTypeComboBox.addItem("Both Somatic and Germline");
		mutationTypeComboBox.addItem("Not Confirmed Somatic");
		
		previousGeneAnnotationButton = new JButton("Previous");
		if (geneAnnotationHistory.size() <= 1) { 	
			previousGeneAnnotationButton.setEnabled(false); 
		}

		previousAnnotationButton = new JButton("Previous");
		if (mutation.getAnnotationHistorySize() <= 1) { 
			previousAnnotationButton.setEnabled(false); 
		}		

		nextGeneAnnotationButton = new JButton("Next");
		nextGeneAnnotationButton.setEnabled(false);
		
		nextAnnotationButton = new JButton("Next");
		nextAnnotationButton.setEnabled(false);

		geneAnnotationTextArea = new JTextArea();
		geneAnnotationTextArea.setWrapStyleWord(true);
		geneAnnotationTextArea.setLineWrap(true);
		
		annotationTextArea = new JTextArea();
		annotationTextArea.setWrapStyleWord(true);
		annotationTextArea.setLineWrap(true);
		
		historyLabelGeneAnnotation = new JLabel("New Gene Annotation");
		historyLabelAnnotation = new JLabel("New Annotation");

		draftButton = new JButton("Variant Annotation Draft");
		draftButton.setEnabled(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.ANNOTATE_DRAFT));

		okButton = new JButton("OK");
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		
		setDefaultComponentValues();
	}
	
	private void setDefaultComponentValues() {
		Annotation defaultAnnotation = mutation.getLatestAnnotation();
		if(defaultAnnotation != null) {
			pathogenicityComboBox.setSelectedItem(defaultAnnotation.classification);
			mutationTypeComboBox.setSelectedItem(defaultAnnotation.somatic);
			annotationTextArea.setText(defaultAnnotation.curation);
			setCommonAnnotationLabel(defaultAnnotation, historyLabelAnnotation);
		}
		
		if(geneAnnotationHistory.size() > 0) {
			GeneAnnotation defaultGeneAnnotation = geneAnnotationHistory.get(geneAnnotationHistory.size() - 1);
			geneAnnotationTextArea.setText(defaultGeneAnnotation.curation);
			setCommonAnnotationLabel(defaultGeneAnnotation, historyLabelGeneAnnotation);
		}
	}
	
	private void setCommonAnnotationLabel(CommonAnnotation commonAnnotation, JLabel label) {
		label.setText(commonAnnotation.enteredBy + " [" + GUICommonTools.shortDateFormat.format(commonAnnotation.enterDate) + "]");
	}
	
	private void addItem(JPanel itemPanel, String label, Component content) {
		JLabel geneLabel = new JLabel(label);
		geneLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		itemPanel.add(geneLabel);
		itemPanel.add(content);
		itemPanel.add(new JLabel());//blank space
	}
	
	private void addItem(JPanel itemPanel, String label, String content) {
		JTextField textField = new JTextField(content);
		textField.setEditable(false);
		addItem(itemPanel, label, textField);
	}
	
	private void layoutComponents(){
		JPanel contentPanel = new JPanel();
		setContentPane(contentPanel);
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout());
		
		JPanel itemPanel = new JPanel();
		GridLayout itemPanelGridLayout = new GridLayout(0,1);
		itemPanelGridLayout.setVgap(1);
		itemPanel.setLayout(itemPanelGridLayout);
		
		addItem(itemPanel, "Gene", mutation.getGene());
		addItem(itemPanel, "Coordinate", mutation.getCoordinate().getCoordinateAsString());
		String HGVSc = (mutation.getHGVSc().startsWith("ENST")) ? mutation.getHGVSc().split(":")[1] : mutation.getHGVSc();
		addItem(itemPanel, "HGVSc", HGVSc);
		String HGVSp = (mutation.getHGVSp().startsWith("ENSP")) ? mutation.getHGVSp().split(":")[1] : mutation.getHGVSp();
		addItem(itemPanel, "HGVSp", HGVSp);
		addItem(itemPanel, "Classification", pathogenicityComboBox);
		addItem(itemPanel, "Origin", mutationTypeComboBox);
		
		Dimension textAreaDimension = new Dimension(300,550);
		
		JPanel textAreaPanel = new JPanel();
		textAreaPanel.setLayout(new GridLayout(1,0));
		//Annotation
		JPanel annotationPanel = new JPanel();
		annotationPanel.setLayout(new BoxLayout(annotationPanel, BoxLayout.Y_AXIS));
		JScrollPane annotationScrollPane = new JScrollPane(annotationTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		annotationScrollPane.setPreferredSize(textAreaDimension);
		TitledBorder annotationBorder = BorderFactory.createTitledBorder("Variant Annotation");
		annotationBorder.setTitleFont(GUICommonTools.TAHOMA_BOLD_14);
		annotationPanel.setBorder(annotationBorder);
		annotationPanel.add(annotationScrollPane);
		JPanel historyPanelA = new JPanel();
		historyPanelA.setLayout(new FlowLayout(FlowLayout.LEFT));
		historyPanelA.add(previousAnnotationButton);
		historyPanelA.add(historyLabelAnnotation);
		historyPanelA.add(nextAnnotationButton);
		annotationPanel.add(historyPanelA);
		textAreaPanel.add(annotationPanel);
		
		//GeneAnnotation
		JPanel geneAnnotationPanel = new JPanel();
		geneAnnotationPanel.setLayout(new BoxLayout(geneAnnotationPanel, BoxLayout.Y_AXIS));
		JScrollPane geneScrollPane = new JScrollPane(geneAnnotationTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		geneScrollPane.setPreferredSize(textAreaDimension);
		TitledBorder geneAnnotationBorder = BorderFactory.createTitledBorder("" + mutation.getGene() + " Annotation");
		geneAnnotationBorder.setTitleFont(GUICommonTools.TAHOMA_BOLD_14);
		geneAnnotationPanel.setBorder(geneAnnotationBorder);
		geneAnnotationPanel.add(geneScrollPane);
		JPanel historyPanelGA = new JPanel();
		historyPanelGA.setLayout(new FlowLayout(FlowLayout.LEFT));
		historyPanelGA.add(previousGeneAnnotationButton);
		historyPanelGA.add(historyLabelGeneAnnotation);
		historyPanelGA.add(nextGeneAnnotationButton);
		geneAnnotationPanel.add(historyPanelGA);
		textAreaPanel.add(geneAnnotationPanel);
		
		//JLabel lblMaxCharacters = new JLabel("Max " + maxCharacters + " characters");
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPane.add(draftButton);
		buttonPane.add(okButton);
		buttonPane.add(cancelButton);
		
		contentPanel.add(itemPanel, BorderLayout.WEST);
		contentPanel.add(textAreaPanel, BorderLayout.CENTER);
		contentPanel.add(buttonPane, BorderLayout.SOUTH);
	}

	private void activateComponents(){
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					AnnotationFrame.this.setVisible(false);
					saveRecord();
				}catch(Exception e){
					HMVVDefectReportFrame.showHMVVDefectReportFrame(AnnotationFrame.this, e);
				}
			}
		});

		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AnnotationFrame.this.dispose();
			}
		});

		draftButton.addActionListener(new ActionListener(){
			
			@Override
			
			public void actionPerformed(ActionEvent arg0) {
				try{
					
					showAnnotationDraftFrame();
					
				}catch(Exception e){
					HMVVDefectReportFrame.showHMVVDefectReportFrame(AnnotationFrame.this, e);
				}
			}
			
		});

		

		ActionListener historyActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(e.getSource() == previousGeneAnnotationButton) {
						showGeneAnnotationPrevious();
					}else if(e.getSource() == nextGeneAnnotationButton) {
						showGeneAnnotationNext();
					}else if(e.getSource() == previousAnnotationButton) {
						showAnnotationPrevious();
					}else if(e.getSource() == nextAnnotationButton) {
						showAnnotationNext();
					}
				} catch (Exception exception) {
					HMVVDefectReportFrame.showHMVVDefectReportFrame(AnnotationFrame.this, exception);
				}
			}
		};
		
		previousGeneAnnotationButton.addActionListener(historyActionListener);
		nextGeneAnnotationButton.addActionListener(historyActionListener);
		previousAnnotationButton.addActionListener(historyActionListener);
		nextAnnotationButton.addActionListener(historyActionListener);
		annotationTextArea.addMouseListener(new ContextMenuMouseListener());

		
	}

	private void saveRecord() throws Exception{
		if(readOnly){
			return;
		}
		
		if(!SSHConnection.isSuperUser(Configurations.USER_FUNCTION.ANNOTATE_MAIN)){
			//readOnly should prevent this, but just in case
			JOptionPane.showMessageDialog(this, "Only authorized user can edit annotation");
			return;
		}
		
		saveAnnotationRecord();
		saveGeneAnnotationRecord();
	}
	
	private void saveAnnotationRecord() throws Exception {
		if(currentAnnotationIndex == mutation.getAnnotationHistorySize() - 1 || mutation.getAnnotationHistorySize() == 0) {//only consider saving annotation if we are at the most recent one, or there never has been an annotation
			//annotation update
			Annotation newAnnotation = new Annotation(
					mutation.getCoordinate(),
					pathogenicityComboBox.getSelectedItem().toString(),
					annotationTextArea.getText(),
					mutationTypeComboBox.getSelectedItem().toString(),
					SSHConnection.getUserName(),
					Calendar.getInstance().getTime()
					);
			Annotation latestAnnotation = mutation.getLatestAnnotation();
			if (latestAnnotation == null || !latestAnnotation.equals(newAnnotation)) {
				DatabaseCommands.addVariantAnnotationCuration(newAnnotation,Configurations.MUTATION_TYPE.GERMLINE);
				mutation.addAnnotation(newAnnotation);
//				parent.notifyAnnotationUpdated(newAnnotation);
			}
		}
	}
	
	private void saveGeneAnnotationRecord() throws Exception {
		if(currentGeneAnnotationIndex == geneAnnotationHistory.size() - 1 || geneAnnotationHistory.isEmpty()) {//only consider saving annotation if we are at the most recent one, or there never has been an annotation
			//gene annotation update
			GeneAnnotation newGeneAnnotation = new GeneAnnotation(
					mutation.getGene(),
					geneAnnotationTextArea.getText(),
					SSHConnection.getUserName(),
					Calendar.getInstance().getTime()
					);
			GeneAnnotation latestGeneAnnotation = (geneAnnotationHistory.isEmpty()) ? null : geneAnnotationHistory.get(geneAnnotationHistory.size() - 1);		
			if(latestGeneAnnotation == null || !latestGeneAnnotation.equals(newGeneAnnotation)) {
				DatabaseCommands.addGeneAnnotationCuration(newGeneAnnotation,Configurations.MUTATION_TYPE.GERMLINE);
				geneAnnotationHistory.add(newGeneAnnotation);//may not be necessary as no GUI object currently stores this list
			}
		}
	}
    
	private void showGeneAnnotationPrevious(){
		currentGeneAnnotationIndex = currentGeneAnnotationIndex - 1;
		updateGeneAnnotation();
	}
	
	private void showGeneAnnotationNext(){
		currentGeneAnnotationIndex = currentGeneAnnotationIndex + 1;
		updateGeneAnnotation();	
    }
	
	private void updateGeneAnnotation() {
		GeneAnnotation currentGeneAnnotation = geneAnnotationHistory.get(currentGeneAnnotationIndex);
		geneAnnotationTextArea.setText(currentGeneAnnotation.curation);
		setCommonAnnotationLabel(currentGeneAnnotation, historyLabelGeneAnnotation);
		
		updateCommon(currentGeneAnnotationIndex, geneAnnotationHistory.size(), geneAnnotationTextArea, previousGeneAnnotationButton, nextGeneAnnotationButton);
	}
	
	private void showAnnotationPrevious(){
		currentAnnotationIndex = currentAnnotationIndex - 1;
		updateAnnotation();
	}

	private void showAnnotationNext(){
		currentAnnotationIndex = currentAnnotationIndex + 1;
		updateAnnotation();
	}
	
	private void updateAnnotation() {
		Annotation currentannotation = mutation.getAnnotation(currentAnnotationIndex);
		annotationTextArea.setText(currentannotation.curation);
		pathogenicityComboBox.setSelectedItem(currentannotation.classification);
		mutationTypeComboBox.setSelectedItem(currentannotation.somatic);
		setCommonAnnotationLabel(currentannotation, historyLabelAnnotation);
		
		updateCommon(currentAnnotationIndex, mutation.getAnnotationHistorySize(), annotationTextArea, previousAnnotationButton, nextAnnotationButton);
		
		if (currentAnnotationIndex == mutation.getAnnotationHistorySize() - 1) {
			if(!readOnly) {
				pathogenicityComboBox.setEnabled(true);
				mutationTypeComboBox.setEnabled(true);
			}
		}else if(mutation.getAnnotationHistorySize() != 1) {
			pathogenicityComboBox.setEnabled(false);
			mutationTypeComboBox.setEnabled(false);
		}
	}
	
	private void updateCommon(int currentIndex, int historySize, JTextArea textArea, JButton previousButton, JButton nextButton) {		
		//default to read only, and switch below if we are at the most present annotation
		textArea.setBackground(readOnlyColor);
		textArea.setEditable(false);
		
		//update previousAnnotationButton
		if (currentIndex == 0) {
			previousButton.setEnabled(false);		
		}else if(historySize > 1) {
			previousButton.setEnabled(true);
		}
		
		//update nextAnnotationButton
		if ( currentIndex == historySize - 1) {
			//we are at the end of the gene annotation history
			nextButton.setEnabled(false);
			if(!readOnly) {
				textArea.setEditable(true);		
				textArea.setBackground(readWriteColor);
			}
		}else if(historySize != 1) {
			nextButton.setEnabled(true);
		}
	}

	private void showAnnotationDraftFrame(){
		AnnotationDraftFrame annotationdraftframe = new AnnotationDraftFrame(this, mutation);	
		annotationdraftframe.setVisible(true);
	}	
}
