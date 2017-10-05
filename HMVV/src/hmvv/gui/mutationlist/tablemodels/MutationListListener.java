package hmvv.gui.mutationlist.tablemodels;

public interface MutationListListener {
	public void mutationReportedStatusChanged(int index);
	public void mutationUpdated(int index);
	public void mutationListStructureChanged();
	public void mutationDataChanged();
}
