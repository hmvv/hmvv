package hmvv.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JOptionPane;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpProgressMonitor;

import hmvv.main.Configurations;
import hmvv.model.CommandResponse;
import hmvv.model.Pipeline;
import hmvv.model.Sample;

public class SSHConnection {
	
	private static Session sshSession;
	private static String[] groups;
	private static int forwardingPort;
	private static String temporaryBAMDirectory = "temp_BAM_files";
	
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
	
	public static boolean isSuperUser(){
		for(String group : groups){
			if(group.equals(Configurations.SUPER_USER_GROUP)){
				return true;
			}
		}
		return false;
	}
	
	public static void connect(String user, String password) throws Exception{
		sshSession = connectToSSH(user, password);
		groups = readGroups();
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
	
	private static String[] readGroups() throws Exception{
		ChannelExec channel = (ChannelExec) sshSession.openChannel("exec");
		BufferedReader in=new BufferedReader(new InputStreamReader(channel.getInputStream()));
		channel.setCommand("groups");
		channel.connect();
		String groups = in.readLine();
		String[] retval = groups.split("\\s+");
		if(retval == null){
			return new String[]{};
		}else{
			return retval;
		}
	}
	
	private static File copyFile(String instrument, String runID, String runFile, SftpProgressMonitor progressMonitor) throws Exception{
		String runIDFolder = temporaryBAMDirectory + File.separator + instrument + File.separator + runID;
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
		}else if(instrument.equals("nextseq") && sample.assay.equals("heme")){
			return SSHConnection.loadIlluminaNextseqHeme_BAM(runID, sampleID, progressMonitor);
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
	
	private static File loadIlluminaNextseqHeme_BAM(String runID, String sampleID, SftpProgressMonitor progressMonitor) throws Exception{
		String instrument = "nextseq";
		//TODO test this
		String command = String.format("ls /home/environments/%s/nextseqAnalysis/*_%s_*/%s/variantCaller/%s*.sort.bam", Configurations.getEnvironment(), runID, sampleID, sampleID);
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
	
	private static String sendCommand(String command){
		StringBuilder outputBuffer = new StringBuilder();
		try{
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
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, e);
		}
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
		File tempBAMDir = new File(temporaryBAMDirectory);
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
			//TODO ignore this? The user may have the temp folder or the file open
		}
	}

	
	public static long getFileSize(Pipeline pipeline) throws Exception {
		String command="";

		if  ( pipeline.getInstrumentName().equals("miseq")) {
			command = String.format("ls -l --block-size=KB  /home/%s/*_%s_*/Data/Intensities/BaseCalls/Alignment/%s_*.vcf | awk '{print $5}' | tr -dc '0-9' ", pipeline.getInstrumentName(), pipeline.getRunID(), pipeline.getsampleName());
		} else if  ( pipeline.getInstrumentName().equals("nextseq")) {
			command = String.format("ls -l --block-size=MB /home/%s/*_%s_*/out1/%s*_R1_001.fastq.gz | awk '{print $5}' | tr -dc '0-9'  ", pipeline.getInstrumentName(), pipeline.getRunID(), pipeline.getsampleName());
		}else if  ( pipeline.getInstrumentName().equals("proton")) {
			command = String.format("ls -l --block-size=KB /home/%s/*%s/plugin_out/variantCaller_out*/%s/TSVC_variants.vcf | awk '{print $5}' | tr -dc '0-9' ", pipeline.getInstrumentName(), pipeline.getRunID(), pipeline.getsampleName());
		}
		CommandResponse commandResult = SSHConnection.executeCommandAndGetOutput(command);
		String fileSizeString = commandResult.responseLines.get(0);
		if (fileSizeString.equals("")){fileSizeString="0";}
		return Long.parseLong(fileSizeString);
	}
}





