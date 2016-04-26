package rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServiceThread<T> implements Runnable{

	
	
	private Socket clientSocket;
    private int clientNumber;
    private T obj;

    public ServiceThread(Socket clientSocket, T obj, int clientNumber) {
        this.clientSocket = clientSocket;
        this.clientNumber = clientNumber;
        this.obj = obj;
        log("New connection with client# " + clientNumber + " at " + clientSocket);
    }

    /**
     * Services this thread's client by accepting method name and arguments from client object stream, invoke the method
     * and return the results
     */
    public void run() {
        try {
        	
        	
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            in.readObject();
            in.readObject();
            
            
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            //out.writeObject(retObj);
            
        } catch (IOException e) {
            log("Error handling client# " + clientNumber + ": " + e);
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log("Couldn't close a socket, what's going on?");
            }
            log("Connection with client# " + clientNumber + " closed");
        }
    }

    /**
     * Logs a simple message.  In this case we just write the
     * message to the server applications standard output.
     */
    private void log(String message) {
        System.out.println(message);
    }
}