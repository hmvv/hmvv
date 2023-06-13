package hmvv.gui.mutationlist;
import java.awt.event.MouseEvent;
import java.awt.Component;

import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import hmvv.io.DatabaseCommands;
import hmvv.main.Configurations;
import hmvv.model.CosmicID;
import hmvv.model.CosmicIdentifier;
import hmvv.model.MutationSomatic;

public class CosmicInfoPopup {

	static class CosmicInfoList{
		ArrayList<CosmicInfo> cosmicInfoList;
		boolean exactMatchFound;

		CosmicInfoList(ArrayList<CosmicInfo> cosmicInfoList, boolean exactMatchFound){
			this.cosmicInfoList = cosmicInfoList;
			this.exactMatchFound = exactMatchFound;
		}
	}
	static class CosmicInfo{
		public boolean openItem;
		public CosmicID cosmicID;
		public Boolean shade;

		CosmicInfo(Boolean openItem, CosmicID cosmicID, Boolean shade){
			this.openItem = openItem;
			this.cosmicID = cosmicID;
			this.shade = shade;
		}
	}

	private static CosmicInfoList buildCosmicInfoList(MutationSomatic mutation) throws Exception {
		 ArrayList<CosmicIdentifier> cosmicIDList = mutation.getAllCosmicIDs();
		 ArrayList<CosmicInfo> cosmicInfoListAll = new ArrayList<CosmicInfo>(cosmicIDList.size());
		 ArrayList<CosmicInfo> cosmicInfoListExactMatch = new ArrayList<CosmicInfo>(cosmicIDList.size());
		 ArrayList<CosmicID> cosmicIDListWithMetadata = new ArrayList<CosmicID>(cosmicIDList.size());

		 int white_count = 0;
		for(CosmicIdentifier cosmicID : cosmicIDList) {
			ArrayList<CosmicID> cosmicIDInfoList = DatabaseCommands.getCosmicIDInfo(cosmicID);
			cosmicIDListWithMetadata.addAll(cosmicIDInfoList);
			for(CosmicID thisCosmicID : cosmicIDInfoList ){
				Boolean shade = true;
				Boolean openItem = false;
				CosmicInfo thisInfo = new CosmicInfo(openItem,thisCosmicID, shade);

				String HGVSc = mutation.getHGVSc();	
				String mut_transcript = HGVSc.split(":")[0];
				String cos_transcript = thisCosmicID.HGVSc.split(":")[0];	
				String cos_dna_change = thisCosmicID.HGVSc.split(":")[1];
				String mut_dna_change = HGVSc.split(":")[1];

				if (cos_dna_change.equals(mut_dna_change)){
					thisInfo.shade = false;
					if (cos_transcript.equals(mut_transcript)){
						thisInfo.openItem = true;
					}
				}
				cosmicInfoListAll.add(thisInfo);
		 	}
		}
		
		for(CosmicInfo thisInfo : cosmicInfoListAll) {
			if (thisInfo.shade == false){
				white_count++;
				cosmicInfoListExactMatch.add(thisInfo);
			}
		}
		 

		if(white_count == 0){
			return new CosmicInfoList(cosmicInfoListAll,false);
		}else{
			return new CosmicInfoList(cosmicInfoListExactMatch,true);

		}
	}
	
	public static void handleCosmicClick(JDialog parent, MutationSomatic mutation) throws Exception{
		CosmicInfoList cosmicInfoList = buildCosmicInfoList(mutation);
		ArrayList<CosmicInfo> cosmicInfoSet = cosmicInfoList.cosmicInfoList;
		
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
				return cosmicInfoSet.size();
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
					cosmicInfoSet.get(row).openItem = (Boolean) aValue;
				}
			}
			
			@Override
			public  final Object getValueAt(int row, int column) {
				switch(column) {
					case 0: return cosmicInfoSet.get(row).openItem;
					case 1: return cosmicInfoSet.get(row).cosmicID.cosmicID;
					case 2: return cosmicInfoSet.get(row).cosmicID.gene;
					case 3: return cosmicInfoSet.get(row).cosmicID.legacyID;
					case 4: return cosmicInfoSet.get(row).cosmicID.CDS;
					case 5: return cosmicInfoSet.get(row).cosmicID.AA;
					case 6: return cosmicInfoSet.get(row).cosmicID.HGVSc;
					case 7: return cosmicInfoSet.get(row).cosmicID.HGVSp;
					case 8: return cosmicInfoSet.get(row).cosmicID.coordinate.getCoordinateAsString();
					case 9: return cosmicInfoSet.get(row).cosmicID.strand;
					case 10: return cosmicInfoSet.get(row).cosmicID.source;
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

			
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component c = super.prepareRenderer(renderer, row, column);
				if (isCellSelected(row, column)){
					c.setForeground(Configurations.TABLE_SELECTION_FONT_COLOR);
					c.setBackground(Configurations.TABLE_SELECTION_COLOR);
				}else {
					c.setForeground(Configurations.TABLE_SELECTION_FONT_COLOR);

					CosmicInfo thisCosmicInfo = cosmicInfoSet.get(this.convertRowIndexToModel(row));
					//mutation.getHGVSc()

					if(thisCosmicInfo.shade){
						c.setBackground(Configurations.TABLE_UNMATCHED_COSMIC_COLOR); //gray
					}else{
						c.setBackground(Configurations.TABLE_MATCHED_COSMIC_COLOR); //white
					}

				}
				return c;
			}
		};
		table.setAutoCreateRowSorter(true);

		CosmicInfoFrame CosmicInfoFrame = new CosmicInfoFrame(parent, table, cosmicInfoSet, cosmicInfoList.exactMatchFound);
		CosmicInfoFrame.setVisible(true);

	}
}

