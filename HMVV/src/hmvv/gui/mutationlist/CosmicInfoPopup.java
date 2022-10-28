package hmvv.gui.mutationlist;

import java.awt.Dimension;
import java.awt.event.MouseEvent;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import hmvv.gui.mutationlist.tables.CommonTable;
import hmvv.io.DatabaseCommands_Mutations;
import hmvv.io.InternetCommands;
import hmvv.model.CosmicID;
import hmvv.model.MutationSomatic;

public class CosmicInfoPopup {
	static class CosmicInfo{
		public boolean openItem;
		public CosmicID cosmicID;
		public String HGVSc;
		CosmicInfo(CosmicID cosmicID, String HGVSc){
			this.cosmicID = cosmicID;
			this.HGVSc = HGVSc;
		}
	}

	private static ArrayList<CosmicInfo> buildCosmicInfoList(MutationSomatic mutation) throws Exception {
		String HGVSc = mutation.getHGVSc();
		String transcript = "";
		if(HGVSc.startsWith("ENST")) {
			transcript = HGVSc.split("\\.")[0];
		}
		
		 ArrayList<CosmicID> cosmicIDList = mutation.getAllCosmicIDs();
		 ArrayList<CosmicInfo> cosmicInfoList = new ArrayList<CosmicInfo>(cosmicIDList.size());
		 boolean transcriptFound = false;
		 for(CosmicID cosmicID : cosmicIDList) {
			CosmicInfo thisInfo = new CosmicInfo(cosmicID,HGVSc);
			cosmicInfoList.add(thisInfo);
		 	//looking for transcript that matches this mutation
		 	if(cosmicID.getTranscript().equals(transcript)) {
		 		thisInfo.openItem = true;
		 		transcriptFound = true;
		 	}
		 }

		 //Find default cosmicID if no transcript found
		 if(!transcriptFound) {
		 	for(CosmicInfo thisInfo : cosmicInfoList) {
		 		if(thisInfo.cosmicID.getTranscript().equals("")) {
		 			thisInfo.openItem = true;
		 		}
		 	}
		 }

		return cosmicInfoList;
	}
	
	public static void handleCosmicClick(CommonTable parent, MutationSomatic mutation) throws Exception{
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
				return 11;
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
					case 2: return "Gene";
					case 3: return "LegacyID";
					case 4: return "CDS";
					case 5: return "AA";
					case 6: return "HGVSc";
					case 7: return "HGVSp";
					case 8: return "coordinate";
					case 9: return "strand";
					case 10: return "source";
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
					case 1: return comsicInfoList.get(row).cosmicID.cosmicID;
					case 2: return comsicInfoList.get(row).cosmicID.gene;
					case 3: return comsicInfoList.get(row).cosmicID.legacyID;
					case 4: return comsicInfoList.get(row).cosmicID.CDS;
					case 5: return comsicInfoList.get(row).cosmicID.AA;
					case 6: return comsicInfoList.get(row).cosmicID.HGVSc;
					case 7: return comsicInfoList.get(row).cosmicID.HGVSp;
					case 8: return comsicInfoList.get(row).cosmicID.coordinate.getCoordinateAsString();
					case 9: return comsicInfoList.get(row).cosmicID.strand;
					case 10: return comsicInfoList.get(row).cosmicID.source;
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
							case 3: return "Legacy ID";
							case 4: return "CDS annotation";
							case 5: return "Peptide annotation";
							default: return "";
						}
					}
				};
			}
		};
		table.setAutoCreateRowSorter(true);
		
		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setPreferredSize(new Dimension(1200,500));
		int returnValue = JOptionPane.showConfirmDialog(parent, tableScrollPane, "Open CosmicID's in Web Browser? Be sure to use GRCh37 on the COSMIC website.", JOptionPane.YES_NO_OPTION);
		if(returnValue == JOptionPane.OK_OPTION) {
			for(CosmicInfo cosmicInfo : comsicInfoList){
				
				 if(cosmicInfo.openItem) {
					String MutationURL = DatabaseCommands_Mutations.getMutationURL(cosmicInfo.cosmicID,cosmicInfo.HGVSc);
				 	InternetCommands.searchCosmic(MutationURL);
				 }
			}
		}
	}
}

