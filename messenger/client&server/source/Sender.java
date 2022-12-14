package serverSrc;

import java.io.*;
import java.net.Socket;

// Sender & receiver class refer - https://gist.github.com/HeptaDecane
public class Sender {

    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
	
	// receive path info and send file
	public Sender(String path) {
		try(Socket socket = new Socket("localhost",29555)) {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            sendFile(path);
            
            dataInputStream.close();
            dataInputStream.close();
        }catch (Exception e){
        	System.out.println("No File or Server has lost.");
            e.printStackTrace();
        }
	}
	
    private static void sendFile(String path) throws Exception{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        
        // send file size
        dataOutputStream.writeLong(file.length());  
        // break file into chunks
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }
}
