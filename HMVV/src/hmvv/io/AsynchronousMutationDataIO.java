package hmvv.io;
import java.util.ArrayList;

import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.main.Configurations;
import hmvv.model.CosmicIdentifier;
import hmvv.model.MutationGermline;
import hmvv.model.MutationSomatic;
import hmvv.model.Sample;

public class AsynchronousMutationDataIO {
	
	public static void loadMissingDataAsynchronous(Sample sample, MutationList mutationList, AsynchronousCallback callback){

		if (mutationList.getMutation_type() == Configurations.MUTATION_TYPE.SOMATIC) {
			createExtraMutationDataThread(mutationList, callback, sample);
		} else if (mutationList.getMutation_type() == Configurations.MUTATION_TYPE.GERMLINE){
			createExtraGermlineMutationDataThread(mutationList, callback);
		}
	}
	
	private static void createExtraMutationDataThread(MutationList mutationList, AsynchronousCallback callback, Sample sample){
		Thread missingDataThread = new Thread(new Runnable(){
			@Override
			public void run() {
				callback.disableInputForAsynchronousLoad();
				getDatabaseMutationData(mutationList, callback, sample);
				callback.enableInputAfterAsynchronousLoad();
			}
		});
		missingDataThread.start();
	}
	
	private static void getDatabaseMutationData(MutationList mutationList, AsynchronousCallback callback, Sample sample){
		for(int index = 0; index < mutationList.getMutationCount(); index++) {
			if(callback.isCallbackClosed()){
				return;
			}
			try{

				MutationSomatic mutation = (MutationSomatic)mutationList.getMutation(index);
				getMutationData(mutation, sample);
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
				MutationSomatic mutation = (MutationSomatic)mutationList.getFilteredMutation(index);
				getMutationData(mutation, sample);
				callback.mutationListIndexUpdated(index);
			}catch(Exception e){
				callback.showErrorMessage(e, "Could not load mutation dbs data - filtered.");
			}
		}
	}
	
	public static void getMutationData(MutationSomatic mutation, Sample sample) throws Exception {
		//cosmic

		if (sample.analyzedBy == ""){ // analyzedBy is null for samples before HMVV 4.2
			ArrayList<CosmicIdentifier> cosmicIDs = DatabaseCommands.getLinkedCosmicIDs(mutation);
			mutation.addLinkedCosmicIDs(cosmicIDs);
		}
		
		mutation.removeCosmicIDLoading();
		
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

	private static void createExtraGermlineMutationDataThread(MutationList mutationList, AsynchronousCallback callback){
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

	private static void getDatabaseGermlineMutationData(MutationList mutationList, AsynchronousCallback callback){
		for(int index = 0; index < mutationList.getMutationCount(); index++) {
			if(callback.isCallbackClosed()){
				return;
			}
			try{

				MutationGermline mutation = (MutationGermline)mutationList.getMutation(index);
				getMutationDataGermline(mutation);
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
				MutationGermline mutation = (MutationGermline)mutationList.getFilteredMutation(index);
				getMutationDataGermline(mutation);
				callback.mutationListIndexUpdated(index);
			}catch(Exception e){
				callback.showErrorMessage(e, "Could not load mutation dbs data - filtered.");
			}
		}
	}


	public static void getMutationDataGermline(MutationGermline mutation) throws Exception {

		int count = DatabaseCommands.getOccurrenceCount(mutation);
		mutation.setOccurrence(count);

		//cardiac atlas
		DatabaseCommands.updateGermlineCardiacAtlasInfo(mutation);

		// default selection
		mutation.setSelected(false);
	}
	
}
