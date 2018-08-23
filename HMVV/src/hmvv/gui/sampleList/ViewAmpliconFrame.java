package hmvv.gui.sampleList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import hmvv.gui.GUICommonTools;
import hmvv.io.DatabaseCommands;
import hmvv.model.Amplicon;
import hmvv.model.AmpliconCount;
import hmvv.model.Sample;

public class ViewAmpliconFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JLabel lblSample;
	private JTextArea textArea;
	private JLabel lblNumber;
	private JLabel lblTotal;
	private Sample sample;
	
	/**
	 * Create the frame.
	 */
	public ViewAmpliconFrame(Sample sample) throws Exception{
		this.sample = sample;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 655, 683);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblAmpliconsBelowCutoff = new JLabel("Amplicons below cutoff");
		lblAmpliconsBelowCutoff.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		JScrollPane scrollPane = new JScrollPane();
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		lblSample = new JLabel("sample");
		lblSample.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		lblNumber = new JLabel("below");
		lblNumber.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		JLabel lblTotalAmplicons = new JLabel("Total amplicons");
		lblTotalAmplicons.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		lblTotal = new JLabel("total");
		lblTotal.setFont(GUICommonTools.TAHOMA_BOLD_14);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(18)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
					.addGap(38))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(19)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblSample, GroupLayout.PREFERRED_SIZE, 396, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblAmpliconsBelowCutoff, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(lblNumber))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblTotalAmplicons, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(lblTotal, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)))
					.addGap(188))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(6)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAmpliconsBelowCutoff, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNumber))
					.addGap(4)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTotalAmplicons, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTotal, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(lblSample, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
					.addGap(17)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
					.addGap(17))
		);
		contentPane.setLayout(gl_contentPane);
		
		initialize();
	}
	
	private void initialize() throws Exception{
		for(Amplicon amplicon : DatabaseCommands.getFailedAmplicon(sample.sampleID)){
			textArea.append(amplicon.ampliconName);
			textArea.append("\t");
			textArea.append(amplicon.readDepth);
			textArea.append("\n");
		}
		lblSample.setText(String.format("%s,%s: %s", sample.getLastName(), sample.getFirstName(), sample.getOrderNumber()));
		
		AmpliconCount ampliconCount = DatabaseCommands.getAmpliconCount(sample.sampleID);
		lblNumber.setText(ampliconCount.failedAmplicon);
		lblTotal.setText(ampliconCount.totalAmplicon);
	}
}
