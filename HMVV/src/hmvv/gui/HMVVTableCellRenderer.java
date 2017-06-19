package hmvv.gui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class HMVVTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	private final CustomColumn[] customColumns;
	
	public HMVVTableCellRenderer(CustomColumn[] customColumns){
		this.customColumns = customColumns;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		c.setForeground(customColumns[column].color);
		return c;
	}
}
