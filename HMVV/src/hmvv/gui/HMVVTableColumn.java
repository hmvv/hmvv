package hmvv.gui;

import java.awt.Color;
import java.awt.Cursor;

public class HMVVTableColumn{
	
	public static final HMVVTableColumn defaultColumn = new HMVVTableColumn(Cursor.getDefaultCursor(), Color.BLACK);
	public static final HMVVTableColumn clickableColumn = new HMVVTableColumn(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR), Color.BLUE);
	
	public final Cursor cursor;
	public final Color color;
	
	private HMVVTableColumn(Cursor cursor, Color color){
		this.cursor = cursor;
		this.color = color;
	}
	
	public static HMVVTableColumn[] getCustomColumnArray(int totalColumns, int... clickableColumns){
		HMVVTableColumn[] columnCursors = new HMVVTableColumn[totalColumns];
		
		for(int i = 0; i < columnCursors.length; i++){
			columnCursors[i] = HMVVTableColumn.defaultColumn;
		}
		for(int i = 0; i < clickableColumns.length; i++){
			columnCursors[clickableColumns[i]] = HMVVTableColumn.clickableColumn;
		}
		return columnCursors;
	}
}
