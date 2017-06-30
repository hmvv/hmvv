package hmvv.gui.mutationlist.tablemodels;

public class HMVVTableModelColumn {
	public final String description;
	public final String title;
	public final Class<?> columnClass;
	
	public HMVVTableModelColumn(String description, String title, Class<?> columnClass) {
		this.description = description;
		this.title = title;
		this.columnClass = columnClass;
	}
}
