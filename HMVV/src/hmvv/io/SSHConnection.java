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
	
	public static boolean isSuperUser(USER_FUNCTION function){
		return function.isSuperUser(userType);
	}
	
	public static void connect(String user, String password) throws Exception{
		sshSession = connectToSSH(user, password);
        readUserType();
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
	
	private static void readUserType() throws Exception{
		ChannelExec channel = (ChannelExec) sshSession.openChannel("exec");
		BufferedReader in=new BufferedReader(new InputStreamReader(channel.getInputStream()));
		channel.setCommand("checkAccountType");
		channel.connect();
		String usertype = in.readLine();

		if( usertype != null && !usertype.equals("")){
			usertype = usertype.toUpperCase();
			userType = USER_TYPE.valueOf(usertype);
        }else{
            throw new Exception(String.format("User not found. Please contact your system administrator."));
        }
	}
	
	private static File copyFile(String instrument, String runID, String runFile, SftpProgressMonitor progressMonitor) throws Exception{
		String runIDFolder = temporaryHMVVDirectory + File.separator + instrument + File.separator + runID;
		new File(runIDFolder).mkdirs();
		
		String fileName = new File(runFile).getName().trim();
		String filePath = new File(runFile).getParentFile().getPath().replace("\\", "/") + "/";
		
		File newSubPath = new File(runIDFolder + File.separator + fileName).getAbsoluteFile();
		if(newSubPath.exists()) {
			progressMonitor.end();
			//file already exists on local disk
			return newSubPath;
		}
	    
		ChannelSftp channel = (ChannelSftp) sshSession.openChannel("sftp");
		channel.connect();
		channel.cd(filePath);
		channel.get(fileName, newSubPath.getAbsolutePath(), progressMonitor);
		channel.get(fileName + ".bai", newSubPath.getAbsolutePath() + ".bai");
		channel.exit();
	    
		return newSubPath;
	}
	
	private static File findSample(String command, String instrument, String runID, String sampleID, SftpProgressMonitor progressMonitor) throws Exception {
		String runFile = SSHConnection.sendCommand(command);
		return copyFile(instrument, runID, runFile, progressMonitor);
	}
	
	public static File loadBAMForIGV(Sample sample, SftpProgressMonitor progressMonitor) throws Exception {
		String runID = sample.runID;
		String sampleID = sample.sampleName;
		String callerID = sample.callerID;
		String instrument =  sample.instrument;

		if(instrument.equals("pgm")){
			return SSHConnection.loadPGM_BAM(runID, sampleID, callerID, progressMonitor);
		}else if(instrument.equals("proton")){
			return SSHConnection.loadProton_BAM(runID, sampleID, callerID, progressMonitor);
		}else if( ( instrument.equals("nextseq") || (instrument.equals("nextseq550"))) && sample.assay.equals("heme")){
			return SSHConnection.loadIlluminaNextseqHeme_BAM(instrument, runID, sampleID, progressMonitor);
		}else{
			return SSHConnection.loadIllumina_BAM(instrument, runID, sampleID, progressMonitor);
		}
	}

	private static File loadPGM_BAM(String runID, String sampleID, String callerID, SftpProgressMonitor progressMonitor) throws Exception{
		String command = null;
		sampleID = sampleID.replaceAll(".*_", "");
		callerID = callerID.replaceAll(".*\\.", "");
		if(!callerID.equals("None")){
			//with a callerID
			command = String.format("ls /home/ionadmin/archivedReports/*%s/plugin_out/variantCaller_out.%s/IonXpress_%s_*.bam", runID,callerID,sampleID);
		}else{
			//without a callerID
			command = String.format("ls /home/ionadmin/archivedReports/*%s/plugin_out/variantCaller_out/IonXpress_%s/IonXpress_%s_*PTRIM.bam", runID,sampleID, sampleID);
		}
		return findSample(command, "proton", runID, sampleID, progressMonitor);
	}

	private static File loadProton_BAM(String runID, String sampleID, String callerID, SftpProgressMonitor progressMonitor) throws Exception{
		sampleID = sampleID.replaceAll(".*_", "");
		callerID = callerID.replaceAll(".*\\.", "");
		String command = String.format("ls /home/proton/*%s/plugin_out/variantCaller_out.%s/IonXpress_%s_*.bam", runID, callerID, sampleID);
		return findSample(command, "proton", runID, sampleID, progressMonitor);
	}

	private static File loadIlluminaNextseqHeme_BAM(String instrument, String runID, String sampleID, SftpProgressMonitor progressMonitor) throws Exception{
		String command = String.format("ls /home/environments/%s/"+instrument+"Analysis/*_%s_*/%s/variantCaller/%s*.sort.bam", Configurations.getEnvironment(), runID, sampleID, sampleID);
		return findSample(command, instrument, runID, sampleID, progressMonitor);
	}

	private static File loadIllumina_BAM(String instrument, String runID, String sampleID, SftpProgressMonitor progressMonitor) throws Exception{
		String command = String.format("ls /home/%s/*_%s_*/Data/Intensities/BaseCalls/Alignment/%s*.bam", instrument, runID, sampleID);
		return findSample(command, instrument, runID, sampleID, progressMonitor);
	}
	
	public static ArrayList<String> getCandidateCoverageIDs(String instrument, String runID) throws Exception {
		String coverageCommand = String.format("ls /home/%s/*_%s/plugin_out/ | grep coverageAnalysis", instrument, runID);
		CommandResponse coverageResult = SSHConnection.executeCommandAndGetOutput(coverageCommand);
		if(coverageResult.exitStatus != 0) {
			throw new Exception(String.format("Error finding samples (%s, %s)", instrument, runID));
		}
		return coverageResult.responseLines;
	}
	
	public static ArrayList<String> getCandidateVariantCallerIDs(String instrument, String runID) throws Exception {
		String variantCallerCommand = String.format("ls /home/%s/*_%s/plugin_out/ | grep variantCaller_out", instrument, runID);
		CommandResponse variantCallerResult = SSHConnection.executeCommandAndGetOutput(variantCallerCommand);
		if(variantCallerResult.exitStatus != 0) {
			throw new Exception(String.format("Error finding samples (%s, %s)", instrument, runID));
		}
		return variantCallerResult.responseLines;
	}
	
	public static ArrayList<String> getSampleListIon(String instrument, String runID, String variantCallerID) throws Exception {
		String sampleListCommand = String.format("ls /home/%s/*_%s/plugin_out/%s/ -F | grep / | grep Ion", instrument, runID, variantCallerID);
		CommandResponse sampleListResult = SSHConnection.executeCommandAndGetOutput(sampleListCommand);
		if(sampleListResult.exitStatus != 0) {
			throw new Exception(String.format("Error finding samples (%s, %s, %s)", instrument, runID, variantCallerID));
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
	
	public static ArrayList<String> getSampleListIllumina(String instrument, String runID) throws Exception {
		String sampleListCommand = String.format("cat /home/%s/*_%s_*/SampleSheet.csv", instrument, runID);
		CommandResponse sampleListResult = SSHConnection.executeCommandAndGetOutput(sampleListCommand);
		if(sampleListResult.exitStatus != 0) {
			throw new Exception(String.format("Error finding samples (%s, %s)", instrument, runID));
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
	
	private static String sendCommand(String command) throws Exception{
		StringBuilder outputBuffer = new StringBuilder();
		com.jcraft.jsch.Channel channel = sshSession.openChannel("exec");
		((ChannelExec)channel).setCommand(command);
		InputStream commandOutput = channel.getInputStream();
		channel.connect();
		int readByte = commandOutput.read();
		while(readByte != 0xffffffff){
			outputBuffer.append((char)readByte);
			readByte = commandOutput.read();
		}
		channel.disconnect();		
		return outputBuffer.toString();
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
        bw.write(Configurations.getEnvironment()+';'+sample.instrument + ';' + sample.runID + ';' + sample.assay + ';' +sample.sampleName+';'+ sample.callerID + ';' + sample.coverageID);
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
            channelSftp.put(tempFile.getAbsolutePath(), "/home/scratch/hmvv3/igv/"+tempFile.getName());

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
	    String command = "/home/pipelines/"+Configurations.getEnvironment()+"/shell/createLocalBam.sh -f "+ fileName;
        CommandResponse rs = executeCommandAndGetOutput(command);
        if(rs.exitStatus != 0) {
            throw new Exception("Error creating local BAM file on server.");
        }
    }

    public static File copyTempBamFileONLocal(Sample sample, SftpProgressMonitor progressMonitor, String tempBamFileName ) throws Exception{

        String runIDFolder = temporaryHMVVDirectory + File.separator + sample.instrument + File.separator + sample.runID + "_filtered";
        new File(runIDFolder).mkdirs();

        String serverFileName = tempBamFileName+".bam";
        String filePath = "/home/scratch/hmvv3/igv/";

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
			serverFilePath = "/home/environments/" + Configurations.getEnvironment() + "/assayCommonFiles/tmbAssay/";
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

		String command = "tail -n +2 /home/pipelines/"+Configurations.getEnvironment()+"/run_files/db_version.csv";
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
    	String command = "cat /home/ngs_config/hmvv_app_config.ini";
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
		String command = "tail -n +2  /home/environments/" + Configurations.getEnvironment() + "/"+sample.instrument+ "Analysis/tmbAssay/*_"+sample.runID+"_*/"+sample.sampleName+"/Paired/"+
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
