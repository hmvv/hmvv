package hmvv.io;

import java.util.ArrayList;

import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.gui.mutationlist.tablemodels.MutationListGermline;
import hmvv.gui.mutationlist.tables.GermlineClinVarTable;
import hmvv.model.GermlineMutation;
import hmvv.model.Mutation;

public class AsynchronousMutationDataIO {
	
	public static void loadMissingDataAsynchronous(Object mutationList, AsynchronousCallback callback){

		if (mutationList instanceof MutationList) {
			createExtraMutationDataThread((MutationList)mutationList, callback);
		} else if (mutationList instanceof MutationListGermline){
			createExtraGermlineMutationDataThread((MutationListGermline)mutationList, callback);
		}
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
				callback.showErrorMessage(e, "Could not load mutation dbs data - main.");
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
				callback.showErrorMessage(e, "Could not load mutation dbs data - filtered.");
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

		//pmkb
		DatabaseCommands.updatePmkbInfo(mutation);
		mutation.setPmkbID();

		// default selection
		mutation.setSelected(false);
	}

	private static void createExtraGermlineMutationDataThread(MutationListGermline mutationList, AsynchronousCallback callback){
		Thread missingDataThread = new Thread(new Runnable(){
			@Override
			public void run() {
				callback.disableInputForAsynchronousLoad();
				getDatabaseGermlineMutationData(mutationList, callback);
				callback.enableInputAfterAsynchronousLoad();
			}
		});
		missingDataThread.start();
	}

	private static void getDatabaseGermlineMutationData(MutationListGermline mutationList, AsynchronousCallback callback){
		for(int index = 0; index < mutationList.getMutationCount(); index++) {
			if(callback.isCallbackClosed()){
				return;
			}
			try{

				GermlineMutation mutation = mutationList.getMutation(index);
				getMutationDataGermline(mutation);
				callback.mutationListIndexUpdated(index);
			}catch(Exception e){
				callback.showErrorMessage(e, "Could not load mutation dbs data - main.");
			}
		}

//		for(int index = 0; index < mutationList.getFilteredMutationCount(); index++){
////			if(callback.isCallbackClosed()){
////				return;
////			}
////
////			try{
////				GermlineMutation mutation = mutationList.getFilteredMutation(index);
////				getMutationDataGermline(mutation);
////				callback.mutationListIndexUpdated(index);
////			}catch(Exception e){
////				callback.showErrorMessage(e, "Could not load mutation dbs data - filtered.");
////			}
////		}
	}


	public static void getMutationDataGermline(GermlineMutation mutation) throws Exception {

//		int count = DatabaseCommands.getOccurrenceCount(mutation);
//		mutation.setOccurrence(count);

		//gnomad
		mutation.setGnomad_id();

		//pmkb
		DatabaseCommands.updateGermlineCardiacAtlasInfo(mutation);

		// default selection
		mutation.setSelected(false);
	}
}
