package hmvv.gui.mutationlist;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
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
import hmvv.model.GeneAnnotation;
import hmvv.model.Mutation;

public class AnnotationFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private CommonTable parent;
	
	private JButton okButton;
	private JButton cancelButton;
	
	private JComboBox<String> pathogenicityComboBox;
	private JComboBox<String> mutationTypeComboBox;
	
	private JTextArea geneAnnotationTextArea;
	private JTextArea annotationTextArea;
	private int maxCharacters = 5000;
	private DefaultStyledDocument defaultStyledDocument;
	
	private JLabel historyLabel;
	
	private Boolean readOnly;
	
	private Annotation currentAnnotation;
	private GeneAnnotation geneAnnotation;
	private Mutation mutation;
	
	/**
	 * Create the dialog.
	 */
	public AnnotationFrame(Boolean readOnly, Mutation mutation, GeneAnnotation geneAnnotation, Annotation annotation, CommonTable parent, MutationListFrame mutationListFrame) {
		super("Annotation - " + mutation.getGene() + " - " + annotation.getCoordinate().getCoordinateAsString());
		this.mutation = mutation;
		this.readOnly = readOnly;
		this.currentAnnotation = annotation;
		this.geneAnnotation = geneAnnotation;
		this.parent = parent;
		
		createComponents();
		layoutComponents();
		activateComponents();
		
		if(readOnly){
			setTitle("Annotation (read only) - " + mutation.getGene() + " - " + annotation.getCoordinate().getCoordinateAsString());
			geneAnnotationTextArea.setEditable(false);
			annotationTextArea.setEditable(false);
			pathogenicityComboBox.setEnabled(false);
			mutationTypeComboBox.setEnabled(false);
		}
		
		pack();
		setResizable(false);
		setLocationRelativeTo(mutationListFrame);
		openRecord();
	}

	private void createComponents(){
		Dimension textAreaDimension = new Dimension(400,400);
		
		pathogenicityComboBox = new JComboBox<String>();
		pathogenicityComboBox.addItem("Not set");
		pathogenicityComboBox.addItem("Benign");
		pathogenicityComboBox.addItem("Likely Benign");
		pathogenicityComboBox.addItem("Unknown");
		pathogenicityComboBox.addItem("Likely Pathogenic");
		pathogenicityComboBox.addItem("Pathogenic");
		pathogenicityComboBox.setSelectedItem(currentAnnotation.getClassification());

		mutationTypeComboBox = new JComboBox<String>();
		mutationTypeComboBox.addItem("Not set");
		mutationTypeComboBox.addItem("Somatic");
		mutationTypeComboBox.addItem("Germline");
		mutationTypeComboBox.addItem("Unknown");
		mutationTypeComboBox.setSelectedItem(currentAnnotation.getSomatic());

		
		geneAnnotationTextArea = new JTextArea();
		geneAnnotationTextArea.setWrapStyleWord(true);
		geneAnnotationTextArea.setLineWrap(true);
		geneAnnotationTextArea.setText(geneAnnotation.getCuration());
		geneAnnotationTextArea.setPreferredSize(textAreaDimension);
		
		annotationTextArea = new JTextArea();
		annotationTextArea.setWrapStyleWord(true);
		annotationTextArea.setLineWrap(true);
		annotationTextArea.setText(currentAnnotation.getCuration());
		annotationTextArea.setPreferredSize(textAreaDimension);
		
		historyLabel = new JLabel(currentAnnotation.getUpdateStatus());
		
		okButton = new JButton("OK");
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");

		defaultStyledDocument = new DefaultStyledDocument();

		//annotationTextArea.setDocument(defaultStyledDocument);
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
		JLabel lblCoordinateText = new JLabel(currentAnnotation.getCoordinate().getCoordinateAsString());
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
		
		JPanel textAreaPanel = new JPanel();
		//Annotation
		JPanel annotationPanel = new JPanel();
		annotationPanel.setLayout(new GridLayout(1,0));
		JScrollPane annotationScrollPane = new JScrollPane();
		annotationScrollPane.setViewportView(annotationTextArea);
		TitledBorder annotationBorder = BorderFactory.createTitledBorder("Variant Annotation (5000 characters max)");
		annotationBorder.setTitleFont(GUICommonTools.TAHOMA_BOLD_14);
		annotationScrollPane.setBorder(annotationBorder);
		annotationPanel.add(annotationScrollPane);
		textAreaPanel.add(annotationPanel);

		//GeneAnnotation
		JPanel geneAnnotationPanel = new JPanel();
		geneAnnotationPanel.setLayout(new GridLayout(1,0));
		JScrollPane geneScrollPane = new JScrollPane();
		geneScrollPane.setViewportView(geneAnnotationTextArea);
		TitledBorder geneAnnotationBorder = BorderFactory.createTitledBorder("" + mutation.getGene() + " Annotation (5000 characters max)");
		geneAnnotationBorder.setTitleFont(GUICommonTools.TAHOMA_BOLD_14);
		geneAnnotationPanel.setBorder(geneAnnotationBorder);
		geneAnnotationPanel.add(geneScrollPane);
		textAreaPanel.add(geneAnnotationPanel);
		
		//JLabel lblMaxCharacters = new JLabel("Max " + maxCharacters + " characters");
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.add(historyLabel);
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
					updateRecord();
					parent.notifyAnnotationUpdated(currentAnnotation);
				}catch(Exception e){
					JOptionPane.showMessageDialog(AnnotationFrame.this, e.getMessage());
				}finally{
					closeRecord();
				}
			}
		});

		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				closeRecord();
				AnnotationFrame.this.dispose();
			}
		});

		defaultStyledDocument.setDocumentFilter(new DocumentSizeFilter(maxCharacters));

		annotationTextArea.addMouseListener(new ContextMenuMouseListener());

		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				//Called if the user clicks the "X" to close the window
				closeRecord();
			}
		});
	}

	private void openRecord(){
		try {
			if(!readOnly){
				DatabaseCommands.setAnnotationStatus(Annotation.STATUS.open, currentAnnotation);
				
				geneAnnotation.setLocked(true);
				DatabaseCommands.setGeneAnnotationLock(geneAnnotation);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	private void closeRecord(){
		try {
			if(!readOnly){
				DatabaseCommands.setAnnotationStatus(Annotation.STATUS.close, currentAnnotation);
				
				geneAnnotation.setLocked(false);
				DatabaseCommands.setGeneAnnotationLock(geneAnnotation);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e);
		}
	}

	private void updateRecord() throws Exception{
		if(readOnly){
			return;
		}
		
		if(!SSHConnection.isSuperUser()){
			//readOnly should prevent this, but just in case
			JOptionPane.showMessageDialog(null, "Only authorized user can edit annotation");
			return;
		}
		
		currentAnnotation.setClassification(pathogenicityComboBox.getSelectedItem().toString());
		currentAnnotation.setSomatic(mutationTypeComboBox.getSelectedItem().toString());
		currentAnnotation.setCuration(annotationTextArea.getText());
		currentAnnotation.setUpdateStatus(getUpdateStatus());
		
		if(!currentAnnotation.isAnnotationSet()){
			DatabaseCommands.deleteAnnotation(currentAnnotation);
		}else{
			DatabaseCommands.updateAnnotation(currentAnnotation);
		}
		
		geneAnnotation.setCuration(geneAnnotationTextArea.getText());
		DatabaseCommands.setGeneAnnotationCuration(geneAnnotation);
	}

	private String getUpdateStatus(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Calendar cal = Calendar.getInstance();
		String date = dateFormat.format(cal.getTime());
		String updateStatus = String.format("Last update: %s by %s", date, SSHConnection.getUserName());
		return updateStatus;
	}

	private class DocumentSizeFilter extends DocumentFilter {
		int maxCharacters;
		boolean DEBUG = false;

		public DocumentSizeFilter(int maxChars) {
			maxCharacters = maxChars;
		}

		public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
			if (DEBUG) {
				System.out.println("in DocumentSizeFilter's insertString method");
			}

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
			if (DEBUG) {
				System.out.println("in DocumentSizeFilter's replace method");
			}
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
