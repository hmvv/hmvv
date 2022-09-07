package hmvv.io;
import java.util.TreeSet;

import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.main.Configurations;
import hmvv.model.MutationGermline;
import hmvv.model.MutationSomatic;
import hmvv.model.Sample;

public class AsynchronousMutationDataIO {
	
	public static void loadMissingDataAsynchronous(Sample sample, MutationList mutationList, AsynchronousCallback callback){

		if (mutationList.getMutation_type() == Configurations.MUTATION_TYPE.SOMATIC) {
			createExtraMutationDataThread(mutationList, callback);
		} else if (mutationList.getMutation_type() == Configurations.MUTATION_TYPE.GERMLINE){
			createExtraGermlineMutationDataThread(mutationList, callback);
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

				MutationSomatic mutation = (MutationSomatic)mutationList.getMutation(index);
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
				MutationSomatic mutation = (MutationSomatic)mutationList.getFilteredMutation(index);
				getMutationData(mutation);
				callback.mutationListIndexUpdated(index);
			}catch(Exception e){
				callback.showErrorMessage(e, "Could not load mutation dbs data - filtered.");
			}
		}
	}
	
	public static void getMutationData(MutationSomatic mutation) throws Exception {
		//cosmic
		TreeSet<String> cosmicIDs = DatabaseCommands.getCosmicIDs(mutation);
		mutation.addCosmicIDs(cosmicIDs);
		mutation.removeCosmicIDLoading();
		
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
