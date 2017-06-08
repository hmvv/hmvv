package hmvv.gui.sampleList;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import hmvv.gui.GUICommonTools;
import hmvv.io.DatabaseCommands;
import hmvv.model.Mutation;

public class MutationSearchDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JTextField TextOrder;
	private JTextField TextLastName;
	private JTextField TextFirstName;
	private JTextField TextGene;
	private JTextField TextCosmicID;
	private JTextField textCDNA;
	private JTextField textAA;
	private JButton okButton;
	private JButton cancelButton;
	private JComboBox<String> comboBox;
	private JLabel lblCdnaChange;
	private JLabel lblAaChange;
	private JLabel lblie;
	private JLabel lbliegt;

	/**
	 * Create the dialog.
	 */
	public MutationSearchDialog() {
		this.setTitle("Mutation Search");
		setBounds(100, 100, 435, 382);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[] {30, 30, 30, 30, 30, 30, 30, 0, 0, 0, 30};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblAssay = new JLabel("Assay");
			lblAssay.setFont(GUICommonTools.TAHOMA_BOLD_13);
			GridBagConstraints gbc_lblAssay = new GridBagConstraints();
			gbc_lblAssay.anchor = GridBagConstraints.EAST;
			gbc_lblAssay.insets = new Insets(0, 0, 5, 5);
			gbc_lblAssay.gridx = 1;
			gbc_lblAssay.gridy = 1;
			contentPanel.add(lblAssay, gbc_lblAssay);
		}
		{
			comboBox = new JComboBox<String>();
			comboBox.addItem("All");
			
			try {
				for(String assay : DatabaseCommands.getAllAssays()){
					comboBox.addItem(assay);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
			
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.insets = new Insets(0, 0, 5, 5);
			gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox.gridx = 2;
			gbc_comboBox.gridy = 1;
			contentPanel.add(comboBox, gbc_comboBox);
		}
		{
			JLabel lblOrderNumber = new JLabel("Order Number");
			lblOrderNumber.setFont(GUICommonTools.TAHOMA_BOLD_13);
			GridBagConstraints gbc_lblOrderNumber = new GridBagConstraints();
			gbc_lblOrderNumber.anchor = GridBagConstraints.EAST;
			gbc_lblOrderNumber.insets = new Insets(0, 0, 5, 5);
			gbc_lblOrderNumber.gridx = 1;
			gbc_lblOrderNumber.gridy = 2;
			contentPanel.add(lblOrderNumber, gbc_lblOrderNumber);
		}
		{
			TextOrder = new JTextField();
			GridBagConstraints gbc_TextOrder = new GridBagConstraints();
			gbc_TextOrder.insets = new Insets(0, 0, 5, 5);
			gbc_TextOrder.fill = GridBagConstraints.HORIZONTAL;
			gbc_TextOrder.gridx = 2;
			gbc_TextOrder.gridy = 2;
			contentPanel.add(TextOrder, gbc_TextOrder);
			TextOrder.setColumns(10);
		}
		{
			JLabel lblLastName = new JLabel("Last Name");
			lblLastName.setFont(GUICommonTools.TAHOMA_BOLD_13);
			GridBagConstraints gbc_lblLastName = new GridBagConstraints();
			gbc_lblLastName.anchor = GridBagConstraints.EAST;
			gbc_lblLastName.insets = new Insets(0, 0, 5, 5);
			gbc_lblLastName.gridx = 1;
			gbc_lblLastName.gridy = 3;
			contentPanel.add(lblLastName, gbc_lblLastName);
		}
		{
			TextLastName = new JTextField();
			GridBagConstraints gbc_TextLastName = new GridBagConstraints();
			gbc_TextLastName.insets = new Insets(0, 0, 5, 5);
			gbc_TextLastName.fill = GridBagConstraints.HORIZONTAL;
			gbc_TextLastName.gridx = 2;
			gbc_TextLastName.gridy = 3;
			contentPanel.add(TextLastName, gbc_TextLastName);
			TextLastName.setColumns(10);
		}
		{
			JLabel lblFirstName = new JLabel("First Name");
			lblFirstName.setFont(GUICommonTools.TAHOMA_BOLD_13);
			GridBagConstraints gbc_lblFirstName = new GridBagConstraints();
			gbc_lblFirstName.anchor = GridBagConstraints.EAST;
			gbc_lblFirstName.insets = new Insets(0, 0, 5, 5);
			gbc_lblFirstName.gridx = 1;
			gbc_lblFirstName.gridy = 4;
			contentPanel.add(lblFirstName, gbc_lblFirstName);
		}
		{
			TextFirstName = new JTextField();
			GridBagConstraints gbc_TextFirstName = new GridBagConstraints();
			gbc_TextFirstName.insets = new Insets(0, 0, 5, 5);
			gbc_TextFirstName.fill = GridBagConstraints.HORIZONTAL;
			gbc_TextFirstName.gridx = 2;
			gbc_TextFirstName.gridy = 4;
			contentPanel.add(TextFirstName, gbc_TextFirstName);
			TextFirstName.setColumns(10);
		}
		{
			JLabel lblGene = new JLabel("Gene");
			lblGene.setFont(GUICommonTools.TAHOMA_BOLD_13);
			GridBagConstraints gbc_lblGene = new GridBagConstraints();
			gbc_lblGene.anchor = GridBagConstraints.EAST;
			gbc_lblGene.insets = new Insets(0, 0, 5, 5);
			gbc_lblGene.gridx = 1;
			gbc_lblGene.gridy = 5;
			contentPanel.add(lblGene, gbc_lblGene);
		}
		{
			TextGene = new JTextField();
			GridBagConstraints gbc_TextGene = new GridBagConstraints();
			gbc_TextGene.insets = new Insets(0, 0, 5, 5);
			gbc_TextGene.fill = GridBagConstraints.HORIZONTAL;
			gbc_TextGene.gridx = 2;
			gbc_TextGene.gridy = 5;
			contentPanel.add(TextGene, gbc_TextGene);
			TextGene.setColumns(10);
		}
		{
			JLabel lblCosmicId = new JLabel("Cosmic ID");
			lblCosmicId.setFont(GUICommonTools.TAHOMA_BOLD_13);
			GridBagConstraints gbc_lblCosmicId = new GridBagConstraints();
			gbc_lblCosmicId.anchor = GridBagConstraints.EAST;
			gbc_lblCosmicId.insets = new Insets(0, 0, 5, 5);
			gbc_lblCosmicId.gridx = 1;
			gbc_lblCosmicId.gridy = 6;
			contentPanel.add(lblCosmicId, gbc_lblCosmicId);
		}
		{
			TextCosmicID = new JTextField();
			GridBagConstraints gbc_TextCosmicID = new GridBagConstraints();
			gbc_TextCosmicID.insets = new Insets(0, 0, 5, 5);
			gbc_TextCosmicID.fill = GridBagConstraints.HORIZONTAL;
			gbc_TextCosmicID.gridx = 2;
			gbc_TextCosmicID.gridy = 6;
			contentPanel.add(TextCosmicID, gbc_TextCosmicID);
			TextCosmicID.setColumns(10);
		}
		{
			lblCdnaChange = new JLabel("cDNA change");
			lblCdnaChange.setFont(GUICommonTools.TAHOMA_BOLD_13);
			GridBagConstraints gbc_lblCdnaChange = new GridBagConstraints();
			gbc_lblCdnaChange.anchor = GridBagConstraints.EAST;
			gbc_lblCdnaChange.insets = new Insets(0, 0, 5, 5);
			gbc_lblCdnaChange.gridx = 1;
			gbc_lblCdnaChange.gridy = 7;
			contentPanel.add(lblCdnaChange, gbc_lblCdnaChange);
		}
		{
			textCDNA = new JTextField();
			textCDNA.setColumns(10);
			GridBagConstraints gbc_textCDNA = new GridBagConstraints();
			gbc_textCDNA.insets = new Insets(0, 0, 5, 5);
			gbc_textCDNA.fill = GridBagConstraints.HORIZONTAL;
			gbc_textCDNA.gridx = 2;
			gbc_textCDNA.gridy = 7;
			contentPanel.add(textCDNA, gbc_textCDNA);
		}
		{
			lbliegt = new JLabel("(ie: 1798G>T)");
			GridBagConstraints gbc_lbliegt = new GridBagConstraints();
			gbc_lbliegt.insets = new Insets(0, 0, 5, 0);
			gbc_lbliegt.gridx = 3;
			gbc_lbliegt.gridy = 7;
			contentPanel.add(lbliegt, gbc_lbliegt);
		}
		{
			lblAaChange = new JLabel("AA change");
			lblAaChange.setFont(GUICommonTools.TAHOMA_BOLD_13);
			GridBagConstraints gbc_lblAaChange = new GridBagConstraints();
			gbc_lblAaChange.anchor = GridBagConstraints.EAST;
			gbc_lblAaChange.insets = new Insets(0, 0, 5, 5);
			gbc_lblAaChange.gridx = 1;
			gbc_lblAaChange.gridy = 8;
			contentPanel.add(lblAaChange, gbc_lblAaChange);
		}
		{
			textAA = new JTextField();
			textAA.setColumns(10);
			GridBagConstraints gbc_textAA = new GridBagConstraints();
			gbc_textAA.insets = new Insets(0, 0, 5, 5);
			gbc_textAA.fill = GridBagConstraints.HORIZONTAL;
			gbc_textAA.gridx = 2;
			gbc_textAA.gridy = 8;
			contentPanel.add(textAA, gbc_textAA);
		}
		{
			lblie = new JLabel("(ie: Gln2102Ter)");
			GridBagConstraints gbc_lblie = new GridBagConstraints();
			gbc_lblie.insets = new Insets(0, 0, 5, 0);
			gbc_lblie.gridx = 3;
			gbc_lblie.gridy = 8;
			contentPanel.add(lblie, gbc_lblie);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("Search");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						MutationSearchDialog.this.setVisible(false);
					}
				});
				buttonPane.add(cancelButton);
			}
		}

		DocumentListener listener = new DocumentListener(){
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateOkButton();
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateOkButton();
			}
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updateOkButton();
			}
		};
		TextOrder.getDocument().addDocumentListener(listener);
		TextLastName.getDocument().addDocumentListener(listener);
		TextFirstName.getDocument().addDocumentListener(listener);
		TextGene.getDocument().addDocumentListener(listener);
		TextCosmicID.getDocument().addDocumentListener(listener);
		textCDNA.getDocument().addDocumentListener(listener);
		textAA.getDocument().addDocumentListener(listener);
		updateOkButton();//fire the event to set the ok button appropriately
	}

	void updateOkButton(){
		if(	TextOrder.getText().length() == 0 && TextLastName.getText().length() == 0 && TextFirstName.getText().length() == 0 &&
				TextGene.getText().length() == 0 && TextCosmicID.getText().length() == 0 && textCDNA.getText().length() == 0 && textAA.getText().length() == 0){
			okButton.setEnabled(false);
		}else{
			okButton.setEnabled(true);
		}
	}

	public ArrayList<Mutation> getMutationSearchResults() throws Exception{
		String assay = comboBox.getSelectedItem().toString();
		String orderNumber = TextOrder.getText();
		String lastName = TextLastName.getText();
		String firstName = TextFirstName.getText();
		String gene = TextGene.getText();
		String cosmicID = TextCosmicID.getText();
		String cDNA = textCDNA.getText();
		String codon = textAA.getText();
		return DatabaseCommands.getMutationDataByQuery(assay, orderNumber, lastName, firstName, gene, cosmicID, cDNA, codon);
	}

	public void addConfirmListener(ActionListener listener) {
		okButton.addActionListener(listener);
	}
}
