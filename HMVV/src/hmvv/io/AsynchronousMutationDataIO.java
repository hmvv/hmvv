package hmvv.io;

import java.util.ArrayList;

import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.model.Mutation;

public class AsynchronousMutationDataIO {
	
	public static void loadMissingDataAsynchronous(MutationList mutationList, AsynchronousCallback callback){
		createExtraMutationDataThread(mutationList, callback);
	}
	
	private static void createExtraMutationDataThread(MutationList mutationList, AsynchronousCallback callback){
		Thread missingDataThread = new Thread(new Runnable(){
			@Override
			public void run() {
				callback.disableInputForAsynchronousLoad();
				getDatabaseMutationData(mutationList, callback);
				callback.enableInputAfterAsynchronousLoad();
			}
		});
		missingDataThread.start();
	}
	
	private static void getDatabaseMutationData(MutationList mutationList, AsynchronousCallback callback){
		for(int index = 0; index < mutationList.getMutationCount(); index++) {
			if(callback.isCallbackClosed()){
				return;
			}
			try{

				Mutation mutation = mutationList.getMutation(index);
				getMutationData(mutation);
				callback.mutationListIndexUpdated(index);
			}catch(Exception e){
				callback.showErrorMessage(e.getMessage() + " : " + e.getClass().getName() + ": Could not load mutation dbs data - main.");
			}
		}
		
		for(int index = 0; index < mutationList.getFilteredMutationCount(); index++){
			if(callback.isCallbackClosed()){
				return;
			}
			
			try{
				Mutation mutation = mutationList.getFilteredMutation(index);
				getMutationData(mutation);
				callback.mutationListIndexUpdated(index);
			}catch(Exception e){
				callback.showErrorMessage(e.getMessage() + " : " + e.getClass().getName() + ": Could not load mutation dbs data - filtered.");
			}
		}
	}
	
	public static void getMutationData(Mutation mutation) throws Exception {
		//cosmic
		ArrayList<String> cosmicIDs = DatabaseCommands.getCosmicIDs(mutation);
		mutation.setCosmicID(cosmicIDs);

		int count = DatabaseCommands.getOccurrenceCount(mutation);
		mutation.setOccurrence(count);
		
		//gnomad
		mutation.setGnomadID();

		//oncokb
		DatabaseCommands.updateOncokbInfo(mutation);
		mutation.setOncokbID();

		//civic
		DatabaseCommands.updateCivicInfo(mutation);
		mutation.setCivicID();
	}
}
