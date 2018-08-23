package hmvv.gui.mutationlist;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import hmvv.io.DatabaseCommands;
import hmvv.io.InternetCommands;

public class CosmicInfoPopup {
	static class CosmicInfo{
		public String cosmicID;
		public String geneName;
		public String geneStrand;
		public String cdsAnnotation;
		public String peptideAnnotation;
		public String count;
		public String classifiedAsSnp;
		CosmicInfo(String cosmicID){
			this.cosmicID = cosmicID;
		}
	}
	
	private static ArrayList<CosmicInfo> buildCosmicInfoList(ArrayList<String> cosmicIDList) throws Exception {
		ArrayList<CosmicInfo> comsicInfoList = new ArrayList<CosmicInfo>();
		for(String cosmicID : cosmicIDList) {
			String cosmicIDInfo = DatabaseCommands.getCosmicInfo(cosmicID);
			String[] infoArray = cosmicIDInfo.split(";");
			CosmicInfo thisInfo = new CosmicInfo(cosmicID);
			comsicInfoList.add(thisInfo);
			for(String infoField : infoArray) {
				String[] pair = infoField.split("=");
				switch(pair[0]) {
					case "GENE": thisInfo.geneName = pair[1];break;
					case "STRAND": thisInfo.geneStrand = pair[1];break;
					case "CDS": thisInfo.cdsAnnotation = pair[1];break;
					case "AA": thisInfo.peptideAnnotation = pair[1];break;
					case "CNT": thisInfo.count = pair[1];break;
					case "SNP": thisInfo.classifiedAsSnp = "SNP";break;
				}
			}
		}
		
		return comsicInfoList;
	}
	
	public static void handleCosmicClick(MutationListFrame parent, ArrayList<String> cosmicIDList) throws Exception{
		ArrayList<CosmicInfo> comsicInfoList = buildCosmicInfoList(cosmicIDList);
		DefaultTableModel tableModel = new DefaultTableModel(){
			private static final long serialVersionUID = 1L;
			
			@Override 
			public final boolean isCellEditable(int row, int column) {
				return false;
			}
			
			@Override
			public final int getColumnCount() {
				return 7;
			}
			
			@Override
			public final int getRowCount() {
				return comsicInfoList.size();
			}
			
			@Override
			public final String getColumnName(int column) {
				switch(column) {
					case 0: return "CosmicID";
					case 1: return "GENE";
					case 2: return "STRAND";
					case 3: return "CDS";
					case 4: return "AA";
					case 5: return "CNT";
					case 6: return "SNP";
					default: return "";
				}
			}
			
			@Override
			public  final Object getValueAt(int row, int column) {
				switch(column) {
					case 0: return comsicInfoList.get(row).cosmicID;
					case 1: return comsicInfoList.get(row).geneName;
					case 2: return comsicInfoList.get(row).geneStrand;
					case 3: return comsicInfoList.get(row).cdsAnnotation;
					case 4: return comsicInfoList.get(row).peptideAnnotation;
					case 5: return comsicInfoList.get(row).count;
					case 6: return comsicInfoList.get(row).classifiedAsSnp;
					default: return "";
				}
			}
			
			@Override
			public final Class<?> getColumnClass(int column) {
				return String.class;
			}
		};
		JTable table = new JTable(tableModel) {
			private static final long serialVersionUID = 1L;

			//Implement table header tool tips.
			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(columnModel) {
					private static final long serialVersionUID = 1L;
					
					public String getToolTipText(MouseEvent e) {
						int index = table.columnAtPoint(e.getPoint());
						int realIndex = table.convertColumnIndexToModel(index);
						switch(realIndex) {
							case 0: return "COSMIC ID";
							case 1: return "Gene Name";
							case 2: return "Strand (+ or -)";
							case 3: return "CDS annotation";
							case 4: return "Peptide annotation";
							case 5: return "How many samples (in COSMIC?) have this mutation";
							case 6: return "Classified as SNP";
							default: return "";
						}
					}
				};
			}
		};
		
		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setPreferredSize(new Dimension(1200,500));
		int returnValue = JOptionPane.showConfirmDialog(parent, tableScrollPane, "Open CosmicID's in Web Browser?", JOptionPane.YES_NO_OPTION);
		if(returnValue == JOptionPane.OK_OPTION) {
			InternetCommands.searchCosmic(cosmicIDList);
		}
	}
}

