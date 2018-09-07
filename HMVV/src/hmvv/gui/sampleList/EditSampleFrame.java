package hmvv.gui.sampleList;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import hmvv.gui.GUICommonTools;
import hmvv.model.Sample;

public class EditSampleFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField textLast;
	private JTextField textFirst;
	private JTextField textOrder;
	private JTextField textPathology;
	private JTextField textSource;
	private JTextField textPercent;
	private JTextField textPatientHistory;
	private JTextField textBMDiagnosis;
	private JTextField textNote;
	private JLabel labelID1;
	private JLabel labelAssay1;
	private JLabel labelInstrument1;
	private JLabel labelRunID1;
	private JLabel labelSampleID1;
	private JLabel labelRunDate1;
	private JButton btnSubmit;
	private JButton btnDelete;
	private Sample sample;
	
	/**
	 * Create the frame.
	 */
	public EditSampleFrame(Sample sample) {
		super("Edit Sample");
		this.sample = sample;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 570, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblId = new JLabel("ID");
		lblId.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblId.setBounds(42, 40, 46, 14);
		contentPane.add(lblId);

		labelID1 = new JLabel("" + sample.sampleID);
		labelID1.setFont(GUICommonTools.TAHOMA_BOLD_13);
		labelID1.setBounds(207, 40, 81, 18);
		contentPane.add(labelID1);

		JLabel lblAssay = new JLabel("Assay");
		lblAssay.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblAssay.setBounds(42, 70, 46, 14);
		contentPane.add(lblAssay);

		labelAssay1 = new JLabel(sample.assay);
		labelAssay1.setFont(GUICommonTools.TAHOMA_BOLD_13);
		labelAssay1.setBounds(207, 70, 81, 18);
		contentPane.add(labelAssay1);

		JLabel lblInstrument = new JLabel("Instrument");
		lblInstrument.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblInstrument.setBounds(42, 100, 81, 14);
		contentPane.add(lblInstrument);

		JLabel lblRunid = new JLabel("runID");
		lblRunid.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblRunid.setBounds(42, 130, 81, 14);
		contentPane.add(lblRunid);

		JLabel lblSampleid = new JLabel("sampleID");
		lblSampleid.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblSampleid.setBounds(42, 160, 81, 14);
		contentPane.add(lblSampleid);

		JLabel lblLastname = new JLabel("LastName");
		lblLastname.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblLastname.setBounds(42, 220, 81, 14);
		contentPane.add(lblLastname);

		JLabel lblFirstname = new JLabel("firstName");
		lblFirstname.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblFirstname.setBounds(42, 250, 81, 14);
		contentPane.add(lblFirstname);

		JLabel lblOrdernumber = new JLabel("orderNumber");
		lblOrdernumber.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblOrdernumber.setBounds(42, 280, 100, 14);
		contentPane.add(lblOrdernumber);

		JLabel lblPathologynumber = new JLabel("pathologyNumber");
		lblPathologynumber.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblPathologynumber.setBounds(42, 310, 127, 14);
		contentPane.add(lblPathologynumber);

		JLabel lblTumorsource = new JLabel("tumorSource");
		lblTumorsource.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblTumorsource.setBounds(42, 340, 127, 14);
		contentPane.add(lblTumorsource);

		JLabel lblTumorpercent = new JLabel("tumorPercent");
		lblTumorpercent.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblTumorpercent.setBounds(42, 370, 127, 14);
		contentPane.add(lblTumorpercent);

		JLabel lblRundate = new JLabel("runDate");
		lblRundate.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblRundate.setBounds(42, 190, 127, 14);
		contentPane.add(lblRundate);

		JLabel lblPatientHistory = new JLabel("Patient History");
		lblPatientHistory.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblPatientHistory.setBounds(42, 400, 127, 14);
		contentPane.add(lblPatientHistory);

		JLabel lblBMDiagnosis = new JLabel("BM Diagnosis");
		lblBMDiagnosis.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblBMDiagnosis.setBounds(42, 430, 127, 14);
		contentPane.add(lblBMDiagnosis);

		JLabel lblNote = new JLabel("Note");
		lblNote.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblNote.setBounds(42, 460, 127, 14);
		contentPane.add(lblNote);

		labelInstrument1 = new JLabel(sample.instrument);
		labelInstrument1.setFont(GUICommonTools.TAHOMA_BOLD_13);
		labelInstrument1.setBounds(207, 101, 81, 18);
		contentPane.add(labelInstrument1);

		labelRunID1 = new JLabel(sample.runID);
		labelRunID1.setFont(GUICommonTools.TAHOMA_BOLD_13);
		labelRunID1.setBounds(207, 131, 81, 18);
		contentPane.add(labelRunID1);

		labelSampleID1 = new JLabel(sample.sampleName);
		labelSampleID1.setFont(GUICommonTools.TAHOMA_BOLD_13);
		labelSampleID1.setBounds(207, 161, 100, 18);
		contentPane.add(labelSampleID1);

		labelRunDate1 = new JLabel(sample.runDate);
		labelRunDate1.setFont(GUICommonTools.TAHOMA_BOLD_13);
		labelRunDate1.setBounds(207, 190, 150, 18);
		contentPane.add(labelRunDate1);

		textLast = new JTextField(sample.getLastName());
		textLast.setBounds(207, 220, 218, 20);
		contentPane.add(textLast);
		textLast.setColumns(10);

		textFirst = new JTextField(sample.getFirstName());
		textFirst.setColumns(10);
		textFirst.setBounds(207, 250, 218, 20);
		contentPane.add(textFirst);

		textOrder = new JTextField(sample.getOrderNumber());
		textOrder.setColumns(10);
		textOrder.setBounds(207, 280, 218, 20);
		contentPane.add(textOrder);

		textPathology = new JTextField(sample.getPathNumber());
		textPathology.setColumns(10);
		textPathology.setBounds(207, 310, 218, 20);
		contentPane.add(textPathology);

		textSource = new JTextField(sample.getTumorSource());
		textSource.setColumns(10);
		textSource.setBounds(207, 340, 218, 20);
		contentPane.add(textSource);

		textPercent = new JTextField(sample.getTumorPercent());
		textPercent.setColumns(10);
		textPercent.setBounds(207, 370, 218, 20);
		contentPane.add(textPercent);

		textPatientHistory = new JTextField(sample.getNote());
		textPatientHistory.setColumns(10);
		textPatientHistory.setBounds(207, 400, 218, 20);
		contentPane.add(textPatientHistory);

		textBMDiagnosis = new JTextField(sample.getNote());
		textBMDiagnosis.setColumns(10);
		textBMDiagnosis.setBounds(207, 430, 218, 20);
		contentPane.add(textBMDiagnosis);

		textNote = new JTextField(sample.getNote());
		textNote.setColumns(10);
		textNote.setBounds(207, 460, 218, 20);
		contentPane.add(textNote);

		btnSubmit = new JButton("Update");
		btnSubmit.setFont(GUICommonTools.TAHOMA_BOLD_14);
		btnSubmit.setBounds(267, 513, 89, 23);
		contentPane.add(btnSubmit);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				EditSampleFrame.this.setVisible(false);
			}
		});
		btnCancel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		btnCancel.setBounds(412, 513, 89, 23);
		contentPane.add(btnCancel);

		btnDelete = new JButton("Delete");

		btnDelete.setFont(GUICommonTools.TAHOMA_BOLD_14);
		btnDelete.setBounds(128, 513, 89, 23);
		contentPane.add(btnDelete);
	}

	public Sample getUpdatedSample(){
		sample.setFirstName(textFirst.getText());
		sample.setLastName(textLast.getText());
		sample.setOrderNumber(textOrder.getText());
		sample.setPathNumber(textPathology.getText());
		sample.setTumorSource(textSource.getText());
		sample.setTumorPercent(textPercent.getText());
		sample.setPatientHistory(textPatientHistory.getText());
		sample.setBmDiagnosis(textBMDiagnosis.getText());
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
