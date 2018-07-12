package hmvv.io;

import java.awt.Component;
import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import hmvv.model.Coordinate;

public class IGVConnection {
	
	private Socket connection;
	private BufferedReader in;
	private PrintWriter out;
	
	private IGVConnection() throws Exception{
		int igvLoadPort = 60151;
		String igvLoadHost = "localhost";
		connection = new Socket(igvLoadHost, igvLoadPort);
		connection.setSoTimeout(10000);
		out = new PrintWriter(connection.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	}
	
	public void closeConnection() {
		if(connection != null){
			try {
				connection.close();
			} catch (IOException e) {}
		}
	}
	
	private static String executeCommand(String command) throws Exception{		
		IGVConnection connection = new IGVConnection();
		try {
			connection.out.println(command + "\n");
			String response = connection.in.readLine();
			return response;
		}finally {
			connection.closeConnection();
		}
	}
	
	
	private static AtomicBoolean igvBusy = new AtomicBoolean(false);
	/**
	 * 
	 * @param parent
	 * @param file
	 * @return the response from the successful load process
	 * @throws Exception if the load did not work properly
	 */
	public static String loadFileIntoIGV(Component parent, String file) throws Exception{
		if(igvBusy.get()) {
			return "Command ignored. Previous IGV command still in process.";
		}
				
		try{
			igvBusy.set(true);
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			return executeCommand("load " + file + "\n" + "genome hg19");
		}catch(ConnectException e){
			throw new Exception("Sample not loaded. Please make sure IGV is running before trying load a sample.");
		}finally {
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			igvBusy.set(false);
		}
	}

	public static String loadCoordinateIntoIGV(Component parent, Coordinate coordinate) throws Exception {
		if(igvBusy.get()) {
			return "Command ignored. Previous IGV command still in process.";
		}
		
		try{
			igvBusy.set(true);
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			return executeCommand("goto " + coordinate.getChr() + ":" + coordinate.getPos());
		}catch(ConnectException e1){
			throw new Exception("Coordinate not loaded. Please make sure IGV is running.");
		}finally {
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			igvBusy.set(false);
		}
	}
}
