package hmvv.io;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.*;
import com.jcraft.jsch.*;
import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.ServerWorker;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.main.Configurations;
import hmvv.main.Configurations.USER_TYPE;
import hmvv.main.Configurations.USER_FUNCTION;
import hmvv.model.*;

public class SSHConnection {
	
	private static Session sshSession;
	private static USER_TYPE userType;
	private static int forwardingPort;
	private static String temporaryHMVVDirectory = "temp_HMVV_files";
	
	
	private SSHConnection(){
		//never constructed
	}
	
	public static Session getSSHSession(){
		return sshSession;
	}
	
	public static int getForwardingPort(){
		return forwardingPort;
	}
	
	public static String getForwardingHost(){
		return Configurations.SSH_FORWARDING_HOST;
	}
	
	public static String getUserName(){
		return sshSession.getUserName();
	}

	public static void setUserType(USER_TYPE usertype){
		userType = usertype;
	}
	
	public static boolean isSuperUser(USER_FUNCTION function){
		return function.isSuperUser(userType);
	}
	
	public static void connect(String user, String password) throws Exception{
		sshSession = connectToSSH(user, password);
	}
	
	private static Session connectToSSH(String userName, String password) throws Exception{
		JSch jsch = new JSch();
		Session sshSession = jsch.getSession(userName, Configurations.SSH_SERVER_ADDRESS, Configurations.SSH_PORT);
		sshSession.setPassword(password);
		sshSession.setConfig("StrictHostKeyChecking", "no");
		sshSession.connect();
		forwardingPort = sshSession.setPortForwardingL(0, getForwardingHost(), Configurations.DATABASE_PORT);
		return sshSession;
	}
	

	
	private static File copyFile(Sample sample, String file_location, SftpProgressMonitor progressMonitor) throws Exception{
		String runFolderName = sample.runFolder.runFolderName;
		String instrumentName =  sample.instrument.instrumentName;
		String sampleName =  sample.sampleName;

		String localSampleFolder = temporaryHMVVDirectory + File.separator + instrumentName + File.separator + runFolderName + File.separator + sampleName;
		new File(localSampleFolder).mkdirs();
		
		String fileName = new File(file_location).getName().trim();
		String filePath = new File(file_location).getParentFile().getPath().replace("\\", "/") + "/";
		
		File newSubPath = new File(localSampleFolder + File.separator + fileName).getAbsoluteFile();
		if(newSubPath.exists()) {
			if (progressMonitor != null){
				progressMonitor.end();
			}
			//file already exists on local disk
			return newSubPath;
		}
	    
		ChannelSftp channel = (ChannelSftp) sshSession.openChannel("sftp");
		channel.connect();
		channel.cd(filePath);
		channel.get(fileName, newSubPath.getAbsolutePath(), progressMonitor);
		if(fileName.endsWith("bam")){
			channel.get(fileName + ".bai", newSubPath.getAbsolutePath() + ".bai");
		}
		channel.exit();
	    
		return newSubPath;
	}

	public static File loadHemeQCFile(Sample sample) throws Exception{
		RunFolder runFolder = sample.runFolder;
		String sampleName = sample.sampleName;
		Instrument instrument =  sample.instrument;
		String fileName = "trusight-myeloid-amplicon-track.all.strands_distribution.pdf";

		String qcFile = String.format("/storage/analysis/environments/%s/%sAnalysis/hemeAssay/%s/%s/QualityControl/Figures/%s", Configurations.getEnvironment(), instrument, runFolder, sampleName, fileName);
		return copyFile(sample, qcFile, null);
	}
	
	public static File loadBAMForIGV(Sample sample, SftpProgressMonitor progressMonitor) throws Exception {
		String instrumentName =  sample.instrument.instrumentName;

		if(instrumentName.equals("proton")){
			return SSHConnection.loadProton_BAM(sample, progressMonitor);
		}else if( instrumentName.equals("nextseq") || instrumentName.equals("novaseq") ){
			return SSHConnection.loadIllumina_BAM(sample, progressMonitor);
		}else{
			throw new IllegalArgumentException(instrumentName + " is not supported");
		}
	}

