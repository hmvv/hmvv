package hmvv.gui.sampleList;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import hmvv.model.Amplicon;
import hmvv.model.Sample;

public class ViewAmpliconFrameTableModel extends AbstractTableModel{

    private static final long serialVersionUID = 1L;

    private Sample sample;
    private ArrayList<Amplicon> amplicons;
    private ArrayList<ViewAmpliconFrameTableModelColumn> columns;

    public ViewAmpliconFrameTableModel(Sample sample){
        this.amplicons = new ArrayList<Amplicon>();
        this.sample = sample;
        constructColumns();
    }

    private void constructColumns() {
        String qcColumnHeader = Amplicon.getQCColumnName(sample);

        columns = new ArrayList<ViewAmpliconFrameTableModelColumn>();
        
        columns.add(new ViewAmpliconFrameTableModelColumn("The sample ID",
                "sampleID",
                int.class,
                (Amplicon amplicon) -> amplicon.sample.sampleID));

        columns.add(new ViewAmpliconFrameTableModelColumn("The amplicon Name",
                "ampliconName",
                String.class,
                (Amplicon amplicon) -> amplicon.ampliconName));

        columns.add(new ViewAmpliconFrameTableModelColumn("The amplicon QC measure",
                qcColumnHeader,
                Integer.class,
                (Amplicon amplicon) -> amplicon.getQCMeasure()));
        
    }

    public void setAmplicons(ArrayList<Amplicon> amplicons){
        this.amplicons = amplicons;
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
        return amplicons.size();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).title;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Amplicon amplicon = amplicons.get(row);
        return columns.get(column).getValue(amplicon);
    }

}