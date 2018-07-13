package hmvv.io;


import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import hmvv.main.Configurations;
import hmvv.model.CommandResponse;

public class SSHConnection {
	
	private static Session sshSession;
	private static String[] groups;
	private static int forwardingPort;
	
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
	
	public static String findPGMSample(String runID, String sampleID, String callerID){
		String runFile = null;
		sampleID = sampleID.replaceAll(".*_", "");
		callerID = callerID.replaceAll(".*\\.", "");
		if(!callerID.equals("None")){
			//with a callerID
			String command = String.format("ls /home/ionadmin/archivedReports/*%s/plugin_out/variantCaller_out.%s/IonXpress_%s_*.bam", runID,callerID,sampleID);
			runFile = SSHConnection.sendCommand(command);

		}else{
			//without a callerID
			String command = String.format("ls /home/ionadmin/archivedReports/*%s/plugin_out/variantCaller_out/IonXpress_%s/IonXpress_%s_*PTRIM.bam", runID,sampleID, sampleID);
			runFile = SSHConnection.sendCommand(command);
		}
		String httpFile = runFile.replace("/home/ionadmin/archivedReports/", "http://10.110.21.70/gene50/");
		return httpFile;
	}

	public static String findProtonSample(String runID, String sampleID, String callerID){
		String runFile = null;
		sampleID = sampleID.replaceAll(".*_", "");
		callerID = callerID.replaceAll(".*\\.", "");
		String command = String.format("ls /home/proton/*%s/plugin_out/variantCaller_out.%s/IonXpress_%s_*.bam", runID,callerID,sampleID);
		runFile = SSHConnection.sendCommand(command);
		String httpFile = runFile.replace("/home/proton/", "http://10.110.21.70/proton/");	
		return httpFile;
	}
	
	public static String findIlluminaNextseqSample(String runID, String sampleID){
		String instrument = "nextSeq_heme";
		String runFile = null;
		String command = String.format("ls /home/%s/*_%s_*/%s*.sort.bam", instrument, runID, sampleID);
		runFile = SSHConnection.sendCommand(command);
		String original = String.format("/home/%s/", instrument);
		String replace = String.format("http://" + Configurations.SSH_SERVER_ADDRESS + "/%s/", instrument);
		String httpFile = runFile.replace(original, replace);	
		return httpFile;
	}
	
	public static String findIlluminaSample(String instrument, String runID, String sampleID){
		String runFile = null;
		String command = String.format("ls /home/%s/*_%s_*/Data/Intensities/BaseCalls/Alignment/%s*.bam", instrument, runID,sampleID);
		runFile = SSHConnection.sendCommand(command);
		String original = String.format("/home/%s/", instrument);
		String replace = String.format("http://" + Configurations.SSH_SERVER_ADDRESS + "/%s/", instrument);
		String httpFile = runFile.replace(original, replace);	
		return httpFile;
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
	
	public static CommandResponse executeCommandAndGetOutput(String command) throws Exception{
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
    
	/**
	 * 
	 * @param parent
	 * @param file
	 * @return the response from the successful load process
	 * @throws Exception if the load did not work properly
	 */
	public static String loadFileIntoIGV(Component parent, String file) throws Exception{
		int igvLoadPort = 60151;
		String igvLoadHost = "localhost";
		
		Socket socket = null;
		try{
			socket = new Socket(igvLoadHost, igvLoadPort);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println("load " + file);
			String response = in.readLine();
			return response;
		}catch(ConnectException e1){
			throw new Exception("Sample not loaded. Please make sure IGV is running before trying load a sample.");
		}finally{
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {}
			}
		}
	}
}