	private static File loadProton_BAM(Sample sample, SftpProgressMonitor progressMonitor) throws Exception{
		String runFolder = sample.runFolder.runFolderName;
		String sampleName = sample.sampleName;
		String callerID = sample.callerID;

		String bamFile = String.format("ls /storage/instruments/proton/%s/plugin_out/%s/%s_rawlib.realigned.bam", runFolder, callerID, sampleName);
		return copyFile(sample, bamFile, progressMonitor);
	}

	private static File loadIllumina_BAM(Sample sample, SftpProgressMonitor progressMonitor) throws Exception{
		String instrument = sample.instrument.instrumentName;
		String runFolder = sample.runFolder.runFolderName;
		String sampleName = sample.sampleName;
		String bamFile = String.format("ls /storage/analysis/environments/%s/%sAnalysis/%s/%s/bam/%s*.sort.bam", Configurations.getEnvironment(), instrument, runFolder, sampleName, sampleName);
		return copyFile(sample, bamFile, progressMonitor);
	}
	
	public static ArrayList<String> getCandidateCoverageIDs(Instrument instrument, RunFolder runFolder) throws Exception {
		String coverageCommand = String.format("ls /storage/instruments/%s/%s/plugin_out/ | grep coverageAnalysis", instrument, runFolder);
		CommandResponse coverageResult = SSHConnection.executeCommandAndGetOutput(coverageCommand);
		if(coverageResult.exitStatus != 0) {
			throw new Exception(String.format("Error finding samples (%s, %s)", instrument, runFolder));
		}
		return coverageResult.responseLines;
	}
	
	public static ArrayList<String> getCandidateVariantCallerIDs(Instrument instrument, RunFolder runFolder) throws Exception {
		String variantCallerCommand = String.format("ls /storage/instruments/%s/%s/plugin_out/ | grep variantCaller_out", instrument, runFolder);
		CommandResponse variantCallerResult = SSHConnection.executeCommandAndGetOutput(variantCallerCommand);
		if(variantCallerResult.exitStatus != 0) {
			throw new Exception(String.format("Error finding samples (%s, %s)", instrument, runFolder));
		}
		return variantCallerResult.responseLines;
	}

	public static boolean checkProtonCopyComplete(Instrument instrument, RunFolder runFolder) throws Exception {
		String checkCommand = String.format("ls /storage/instruments/%s/%s/CopyComplete.txt ", instrument, runFolder);
		CommandResponse rs = SSHConnection.executeCommandAndGetOutput(checkCommand);

		if (rs.exitStatus != 0) {
			throw new Exception(String.format("Data transfer is in progress. Please try again later."));
		}
		boolean status = false;
		for (String line : rs.responseLines) {
			if (line.contains("CopyComplete")) {
				status = true;
			}
		}
		return status;
	}
	
	public static ArrayList<String> getSampleListIon(Instrument instrument, RunFolder runFolder, String variantCallerID) throws Exception {
		String sampleListCommand = String.format("ls /storage/instruments/%s/%s/plugin_out/%s/ -F | grep / | grep Ion", instrument, runFolder, variantCallerID);
		CommandResponse sampleListResult = SSHConnection.executeCommandAndGetOutput(sampleListCommand);
		if(sampleListResult.exitStatus != 0) {
			throw new Exception(String.format("Error finding samples (%s, %s, %s)", instrument, runFolder, variantCallerID));
		}
		parseSampleListIon(sampleListResult.responseLines);
		return sampleListResult.responseLines;
	}
	
