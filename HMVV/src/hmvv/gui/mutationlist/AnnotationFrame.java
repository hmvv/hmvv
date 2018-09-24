package hmvv.gui.mutationlist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tables.CommonTable;
import hmvv.gui.sampleList.ContextMenuMouseListener;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;
import hmvv.model.Annotation;
import hmvv.model.CommonAnnotation;
import hmvv.model.GeneAnnotation;
import hmvv.model.Mutation;

public class AnnotationFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private CommonTable parent;
	
	private JButton okButton;
	private JButton cancelButton;
	private JButton previousGeneAnnotationButton;
	private JButton previousAnnotationButton;
	private JButton nextGeneAnnotationButton;
	private JButton nextAnnotationButton;
	
	private JComboBox<String> pathogenicityComboBox;
	private JComboBox<String> mutationTypeComboBox;
	
	private JTextArea geneAnnotationTextArea;
	private JTextArea annotationTextArea;
	private int maxCharacters = 5000;
	private DefaultStyledDocument defaultStyledDocument;
	
	private JLabel historyLabelGeneAnnotation;
	private JLabel historyLabelAnnotation;
	
	private Boolean readOnly;
	private Integer currentGeneAnnotationIndex;
	private Integer currentAnnotationIndex;
	
	private ArrayList<GeneAnnotation> geneAnnotationHistory;
	private Mutation mutation;
	
	private final Color readOnlyColor = new Color(245,245,245);
	private final Color readWriteColor = Color.WHITE;
	
	/**
	 * Create the dialog.
	 * @throws Exception 
	 */
	public AnnotationFrame(Boolean readOnly, Mutation mutation, ArrayList<GeneAnnotation> geneAnnotationHistory, CommonTable parent, MutationListFrame mutationListFrame) throws Exception {
		super("Annotation - " + mutation.getGene() + " - " + mutation.getCoordinate().getCoordinateAsString());
		this.mutation = mutation;
		this.readOnly = readOnly;
		this.parent = parent;
		
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
		setResizable(false);
		setLocationRelativeTo(mutationListFrame);
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
		
		okButton = new JButton("OK");
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");

		defaultStyledDocument = new DefaultStyledDocument();
		
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
		label.setText("Entered By: " + commonAnnotation.enteredBy + "   Date: " + GUICommonTools.extendedDateFormat2.format(commonAnnotation.enterDate));
	}
	
	private void layoutComponents(){
		JPanel contentPanel = new JPanel();
		setContentPane(contentPanel);
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout());
		
		JPanel itemPanel = new JPanel();
		GridLayout itemPanelGridLayout = new GridLayout(0,1);
		itemPanelGridLayout.setVgap(45);
		itemPanel.setLayout(itemPanelGridLayout);
		
		//blank space
		itemPanel.add(new JLabel());
		
		//Gene
		JLabel geneLabel = new JLabel("Gene");
		geneLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		JPanel genePanel = new JPanel();
		genePanel.setLayout(new GridLayout(1,0));
		genePanel.add(geneLabel);
		JLabel geneLabelText = new JLabel(mutation.getGene());
		geneLabelText.setFont(GUICommonTools.TAHOMA_BOLD_14);
		genePanel.add(geneLabelText);
		itemPanel.add(genePanel);
				
		//Coordinate
		JLabel lblCoordinate = new JLabel("Coordinate");
		lblCoordinate.setFont(GUICommonTools.TAHOMA_BOLD_14);
		JPanel coordinatePanel = new JPanel();
		coordinatePanel.setLayout(new GridLayout(1,0));
		coordinatePanel.add(lblCoordinate);
		JLabel lblCoordinateText = new JLabel(mutation.getCoordinate().getCoordinateAsString());
		lblCoordinateText.setFont(GUICommonTools.TAHOMA_BOLD_14);
		coordinatePanel.add(lblCoordinateText);
		itemPanel.add(coordinatePanel);
		
		//Classification
		JLabel lblClassification = new JLabel("Classification");
		lblClassification.setFont(GUICommonTools.TAHOMA_BOLD_14);
		JPanel classificationPanel = new JPanel();
		classificationPanel.setLayout(new GridLayout(1,0));
		classificationPanel.add(lblClassification);
		classificationPanel.add(pathogenicityComboBox);
		itemPanel.add(classificationPanel);
		
		//Somatic
		JLabel lblSomatic = new JLabel("Somatic");
		lblSomatic.setFont(GUICommonTools.TAHOMA_BOLD_14);
		JPanel somaticPanel = new JPanel();
		somaticPanel.setLayout(new GridLayout(1,0));
		somaticPanel.add(lblSomatic);
		somaticPanel.add(mutationTypeComboBox);
		itemPanel.add(somaticPanel);
		
		//blank space
		itemPanel.add(new JLabel());
		itemPanel.add(new JLabel());
		
		Dimension textAreaDimension = new Dimension(400,400);
		
		JPanel textAreaPanel = new JPanel();
		//Annotation
		JPanel annotationPanel = new JPanel();
		annotationPanel.setLayout(new BoxLayout(annotationPanel, BoxLayout.Y_AXIS));
		JScrollPane annotationScrollPane = new JScrollPane(annotationTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		annotationScrollPane.setPreferredSize(textAreaDimension);
		TitledBorder annotationBorder = BorderFactory.createTitledBorder("Variant Annotation (5000 characters max)");
		annotationBorder.setTitleFont(GUICommonTools.TAHOMA_BOLD_14);
		annotationPanel.setBorder(annotationBorder);
		annotationPanel.add(annotationScrollPane);
		//annotationPanel.add(annotationTextArea);
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
		TitledBorder geneAnnotationBorder = BorderFactory.createTitledBorder("" + mutation.getGene() + " Annotation (5000 characters max)");
		geneAnnotationBorder.setTitleFont(GUICommonTools.TAHOMA_BOLD_14);
		geneAnnotationPanel.setBorder(geneAnnotationBorder);
		geneAnnotationPanel.add(geneScrollPane);
		//geneAnnotationPanel.add(geneAnnotationTextArea);
		JPanel historyPanelGA = new JPanel();
		historyPanelGA.setLayout(new FlowLayout(FlowLayout.LEFT));
		historyPanelGA.add(previousGeneAnnotationButton);
		historyPanelGA.add(historyLabelGeneAnnotation);
		historyPanelGA.add(nextGeneAnnotationButton);
		geneAnnotationPanel.add(historyPanelGA);
		textAreaPanel.add(geneAnnotationPanel);
		
		//JLabel lblMaxCharacters = new JLabel("Max " + maxCharacters + " characters");
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
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
					JOptionPane.showMessageDialog(AnnotationFrame.this, e.getMessage());
				}
			}
		});

		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AnnotationFrame.this.dispose();
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
					JOptionPane.showMessageDialog(AnnotationFrame.this, exception.getMessage());
				}
			}
		};
		
		previousGeneAnnotationButton.addActionListener(historyActionListener);
		nextGeneAnnotationButton.addActionListener(historyActionListener);
		previousAnnotationButton.addActionListener(historyActionListener);
		nextAnnotationButton.addActionListener(historyActionListener);

		defaultStyledDocument.setDocumentFilter(new DocumentSizeFilter());
		annotationTextArea.addMouseListener(new ContextMenuMouseListener());
	}

	private void saveRecord() throws Exception{
		if(readOnly){
			return;
		}
		
		if(!SSHConnection.isSuperUser()){
			//readOnly should prevent this, but just in case
			JOptionPane.showMessageDialog(null, "Only authorized user can edit annotation");
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
				DatabaseCommands.addVariantAnnotationCuration(newAnnotation);
				mutation.addAnnotation(newAnnotation);
				parent.notifyAnnotationUpdated(newAnnotation);
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
				DatabaseCommands.addGeneAnnotationCuration(newGeneAnnotation);
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

	private class DocumentSizeFilter extends DocumentFilter {

		public DocumentSizeFilter() {
			super();
		}

		public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
			//This rejects the entire insertion if it would make
			//the contents too long. Another option would be
			//to truncate the inserted string so the contents
			//would be exactly maxCharacters in length.
			if ((fb.getDocument().getLength() + str.length()) <= maxCharacters)
				super.insertString(fb, offs, str, a);
			else
				Toolkit.getDefaultToolkit().beep();
		}

		public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
			//This rejects the entire replacement if it would make
			//the contents too long. Another option would be
			//to truncate the replacement string so the contents
			//would be exactly maxCharacters in length.
			if ((fb.getDocument().getLength() + str.length()
			- length) <= maxCharacters)
				super.replace(fb, offs, length, str, a);
			else
				Toolkit.getDefaultToolkit().beep();
		}
	}
}
