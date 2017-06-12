import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileServer extends Remote {
	public void sendFile(String filename, byte[] file) throws RemoteException; 
	public String[] getFiles() throws RemoteException;
	public void deleteFile(String filename) throws RemoteException;
	public byte[] getFile(String filename) throws RemoteException;
	public void renameFile(String filename, String newFilename) throws RemoteException;

}
