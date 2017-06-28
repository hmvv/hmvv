package hmvv.gui.mutationlist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.gui.mutationlist.tablemodels.MutationTraceModel;
import hmvv.gui.mutationlist.tables.MutationTraceTable;

public class MutationTraceFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private MutationTraceTable table;
	private MutationTraceModel model;
	
	public MutationTraceFrame(MutationListFrame parent, MutationList mutationList, String title) {
		super(title);
		this.model = new MutationTraceModel(mutationList);
		table = new MutationTraceTable(parent, model);
		table.setModel(model);
		
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.85), (int)(bounds.height*.60));
		
		layoutComponents();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(parent);
	}
	
	private void layoutComponents(){
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		
		JScrollPane scrollPane = new JScrollPane(table);
		
		BorderFactory.createLineBorder(Color.black, 5, true);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
		
		JPanel contentPanel = new JPanel();
		int width = 15;
		EmptyBorder emptyBorder = new EmptyBorder(width, width, width, width);
		contentPanel.setBorder(emptyBorder);
		
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		setContentPane(contentPanel);
	}
}
