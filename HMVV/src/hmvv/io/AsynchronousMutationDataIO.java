package hmvv.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
			createExtraGermlineMutationDataThreadForHGMD(sample,mutationList, callback);
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

		//gnomad
		mutation.setGnomadID();

		//cardiac atlas
		DatabaseCommands.updateGermlineCardiacAtlasInfo(mutation);

		// default selection
		mutation.setSelected(false);
	}

	private static void createExtraGermlineMutationDataThreadForHGMD( Sample sample, MutationList mutationList, AsynchronousCallback callback){
		Thread missingDataThread = new Thread(new Runnable(){
			@Override
			public void run() {
				callback.disableInputForAsynchronousLoad();
				try {
					getDatabaseGermlineMutationHGMDData(sample, mutationList, callback);
				} catch (Exception e) {
					e.printStackTrace();
				}
				callback.enableInputAfterAsynchronousLoad();
			}
		});
		missingDataThread.start();
	}

	private static void getDatabaseGermlineMutationHGMDData(Sample sample, MutationList mutationList, AsynchronousCallback callback) throws Exception {

		//HGMD Database
		ArrayList<String> hgmd_info = SSHConnection.getHGMDInformation(sample);

		for (String line:hgmd_info){
			Map<String, String> row_map = stringtodict(line.replace("{","").replace("}","").replaceAll("'",""));


			for(int index = 0; index < mutationList.getMutationCount(); index++) {
				MutationGermline mutation = (MutationGermline) mutationList.getMutation(index);
//				System.out.println(row_map.get("hgmd_variantid"));
				if (row_map.get("hgmd_variantid").replace(" ","").equals(mutation.getChr()+"-"+mutation.getPos()+"-"+mutation.getRef()+"-"+mutation.getAlt())){
//					System.out.println(row_map.get("variant_id"));
//					System.out.println(mutation.getChr()+"_"+mutation.getPos()+"_"+mutation.getRef()+"_"+mutation.getAlt());
					mutation.setHgmd_id(row_map.get("hgmd_id"));
					mutation.setHgmd_info(row_map);
				}
			}

			for(int index = 0; index < mutationList.getFilteredMutationCount(); index++) {
				MutationGermline mutation = (MutationGermline) mutationList.getFilteredMutation(index);

				if (row_map.get("hgmd_variantid").equals(mutation.getChr()+"_"+mutation.getPos()+"_"+mutation.getRef()+"_"+mutation.getAlt())){
//					System.out.println(row_map.get("variant_id"));
//					System.out.println(mutation.getChr()+"_"+mutation.getPos()+"_"+mutation.getRef()+"_"+mutation.getAlt());
					mutation.setHgmd_id(row_map.get("hgmd_id"));
					mutation.setHgmd_info(row_map);
				}
			}

		}



	}

	public static Map<String, String> stringtodict(String line){

		Map<String, String> row_map = new HashMap<String, String>();

		String[] pairs = line.split(",");

		for (int i=0;i<pairs.length;i++) {
			String pair = pairs[i];
			String[] keyValue = pair.split(":");
			row_map.put(keyValue[0].replace(" ",""), keyValue[1]);
		}


		return row_map;
	}

}
