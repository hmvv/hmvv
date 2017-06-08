package hmvv.gui.sampleList;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

public class ReportFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	/**
	 * Create the frame.
	 */
	public ReportFrame(ArrayList<HashMap<String, String>> report) {
		this.setTitle("Generate Report");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 911, 785);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		JTextArea textArea = new JTextArea();
		textArea.addMouseListener(new ContextMenuMouseListener());
		for(int i=0; i< report.size(); i++){
			HashMap<String, String> record = report.get(i);
			System.out.println(record.get("Name"));
			textArea.append("Name: " + record.get("Name") + "\n");
			textArea.append("OrderNumber: " + record.get("OrderNumber") + "\n");
			textArea.append("Mutation Info: " + record.get("Mutation") + "\n");
			textArea.append("Coordinate: " + record.get("Coordinate") + "\n");
			//textArea.append("Genotype: " + record.get("Genotype") + "\n");
			textArea.append("dbSNP ID: " + record.get("dbSNP") + "\n");
			textArea.append("Cosmic ID: " + record.get("Cosmic") + "\n");
			textArea.append("Occurance: " + record.get("Occurance") + "\n");
			textArea.append("Somatic: " + record.get("Somatic") + "\n");
			textArea.append("Classification: " + record.get("Classification") + "\n");
			textArea.append("Curation Note: " + record.get("Curation") + "\n" + "\n");
		}
		JButton btnSaveAsFile = new JButton("Save As File");
		btnSaveAsFile.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
		            exportReport(textArea.getText());
		        } catch (IOException ex) {
		        	JOptionPane.showMessageDialog(null, ex.getMessage());
		        }
			}
			
		});
		btnSaveAsFile.setBounds(616, 56, 130, 23);
		contentPane.add(btnSaveAsFile);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(47, 136, 803, 569);
		scrollPane.setViewportView(textArea);
		contentPane.add(scrollPane);
		
	}
	
	private void exportReport(String text) throws IOException{
		JFileChooser SaveAs = new JFileChooser();
		SaveAs.setAcceptAllFileFilterUsed(false);
		SaveAs.addChoosableFileFilter(new FileFilter(){
			@Override
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().endsWith(".txt");
			}

			@Override
			public String getDescription() {
				return ".txt file";
			}			
		});
		int returnValue = SaveAs.showOpenDialog(SaveAs);
		if(returnValue == JFileChooser.APPROVE_OPTION){
			File fileName = SaveAs.getSelectedFile();
			if(!fileName.getName().endsWith(".txt")){
				fileName = new File(fileName.toString() + ".txt");
			}
			BufferedWriter outFile = new BufferedWriter(new FileWriter(fileName));
            outFile.write(text); 

            outFile.close();
		}
	}
}
