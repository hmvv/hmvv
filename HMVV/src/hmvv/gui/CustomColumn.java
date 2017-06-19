package hmvv.gui;

import java.awt.Color;
import java.awt.Cursor;

public class CustomColumn{
	
	public static final CustomColumn defaultColumn = new CustomColumn(Cursor.getDefaultCursor(), Color.BLACK);
	public static final CustomColumn clickableColumn = new CustomColumn(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR), Color.BLUE);
	
	public final Cursor cursor;
	public final Color color;
	
	private CustomColumn(Cursor cursor, Color color){
		this.cursor = cursor;
		this.color = color;
	}
	
	public static CustomColumn[] getCustomColumnArray(int totalColumns, int... clickableColumns){
		CustomColumn[] columnCursors = new CustomColumn[totalColumns];
		
		for(int i = 0; i < columnCursors.length; i++){
			columnCursors[i] = CustomColumn.defaultColumn;
		}
		for(int i = 0; i < clickableColumns.length; i++){
			columnCursors[clickableColumns[i]] = CustomColumn.clickableColumn;
		}
		return columnCursors;
	}
	
	
}
