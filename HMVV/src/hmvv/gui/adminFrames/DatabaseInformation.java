package hmvv.gui.adminFrames;
import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.io.SSHConnection;
import hmvv.model.Database;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Color;
import java.util.ArrayList;

public class DatabaseInformation extends JDialog {

    private static final long serialVersionUID = 1L;

    //Table
    private JTable table;
    private DatabaseInformationTableModel tableModel;
    private JScrollPane tableScrollPane;

    private ArrayList<Database> databases;

    public DatabaseInformation(SampleListFrame parent) throws Exception {
        super(parent, "Database Information");

        tableModel = new DatabaseInformationTableModel();
        databases = new ArrayList<Database>();

        Rectangle bounds = GUICommonTools.getBounds(parent);
        setSize((int)(bounds.width*.60), (int)(bounds.height*.2));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        createComponents();
        layoutComponents();
        setLocationRelativeTo(parent);
        buildDatabaseModelFromCSV();
    }

    private void createComponents() {
        table = new JTable(tableModel){
        @Override
        public Component prepareRenderer (TableCellRenderer renderer,int row, int column){
            try {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isCellSelected(row, column)) {
                    c.setBackground(new Color(51, 153, 255));
                }
                return c;
            } catch (Exception e) {
                return null; }
            }
        };

        table.setDefaultRenderer(Object.class, new TableCellRenderer(){
            private DefaultTableCellRenderer DEFAULT_RENDERER =  new DefaultTableCellRenderer();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if((column==3) && (Integer.parseInt(value.toString()) > 24)){
                    c.setBackground(GUICommonTools.ERROR_COLOR);
                }else if((column==3) && (Integer.parseInt(value.toString()) > 11)){
                    c.setBackground(GUICommonTools.RUNNING_COLOR);
                }else if((column==3) && (Integer.parseInt(value.toString()) <= 11)){
                    c.setBackground(GUICommonTools.COMPLETE_COLOR);
                }else{
                    c.setBackground(GUICommonTools.WHITE_COLOR);
                }
                return c;
            }
        });
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        tableScrollPane = new JScrollPane();
        tableScrollPane.setViewportView(table);
        table.getTableHeader().setFont(GUICommonTools.TAHOMA_BOLD_12);
        table.setFont(GUICommonTools.TAHOMA_BOLD_11);
    }

    private void layoutComponents(){
    add(tableScrollPane);
    }

    private void buildDatabaseModelFromCSV() throws Exception {
        databases = SSHConnection.getDatabaseInformation();
        for (Database d: databases){
            tableModel.addDatabase(d);
            d.updateAge();
        }
    }
}
