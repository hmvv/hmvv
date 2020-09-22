package hmvv.gui.mutationlist;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.GermlineHGMDGeneLevelMutationsTableModel;
import hmvv.gui.mutationlist.tablemodels.GermlineHGMDGeneLevelSummaryTableModel;
import hmvv.io.DatabaseCommands;
import hmvv.io.DatabaseCommands_HGMD;
import hmvv.main.HMVVFrame;
import hmvv.model.MutationGermline;
import hmvv.model.MutationGermlineHGMD;
import hmvv.model.MutationGermlineHGMDGeneLevel;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;


public class MutationGermlineHGMDGeneFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public final HMVVFrame parent;
	private MutationGermline mutation;

	private GermlineHGMDGeneLevelSummaryTableModel summaryTableModel;
	private JTable summaryTable;
	private JScrollPane summaryScrollPane;
	private TableRowSorter<GermlineHGMDGeneLevelSummaryTableModel> summarySorter;


	private GermlineHGMDGeneLevelMutationsTableModel mutationTableModel;
	private JTable mutationTable;
	private JScrollPane mutationScrollPane;
	private TableRowSorter<GermlineHGMDGeneLevelMutationsTableModel> mutationsSorter;

	public MutationGermlineHGMDGeneFrame(HMVVFrame parent, MutationGermline mutation) throws Exception {
		String title = "HGMD: "+ mutation.getGene();
		setTitle(title);

		this.parent = parent;
		this.mutation = mutation;

		constructComponents();
		layoutComponents();


//		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.50), (int)(bounds.height*.50));
		setMinimumSize(new Dimension(500, getHeight()/2));

		setLocationRelativeTo(parent);
		setAlwaysOnTop(false);


	}

	
	private void constructComponents() throws Exception {

		ArrayList<MutationGermlineHGMDGeneLevel> genelevel_mutations_summary = DatabaseCommands.getMutationSummaryForGene(mutation.getGene());
		summaryTableModel = new GermlineHGMDGeneLevelSummaryTableModel(genelevel_mutations_summary);
		summaryTable = new JTable(summaryTableModel);
		summaryTable.setAutoCreateRowSorter(true);
		summaryTable.setRowSorter(summarySorter);


		ArrayList<MutationGermlineHGMD> mutations = DatabaseCommands.getMutationsByTable(mutation,"db_hgmd_mutation");
		mutationTableModel = new GermlineHGMDGeneLevelMutationsTableModel(mutations);
		mutationTable = new JTable(mutationTableModel);
		mutationTable.setAutoCreateRowSorter(true);
		mutationTable.setRowSorter(mutationsSorter);

	}

	
	private void layoutComponents() throws Exception {

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		summaryScrollPane = new JScrollPane(summaryTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		summaryScrollPane.setViewportView(summaryTable);
		summaryScrollPane.setPreferredSize(new Dimension(300,150));

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(summaryScrollPane);
		leftPanel.setBorder(BorderFactory.createEmptyBorder(100, 50, 140, 50));


		mutationScrollPane = new JScrollPane(mutationTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mutationScrollPane.setViewportView(mutationTable);
//		summaryScrollPane.setPreferredSize(new Dimension(1000,1200));

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(mutationScrollPane);
		rightPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		mainPanel.add(leftPanel,BorderLayout.WEST);
		mainPanel.add(rightPanel,BorderLayout.EAST);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(1, 10, 1, 1));

		setLayout(new BorderLayout());
		add(mainPanel);


	}

}

