package hmvv.gui.sampleList;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import hmvv.gui.GUICommonTools;
import hmvv.io.SSHConnection;
import hmvv.main.Configurations;
import hmvv.main.HMVVFrame;
import hmvv.model.Sample;

public class EditSampleFrame extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JTextField textMRN;
	private JTextField textLast;
	private JTextField textFirst;
	private JTextField textOrder;
	private JTextField textPathology;
	private JTextField textSource;
	private JTextField textPercent;
	private JTextArea textPatientHistory;
	private JTextArea textDiagnosis;
	private JTextArea textNote;
	
	private JButton btnSubmit;
	private JButton btnDelete;
	private JButton btnCancel;
	private Sample sample;
	
	/**
	 * Create the frame.
	 */
	public EditSampleFrame(HMVVFrame parent, Sample sample) {
		super(parent, "Edit Sample");
		this.sample = sample;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.85), (int)(bounds.height*.85));
		setMinimumSize(new Dimension(700, getHeight()/3));
		
		textMRN = new JTextField(sample.getMRN());
		textMRN.setColumns(10);
        textMRN.setEnabled(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.EDIT_SAMPLE_LABR));


		textLast = new JTextField(sample.getLastName());
		textLast.setColumns(10);
        textLast.setEnabled(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.EDIT_SAMPLE_LABR));
		
		textFirst = new JTextField(sample.getFirstName());
		textFirst.setColumns(10);
        textFirst.setEnabled(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.EDIT_SAMPLE_LABR));

		textOrder = new JTextField(sample.getOrderNumber());
		textOrder.setColumns(10);
        textOrder.setEnabled(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.EDIT_SAMPLE_LABR));

		textPathology = new JTextField(sample.getPathNumber());
		textPathology.setColumns(10);
        textPathology.setEnabled(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.EDIT_SAMPLE_LABR));

        textSource = new JTextField(sample.getTumorSource());
		textPercent = new JTextField(sample.getTumorPercent());
		
		textPatientHistory = new JTextArea();
		textPatientHistory.setText(sample.getPatientHistory());
		textPatientHistory.setLineWrap(true);
		textPatientHistory.setWrapStyleWord(true);
		textPatientHistory.setColumns(10);

		
		textDiagnosis = new JTextArea();
		textDiagnosis.setText(sample.getDiagnosis());
		textDiagnosis.setLineWrap(true);
		textDiagnosis.setWrapStyleWord(true);
		textDiagnosis.setColumns(10);

		textNote = new JTextArea();
		textNote.setText(sample.getNote());
		textNote.setLineWrap(true);
		textNote.setWrapStyleWord(true);
		textNote.setColumns(10);

		btnSubmit = new JButton("Update");
		btnSubmit.setFont(GUICommonTools.TAHOMA_BOLD_14);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				EditSampleFrame.this.setVisible(false);
			}
		});
		btnCancel.setFont(GUICommonTools.TAHOMA_BOLD_14);

		btnDelete = new JButton("Delete");
		btnDelete.setFont(GUICommonTools.TAHOMA_BOLD_14);
        btnDelete.setEnabled(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.EDIT_SAMPLE_LABR));

		layoutComponents();
		pack();
		setLocationRelativeTo(parent);
		setResizable(false);
	}
	
	private Dimension labelDimension = new Dimension(150,20);
	private JLabel createJLabel(String text) {
		JLabel label = new JLabel(text);
		label.setPreferredSize(labelDimension);
		label.setFont(GUICommonTools.TAHOMA_BOLD_13);
		return label;
	}
	
	private JPanel createPair(String text, Component component) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(createJLabel(text), BorderLayout.WEST);
		panel.add(component, BorderLayout.CENTER);
		return panel;
	}
	
	private void layoutComponents() {

		JPanel leftPanel = new JPanel();

		//Labels
		JPanel labelPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS);
		labelPanel.setLayout(boxLayout);
		
		Dimension textFieldDimension = new Dimension(250, 20);
		int strutHeight = 10;
		
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		labelPanel.add(createPair("Sample ID", createJLabel(sample.sampleID+"")));
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		
		labelPanel.add(createPair("Instrument", createJLabel(sample.instrument)));
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		
		labelPanel.add(createPair("Run ID", createJLabel(sample.runID)));
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		
		labelPanel.add(createPair("Assay", createJLabel(sample.assay)));
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		
		labelPanel.add(createPair("Sample Name", createJLabel(sample.sampleName)));
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		
		labelPanel.add(createPair("Caller ID", createJLabel(sample.callerID)));
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		
		labelPanel.add(createPair("Coverage ID", createJLabel(sample.coverageID)));
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		
		labelPanel.add(createPair("Entered by", createJLabel(sample.enteredBy)));
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		
		labelPanel.add(createPair("Run Date", createJLabel(sample.runDate)));
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		
		labelPanel.add(createPair("MRN", textMRN));
		textMRN.setPreferredSize(textFieldDimension);
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		
		labelPanel.add(createPair("Last Name", textLast));
		textLast.setPreferredSize(textFieldDimension);
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		
		labelPanel.add(createPair("First Name", textFirst));
		textFirst.setPreferredSize(textFieldDimension);
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		
		labelPanel.add(createPair("Order Number", textOrder));
		textOrder.setPreferredSize(textFieldDimension);
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		
		labelPanel.add(createPair("Pathology Number", textPathology));
		textPathology.setPreferredSize(textFieldDimension);
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		
		labelPanel.add(createPair("Tumor Source", textSource));
		textSource.setPreferredSize(textFieldDimension);
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		
		labelPanel.add(createPair("Tumor Percent", textPercent));
		textPercent.setPreferredSize(textFieldDimension);
		labelPanel.add(Box.createVerticalStrut(strutHeight));
		labelPanel.add(Box.createVerticalStrut(strutHeight));


		JPanel textPanel = new JPanel();
		textPanel.setBorder(new EmptyBorder(5, 50, 5, 5));
		BoxLayout boxLayout2 = new BoxLayout(textPanel, BoxLayout.PAGE_AXIS);
		textPanel.setLayout(boxLayout2);


		//jtextArea
		Dimension textAreaDimension = new Dimension(400, 150);
		
		JScrollPane textPatientHistoryScroll = new JScrollPane(textPatientHistory);
		textPatientHistoryScroll.setPreferredSize(textAreaDimension);
		textPanel.add(createPair("Patient History", textPatientHistoryScroll));
		textPanel.add(Box.createVerticalStrut(strutHeight));
		
		JScrollPane textDiagnosisScroll = new JScrollPane(textDiagnosis);
		textDiagnosisScroll.setPreferredSize(textAreaDimension);
		textPanel.add(createPair("Diagnosis", textDiagnosisScroll));
		textPanel.add(Box.createVerticalStrut(strutHeight));
		
		JScrollPane textNoteScroll = new JScrollPane(textNote);
		textNoteScroll.setPreferredSize(textAreaDimension);
		textPanel.add(createPair("Note", textNoteScroll));
		textPanel.add(Box.createVerticalStrut(strutHeight));
		
		JPanel southPanel = new JPanel();
		southPanel.setBorder(new EmptyBorder(10, 25, 10, 25));
		GridLayout southLayout = new GridLayout(1,0);
		southLayout.setHgap(20);
		southPanel.setLayout(southLayout);
		southPanel.add(btnDelete);
		southPanel.add(btnSubmit);
		southPanel.add(btnCancel);
		
		setLayout(new BorderLayout());
		leftPanel.add(labelPanel);
		add(leftPanel, BorderLayout.CENTER);
		add(textPanel, BorderLayout.LINE_END);
		add(southPanel, BorderLayout.SOUTH);
	}

	public Sample getUpdatedSample(){
		sample.setMRN(textMRN.getText());
		sample.setLastName(textLast.getText());
		sample.setFirstName(textFirst.getText());
		sample.setOrderNumber(textOrder.getText());
		sample.setPathNumber(textPathology.getText());
		sample.setTumorSource(textSource.getText());
		sample.setTumorPercent(textPercent.getText());
		sample.setPatientHistory(textPatientHistory.getText());
		sample.setDiagnosis(textDiagnosis.getText());
		sample.setNote(textNote.getText());
		return sample;
	}
	
	public void addConfirmListener(ActionListener listener) {
		btnSubmit.addActionListener(listener);
	}

	public void addDeleteListener(ActionListener listener) {
		btnDelete.addActionListener(listener);
	}
}
