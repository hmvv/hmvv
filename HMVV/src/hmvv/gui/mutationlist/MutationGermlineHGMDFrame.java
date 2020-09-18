package hmvv.gui.mutationlist;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.HGMDDatabaseInformationTableModel;
import hmvv.io.DatabaseCommands;
import hmvv.main.HMVVFrame;
import hmvv.model.MutationHGMD;
import hmvv.model.MutationGermline;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MutationGermlineHGMDFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public final HMVVFrame parent;

	private MutationGermline mutation;

	Map<String, HGMDDatabaseInformationTableModel > hgmdTableModels = new HashMap<>();
	Map<String,JTable> hgmdTables = new HashMap<>();
	Map<String,JScrollPane> hgmdTableScrollpanes = new HashMap<>();

	private JTabbedPane mutationTabbedPane;
	private JTabbedPane geneTabbedPane;
	private JTable selectedTable;
	private JScrollPane selectedScrollPane;


	public MutationGermlineHGMDFrame(HMVVFrame parent, MutationGermline mutation) throws Exception {
		String title = "HGMD:"+mutation.getHgmd_id()+","+mutation.getChr()+", ref:"+mutation.getRef()+
				", alt:"+mutation.getAlt();
		setTitle(title);

		this.parent = parent;
		this.mutation = mutation;
		
		constructComponents();
		layoutComponents();


//		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.60), (int)(bounds.height*.50));
		setMinimumSize(new Dimension(500, getHeight()/3));

		setLocationRelativeTo(parent);
		setAlwaysOnTop(false);


	}

	
	private void constructComponents(){

		Map<String, String> hgmdInfo = mutation.getHgmd_info();

		for(String attribute : hgmdInfo.keySet()){
			String tableName = attribute.split("_")[0];
			if(hgmdTables.containsKey(tableName)){
				continue;
			}
			HGMDDatabaseInformationTableModel new_table_model = new HGMDDatabaseInformationTableModel();
			JTable new_table = new JTable(new_table_model);
			JScrollPane new_tabscrollpane =  new JScrollPane(new_table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			hgmdTableModels.put(tableName,new_table_model);
			hgmdTables.put(tableName,new_table);
			hgmdTableScrollpanes.put(tableName,new_tabscrollpane);
		}

		updateHGMDMutationTableData();

	}

	private void updateHGMDMutationTableData(){
		Map<String, String> hgmdInfo = mutation.getHgmd_info();
		for(String attribute : hgmdInfo.keySet()){
			String tableName = attribute.split("_")[0];
			String columnName = attribute.split("_")[1];
			String columnValue = hgmdInfo.get(attribute);
			hgmdTableModels.get(tableName).addHGMDEntry(new MutationHGMD(columnName,columnValue));
		}
	}

//	private void updateHGMDGeneTableData() throws Exception {
//
//	}
	
	private void layoutComponents() throws Exception {

		//add all tabs
		mutationTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		for( Map.Entry<String,JScrollPane> tab: hgmdTableScrollpanes.entrySet()){

			mutationTabbedPane.addTab(tab.getKey(), null, tab.getValue(), null);
		}


		//align center
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		for( Map.Entry<String,JTable> table: hgmdTables.entrySet()) {

			((DefaultTableCellRenderer)table.getValue().getTableHeader().getDefaultRenderer())
					.setHorizontalAlignment(JLabel.CENTER);

			table.getValue().getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
			table.getValue().getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

//			table.getValue().setFont(GUICommonTools.TAHOMA_BOLD_12);
//			table.getValue().getTableHeader().setFont(GUICommonTools.TAHOMA_BOLD_13);
		}

		geneTabbedPane = new JTabbedPane(JTabbedPane.TOP);

		HGMDDatabaseInformationTableModel new_table_model = new HGMDDatabaseInformationTableModel();
		JTable new_table = new JTable(new_table_model);
		JScrollPane new_tabscrollpane =  new JScrollPane(new_table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		ArrayList<MutationHGMD> mutations = DatabaseCommands.getAllMutationForGene(mutation.getGene());
		for(MutationHGMD entry : mutations){
			new_table_model.addHGMDEntry(new MutationHGMD(entry.getFeature(),entry.getValue()));
		}

		geneTabbedPane.addTab("Gene:"+mutation.getGene(), null, new_tabscrollpane, null);

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.add(mutationTabbedPane, BorderLayout.CENTER);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		southPanel.add(geneTabbedPane, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(northPanel, BorderLayout.WEST);
		add(southPanel, BorderLayout.CENTER);

		southPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
		northPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

	}

}

