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
import hmvv.model.Mutation;

public class CosmicInfoPopup {
	static class CosmicInfo{
		public boolean openItem;
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
		
		public String getTranscript() {
			if(geneName.contains("ENST")) {
				String[] split = geneName.split("_");
				if(split.length > 1) {
					return split[1];
				}
			}
			return "";
		}
	}
	
	private static ArrayList<CosmicInfo> buildCosmicInfoList(Mutation mutation) throws Exception {
		String HGVSc = mutation.getHGVSc();
		String transcript = "";
		if(HGVSc.startsWith("ENST")) {
			transcript = HGVSc.split("\\.")[0];
		}
		
		ArrayList<String> cosmicIDList = mutation.getCosmicID();
		ArrayList<CosmicInfo> comsicInfoList = new ArrayList<CosmicInfo>();
		boolean transcriptFound = false;
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
			
			//looking for transcript that matches this mutation
			if(thisInfo.getTranscript().equals(transcript)) {
				thisInfo.openItem = true;
				transcriptFound = true;
			}
		}
		
		//Find default cosmicID if no transcript found
		if(!transcriptFound) {
			for(CosmicInfo thisInfo : comsicInfoList) {
				if(thisInfo.getTranscript().equals("")) {
					thisInfo.openItem = true;
				}
			}
		}
		
		return comsicInfoList;
	}
	
	public static void handleCosmicClick(MutationListFrame parent, Mutation mutation) throws Exception{
		ArrayList<CosmicInfo> comsicInfoList = buildCosmicInfoList(mutation);
		DefaultTableModel tableModel = new DefaultTableModel(){
			private static final long serialVersionUID = 1L;
			
			@Override 
			public final boolean isCellEditable(int row, int column) {
				if(column == 0) {
					return true;
				}
				return false;
			}
			
			@Override
			public final int getColumnCount() {
				return 8;
			}
			
			@Override
			public final int getRowCount() {
				return comsicInfoList.size();
			}
			
			@Override
			public final String getColumnName(int column) {
				switch(column) {
					case 0: return "Open?";
					case 1: return "CosmicID";
					case 2: return "GENE";
					case 3: return "STRAND";
					case 4: return "CDS";
					case 5: return "AA";
					case 6: return "CNT";
					case 7: return "SNP";
					default: return "";
				}
			}
			
			@Override
			public final void setValueAt(Object aValue, int row, int column) {
				if(column == 0) {
					comsicInfoList.get(row).openItem = (Boolean) aValue;
				}
			}
			
			@Override
			public  final Object getValueAt(int row, int column) {
				switch(column) {
					case 0: return comsicInfoList.get(row).openItem;
					case 1: return comsicInfoList.get(row).cosmicID;
					case 2: return comsicInfoList.get(row).geneName;
					case 3: return comsicInfoList.get(row).geneStrand;
					case 4: return comsicInfoList.get(row).cdsAnnotation;
					case 5: return comsicInfoList.get(row).peptideAnnotation;
					case 6: return comsicInfoList.get(row).count;
					case 7: return comsicInfoList.get(row).classifiedAsSnp;
					default: return "";
				}
			}
			
			@Override
			public final Class<?> getColumnClass(int column) {
				if(column == 0) {
					return Boolean.class;
				}
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
							case 0: return "Open in browser?";
							case 1: return "COSMIC ID";
							case 2: return "Gene Name";
							case 3: return "Strand (+ or -)";
							case 4: return "CDS annotation";
							case 5: return "Peptide annotation";
							case 6: return "How many samples (in COSMIC?) have this mutation";
							case 7: return "Classified as SNP";
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
			for(CosmicInfo cosmicID : comsicInfoList){
				if(cosmicID.openItem) {
					InternetCommands.searchCosmic(cosmicID.cosmicID);
				}
			}
		}
	}
}

