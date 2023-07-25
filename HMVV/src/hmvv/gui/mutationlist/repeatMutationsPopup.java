package hmvv.gui.mutationlist;
import java.awt.event.MouseEvent;
import java.awt.*;

import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import hmvv.io.DatabaseCommands;
import hmvv.main.Configurations;
import hmvv.model.MutationSomatic;
import hmvv.model.repeatMutations;

public class repeatMutationsPopup {
	static class CosmicInfo{

		public MutationSomatic mutation;
	

		void repeatMutationsPopup(JDialog parent, MutationSomatic mutation){
			this.mutation = mutation;

		}
	}
	
	public static void handleRepeatMutationsClick(JDialog parent, MutationSomatic mutation) throws Exception{
		ArrayList<repeatMutations> repeatMutationsList = DatabaseCommands.getrepeatMutations(mutation);
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
				return repeatMutationsList.size();
			}

			@Override
			public final String getColumnName(int column) {
				switch(column) {
					case 0: return "sampleID";
					case 1: return "sampleName";
					case 2: return "lastName";
					case 3: return "firstName";
					case 4: return "altFreq";
					case 5: return "readDP";
					case 6: return "altReadDP";
					default: return "";
				}
			}
			
		
			@Override
			public  final Object getValueAt(int row, int column) {
				switch(column) {
					case 0: return repeatMutationsList.get(row).sampleID;
					case 1: return repeatMutationsList.get(row).sampleName;
					case 2: return repeatMutationsList.get(row).lastName;
					case 3: return repeatMutationsList.get(row).firstName;
					case 4: return repeatMutationsList.get(row).altFreq;
					case 5: return repeatMutationsList.get(row).readDP;
					case 6: return repeatMutationsList.get(row).altReadDP;
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

			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(columnModel) {
					private static final long serialVersionUID = 1L;
					
					public String getToolTipText(MouseEvent e) {
						int index = table.columnAtPoint(e.getPoint());
						int realIndex = table.convertColumnIndexToModel(index);
						switch(realIndex) {
							case 0: return "sampleID";
							case 1: return "sampleName";
							case 2: return "lastName";
							case 3: return "firstName";
							case 4: return "altFreq";
							case 5: return "readDP";
							case 6: return "altReadDP";
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
				}
				return c;
			}
		};
		table.setAutoCreateRowSorter(true);

		repeatMutationsFrame repeatMutationsFrame = new repeatMutationsFrame(parent, table, repeatMutationsList);
		repeatMutationsFrame.setVisible(true);

	}
}

