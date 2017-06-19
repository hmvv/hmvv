package hmvv.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;

public class BooleanRenderer extends JCheckBox implements TableCellRenderer, UIResource {

	private static final long serialVersionUID = 1L;
	private final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	
	public BooleanRenderer() {
		super();
		setHorizontalAlignment(JLabel.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if(column == 0){
			setBackground(Color.white);
		}
		if (isSelected) {
			setForeground(table.getSelectionForeground());
		} else {
			setForeground(table.getForeground());
		}
		setSelected(value != null && ((Boolean) value).booleanValue());
		if (hasFocus) {
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
		} else {
			setBorder(noFocusBorder);
		}
		return this;
	}

}
