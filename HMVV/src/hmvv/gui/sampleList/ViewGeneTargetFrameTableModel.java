package hmvv.gui.sampleList;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import hmvv.model.GeneTargetQC;
import hmvv.model.Sample;

public class ViewGeneTargetFrameTableModel extends AbstractTableModel{

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private Sample sample;
    private ArrayList<GeneTargetQC> geneTargets;
    private ArrayList<ViewGeneTargetFrameTableModelColumn> columns;

    public ViewGeneTargetFrameTableModel(Sample sample){
        this.geneTargets = new ArrayList<GeneTargetQC>();
        this.sample = sample;
        constructColumns();
    }

    private void constructColumns() {
        String qcColumnHeader = "% Passing";

        columns = new ArrayList<ViewGeneTargetFrameTableModelColumn>();
        
        columns.add(new ViewGeneTargetFrameTableModelColumn("The Gene name",
                "Gene",
                String.class,
                (GeneTargetQC geneTarget) -> geneTarget.gene));

        columns.add(new ViewGeneTargetFrameTableModelColumn("% of Targets Passing Threshold",
                qcColumnHeader,
                Double.class,
                (GeneTargetQC geneTarget) -> geneTarget.getPercentage()));
        
        columns.add(new ViewGeneTargetFrameTableModelColumn("Passing Targets",
                "Passing Targets",
                Integer.class,
                (GeneTargetQC geneTarget) -> geneTarget.targetsPassedQC));

        columns.add(new ViewGeneTargetFrameTableModelColumn("Total Targets",
                "Total Targets",
                Integer.class,
                (GeneTargetQC geneTarget) -> geneTarget.totalTargets));
    }

    public void setGeneTargetQCs(ArrayList<GeneTargetQC> geneTargets){
        this.geneTargets = geneTargets;
        this.fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return columns.get(column).columnClass;
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public int getRowCount() {
        return geneTargets.size();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).title;
    }

    @Override
    public Object getValueAt(int row, int column) {
        GeneTargetQC geneTargetQC = geneTargets.get(row);
        return columns.get(column).getValue(geneTargetQC);
    }
}
