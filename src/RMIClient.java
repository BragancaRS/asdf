import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import javax.swing.*;

public class RMIClient{
	
	String hostname;
	String serviceName;
	RMIClientGui clientGui;
	FileServer fileServer;
	
	public RMIClient(RMIClientGui client, String hostname, String serviceName){
		this.serviceName = serviceName;
		this.clientGui = client;
		this.hostname = hostname;
		
		String serverObjectName = "//"+this.hostname+"/"+this.serviceName;
		try {
			
			if(System.getSecurityManager() == null){
				System.setProperty("java.security.policy", "/Users/DaleParkes/Documents/College/Year 3/Semester 2/Network Distributed Systems/NetDistributedSystemsWorkbench/Assignment2/src/local.policy");
				System.setSecurityManager(new RMISecurityManager());
			}
			
			fileServer = (FileServer)Naming.lookup("rmi:"+serverObjectName);
			if(fileServer != null){
				clientGui.append("\nConnected..");
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendFile(String filename, byte[] file){
		try {
			fileServer.sendFile(filename, file);
			clientGui.append("\nFile "+filename+" sent.");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String[] getFiles() throws RemoteException{
		
		String[] files = fileServer.getFiles();
		
		return files;
	}
	
	public void deleteFile(String filename) throws RemoteException{
		fileServer.deleteFile(filename);
		clientGui.append("\nFile "+filename+" has been deleted.");

	}
	
	public void saveFile(String directory, String filename){
		
		FileOutputStream fileOuputStream = null; 
		try {
			byte[] file = fileServer.getFile(filename);
			
			fileOuputStream = new FileOutputStream(directory+"/"+filename); 
		    fileOuputStream.write(file);
		    fileOuputStream.close();
		    
		    clientGui.append("\nFile "+filename+" has been saved to "+directory+".");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void rename(String filename, String newFilename) throws RemoteException{
		fileServer.renameFile(filename, newFilename);
	}
}
