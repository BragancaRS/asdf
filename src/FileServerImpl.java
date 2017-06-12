import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class FileServerImpl extends UnicastRemoteObject implements FileServer {
	
	public FileServerImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		try {
			if(System.getSecurityManager() == null){
				System.setProperty("java.security.policy", "/home/raphael/NetBeansProjects/JavaApplication4/src//local.policy");
		                          ;		
                                System.setSecurityManager(new RMISecurityManager());
			}
			
			FileServerImpl fs = new FileServerImpl();
                        System.setProperty("java.rmi.server.raphael-l16", "172.16.90.238");
			String serverObjectName = "/172.16.90.238/FileServer";
			Naming.rebind(serverObjectName, fs);
			System.out.println("RMI Server running...");
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendFile(String filename, byte[] file) throws RemoteException {
		FileOutputStream fileOuputStream = null; 
		try { 
			    fileOuputStream = new FileOutputStream("/Users/DaleParkes/Documents/College/Year 3/Semester 2/Network Distributed Systems/NetDistributedSystemsWorkbench/Assignment2/ServerFiles/"+filename); 
			    fileOuputStream.write(file);
			    fileOuputStream.close();
			 } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public String[] getFiles() throws RemoteException{
				
		String[] files = new File("/Users/DaleParkes/Documents/College/Year 3/Semester 2/Network Distributed Systems/NetDistributedSystemsWorkbench/Assignment2/ServerFiles/").list();
				
		return files;
	}
	

	@Override
	public void deleteFile(String filename) throws RemoteException {
		File file = new File("/Users/DaleParkes/Documents/College/Year 3/Semester 2/Network Distributed Systems/NetDistributedSystemsWorkbench/Assignment2/ServerFiles/"+filename);
    	
		file.delete();
	}

	@Override
	public byte[] getFile(String filename) throws RemoteException {

		File file = new File("/Users/DaleParkes/Documents/College/Year 3/Semester 2/Network Distributed Systems/NetDistributedSystemsWorkbench/Assignment2/ServerFiles/"+filename);
		
		byte[] array = null;
		try {
			array = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return array;
	}

	@Override
	public void renameFile(String filename, String newFilename) throws RemoteException {
		File file = new File("/Users/DaleParkes/Documents/College/Year 3/Semester 2/Network Distributed Systems/NetDistributedSystemsWorkbench/Assignment2/ServerFiles/"+filename);
		File newFile = new File("/Users/DaleParkes/Documents/College/Year 3/Semester 2/Network Distributed Systems/NetDistributedSystemsWorkbench/Assignment2/ServerFiles/"+newFilename);
		
		try {
			Files.move(file.toPath(), newFile.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