	private static void parseSampleListIon(ArrayList<String> responseLines){
		responseLines.sort(new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareTo(arg1);
			}
		});
		for(int i =0; i < responseLines.size(); i++){
			responseLines.set(i, responseLines.get(i).replaceAll("/", ""));
		}
	}

	public static void checkSampleSheetError(Instrument instrument, String runFolder, String assay) throws Exception {
		String integrityScript = "/storage/apps/pipelines/" + Configurations.getEnvironment() + "/scripts/common/python/sampleSheet_integrity_check.py";
		String checkSampleSheetCommand = String.format("python3 %s /storage/instruments/%s/%s/SampleSheet.csv %s", integrityScript, instrument, runFolder, assay);
		CommandResponse sampleSheetIntegrityResult = SSHConnection.executeCommandAndGetOutput(checkSampleSheetCommand);
		if(sampleSheetIntegrityResult.responseLines.get(0).toUpperCase().contains("ERROR")){
			
			String sampleSheetErrorString = "";
			for(int i =0; i < sampleSheetIntegrityResult.responseLines.size(); i++){
				sampleSheetErrorString += sampleSheetIntegrityResult.responseLines.get(i) + "\n";
			};
			throw new IllegalArgumentException(sampleSheetErrorString);
		}
	}

	public static RunFolder getRunFolderIllumina(Instrument instrument, String runID) throws Exception {
		String runFolderCommand = String.format("ls /storage/instruments/%s -t | grep _%s_ | head -1", instrument, runID);
		CommandResponse runFolderResult = SSHConnection.executeCommandAndGetOutput(runFolderCommand);
		if(runFolderResult.exitStatus != 0) {
			throw new Exception(String.format("Error finding Run Folder (%s, %s)", instrument, runID));
		}
		return new RunFolder(runFolderResult.responseLines.get(0));
	}

	public static RunFolder getRunFolderIon(Instrument instrument, String runID) throws Exception {
		String runFolderCommand = String.format("ls /storage/instruments/%s -t | grep _%s | head -1", instrument, runID);
		CommandResponse runFolderResult = SSHConnection.executeCommandAndGetOutput(runFolderCommand);
		if(runFolderResult.exitStatus != 0) {
			throw new Exception(String.format("Error finding Run Folder (%s, %s)", instrument, runID));
		}
		return new RunFolder(runFolderResult.responseLines.get(0));
	}

	
	public static ArrayList<String> getSampleListIllumina(Instrument instrument, RunFolder runFolder) throws Exception {
		String sampleListCommand = String.format("cat /storage/instruments/%s/%s/SampleSheet.csv", instrument, runFolder);
		CommandResponse sampleListResult = SSHConnection.executeCommandAndGetOutput(sampleListCommand);
		if(sampleListResult.exitStatus != 0) {
			throw new Exception(String.format("Error finding samples (%s, %s)", instrument, runFolder));
		}
		parseSampleListIllumnina(sampleListResult.responseLines);
		return sampleListResult.responseLines;
	}
	
	private static void parseSampleListIllumnina(ArrayList<String> responseLines){
		ArrayList<String> sampleList = new ArrayList<String>();		
		boolean samplesFound = false;
		for(int i = 0; i < responseLines.size(); i++){
			String responseLine = responseLines.get(i);
			String[] responseLineArray = responseLine.split(",");
			if(responseLineArray == null || responseLineArray.length == 0) {
				continue;
			}
			String fileName = responseLineArray[0];
			if(fileName.equals("Sample_ID")) {
				samplesFound = true;
				continue;
			}
			if(!samplesFound) {
				continue;
			}
			sampleList.add(fileName);
		}
		sampleList.sort(new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareTo(arg1);
			}
		});
		responseLines.clear();
		responseLines.addAll(sampleList);
	}
	
	private static CommandResponse executeCommandAndGetOutput(String command) throws Exception{
		StringBuilder result = new StringBuilder();
		ChannelExec channel = null;
		
		try{
			channel = (ChannelExec) sshSession.openChannel("exec");
			channel.setCommand(command);
			channel.setInputStream(null);
			InputStream stdout = channel.getInputStream();
			channel.connect();
			
			//Read output line by line
			byte[] tmp = new byte[1024];
			while (true){ 
				while (stdout.available() > 0){
					int i = stdout.read(tmp, 0, 1024);
					if (i < 0)
						break;
					String thisLine = new String(tmp, 0, i);
					result.append(thisLine);
				}
				if (channel.isClosed()){
					break;
				}
			}
			
			ArrayList<String> responseLines = new ArrayList<String>();
			for(String s : result.toString().split("\\r?\\n")) {
				responseLines.add(s);
			}
			return new CommandResponse(responseLines, channel.getExitStatus());
		}finally {
			if (channel != null) {
				channel.disconnect();
			}
		}
	}
	
	public static void shutdown() {
		sshSession.disconnect();
		File tempBAMDir = new File(temporaryHMVVDirectory);
		if(!tempBAMDir.exists()) {
			return;
		}
		
		for(File instrumentFolder : tempBAMDir.listFiles()) {
			if(instrumentFolder.isDirectory()) {
				for(File runIDFolder : instrumentFolder.listFiles()) {
					if(runIDFolder.isDirectory()) {
						for(File bamFile : runIDFolder.listFiles()) {
							if(bamFile.isFile() && bamFile.getName().toLowerCase().endsWith("bam") || bamFile.getName().toLowerCase().endsWith(".bam.bai")) {
								deleteFile(bamFile);
							}
						}
						deleteFile(runIDFolder);
					}
				}
				deleteFile(instrumentFolder);
			}
		}
	}
	
	private static void deleteFile(File file) {
		try {
			file.delete();
		}catch(Exception e) {
			//Ignore this. The user may have the temp folder or the file open.
		}
	}

	public static String createTempParametersFile(Sample sample, MutationList mutationList, JButton loadIGVButton, ServerWorker serverWorker) throws Exception {
	    //create a local temp file
		int bamCoverage = 25;
        File tempFile = File.createTempFile(sample.sampleName+"_"+sample.runID+"_",".params");
        BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
        bw.write(Configurations.getEnvironment()+';'+sample.instrument + ';' + sample.runFolder + ';' + sample.assay + ';' +sample.sampleName+';'+ sample.callerID + ';' + sample.coverageID);
        bw.newLine();

        ArrayList<MutationCommon> selectedMutations = mutationList.getSelectedMutations();

        // verify chr/position pair is unique
        ArrayList<Coordinate> selectedCoordinates = new ArrayList<>();

        for (int index = 0; index < selectedMutations.size(); index++) {

            MutationCommon mutation = selectedMutations.get(index);

            if (selectedCoordinates.isEmpty()){
				selectedCoordinates.add(new Coordinate(mutation.getChr(),mutation.getPos(),mutation.getRef(),mutation.getAlt()));
			} else {
            	int duplicate = 0;
            	for (Coordinate coordinate:selectedCoordinates){

            		if ( (coordinate.getChr().equals(mutation.getChr())) && (coordinate.getPos().equals(mutation.getPos()))) {
						duplicate = 1;
						break;
					}
				}
				if (duplicate == 0){
					selectedCoordinates.add(new Coordinate(mutation.getChr(),mutation.getPos(),mutation.getRef(),mutation.getAlt()));
				}
			}
        }

		for (int index = 0; index < selectedCoordinates.size(); index++) {
			Coordinate coordinate = selectedCoordinates.get(index);
			Integer lower = Integer.parseInt(coordinate.getPos()) - bamCoverage;
			Integer higher = Integer.parseInt(coordinate.getPos()) + bamCoverage;
			String line = coordinate.getChr() + ':' + lower.toString() + '-' + higher.toString();
			bw.write(line);
			bw.newLine();
		}
		bw.close();

		loadIGVButton.setText("Sending mutations to server.");

        // load file to server
        Channel channel = null;
        try {
            channel = sshSession.openChannel("sftp");
            channel.connect();
            ChannelSftp channelSftp = (ChannelSftp) channel;
            channelSftp.put(tempFile.getAbsolutePath(), "/storage/scratch/hmvv/igv/"+tempFile.getName());

        } catch (SftpException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
        
        //delete local temp file
        deleteFile(tempFile);
		loadIGVButton.setText("Server started.");
        createTempBamFileONServer(tempFile.getName());
		loadIGVButton.setText("Finished server work.");
		serverWorker.setStatus(1);
        return tempFile.getName();
	}

	private static void createTempBamFileONServer(String fileName) throws Exception {
	    String command = "bash /storage/apps/pipelines/"+Configurations.getEnvironment()+"/scripts/common/shell/createLocalBam.sh -f "+ fileName;
		//System.out.printf(command);
        CommandResponse rs = executeCommandAndGetOutput(command);
        if(rs.exitStatus != 0) {
            throw new Exception("Error creating local BAM file on server.");
        }
    }

    public static File copyTempBamFileONLocal(Sample sample, SftpProgressMonitor progressMonitor, String tempBamFileName ) throws Exception{

        String runIDFolder = temporaryHMVVDirectory + File.separator + sample.instrument + File.separator + sample.runFolder.runFolderName + "_filtered";
        new File(runIDFolder).mkdirs();

        String serverFileName = tempBamFileName+".bam";
        String filePath = "/storage/scratch/hmvv/igv/";

//        File localBamFile = new File(runIDFolder + File.separator + serverFileName.split("-")[0]+".bam").getAbsoluteFile();
		File localBamFile = new File(runIDFolder + File.separator + serverFileName);

        ChannelSftp channel = (ChannelSftp)sshSession.openChannel("sftp");
        channel.connect();
        channel.cd(filePath);
        channel.get(serverFileName, localBamFile.getAbsolutePath(), progressMonitor);
        channel.get(serverFileName + ".bai", localBamFile.getAbsolutePath() + ".bai");
        channel.exit();

        return localBamFile;
    }

	public static File copyFileONLocal(String fileType) throws Exception{

		String serverFileName = "";
		String serverFilePath = "";

		if (fileType.equals("tmb_control")){
			serverFileName = "TMB_ControlCOLO829_Scores.png";
			serverFilePath = "/storage/analysis/environments/" + Configurations.getEnvironment() + "/tmbQCFiles/";
		}

		String fileFolder = temporaryHMVVDirectory + File.separator + fileType.split("_")[0]+"Assay"+File.separator;
		new File(fileFolder).mkdirs();
		File localFile = new File(fileFolder + serverFileName);

		ChannelSftp channel = (ChannelSftp)sshSession.openChannel("sftp");
		channel.connect();
		channel.cd(serverFilePath);
		channel.get(serverFileName, localFile.getAbsolutePath());
		channel.exit();

		return localFile;
	}

    public static ArrayList<Database> getDatabaseInformation() throws Exception {

		String command = "tail -n +2 /storage/apps/pipelines/"+Configurations.getEnvironment()+"/config/db_version.csv";
		CommandResponse rs = executeCommandAndGetOutput(command);

		if(rs.exitStatus != 0) {
			throw new Exception(String.format("Error finding database information file."));
		}

		ArrayList<Database> databases = new ArrayList<Database>();

		for (String line : rs.responseLines){
			Database database = new Database();
			String[] dbInfoColumns = line.split(",");
			database.setName(dbInfoColumns[0]);
			database.setVersion(dbInfoColumns[1]);
			database.setRelease(dbInfoColumns[2]);
			databases.add(database);
		}
		return databases;
	}
    
    public static ArrayList<String> readConfigurationFile() throws Exception{
    	String command = "cat /storage/apps/pipelines/" + Configurations.getEnvironment() + "/config/config_v2.ini";
		CommandResponse rs = executeCommandAndGetOutput(command);
		if(rs.exitStatus != 0) {
			throw new Exception(String.format("Error finding or reading configuration file."));
		}
		
		ArrayList<String> configurations = new ArrayList<String>();
		for(String line : rs.responseLines) {
			if(line.equals("")){
				continue;
			}
			configurations.add(line);
		}
		return configurations;
    }

	public static ArrayList<String> readTMBSeqStatsFile(TMBSample sample) throws Exception{
		String command = "tail -n +2  /storage/analysis/environments/" + Configurations.getEnvironment() + "/"+sample.instrument+ "Analysis/tmbAssay/"+sample.runFolder.runFolderName+"/"+sample.sampleName+"/Paired/"+
				sample.sampleName+"_"+sample.getNormalSampleName()+"/"+
				sample.sampleName+"_"+sample.getNormalSampleName()+".final_result.txt";
		CommandResponse rs = executeCommandAndGetOutput(command);

		ArrayList<String> stats = new ArrayList<String>();

		if(rs.exitStatus != 0) {

			stats.add(GUICommonTools.PIPELINE_INCOMPLETE_STATUS);

		} else {
			for (String line : rs.responseLines) {
				if (line.equals("")) {
					continue;
				}
				stats.add(line);
			}
		}
		return stats;
	}

}
