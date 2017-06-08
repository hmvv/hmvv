package hmvv.gui.mutationlist;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.MutationTraceModel;
import hmvv.model.Mutation;

public class MutationTraceFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTable table;
	private MutationTraceModel model;
	
	public MutationTraceFrame(Component parent, ArrayList<Mutation> mutations, String title) {
		super(title);
		this.model = new MutationTraceModel(mutations);
		
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.80), (int)(bounds.height*.60));
				
		constructComponents();
		layoutComponents();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(parent);
	}
	
	private void constructComponents(){
		table = new JTable(){
			private static final long serialVersionUID = 1L;
			
			@Override 
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setModel(model);
	}
	
	private void layoutComponents(){
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(Boolean.class, new BooleanRenderer(false));
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(table);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGap(43)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 1066, Short.MAX_VALUE)
						.addGap(36))
				);
		gl_contentPane.setVerticalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGap(84)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
						.addGap(28))
				);
		contentPane.setLayout(gl_contentPane);
		table.setAutoCreateRowSorter(true);
	}
}
