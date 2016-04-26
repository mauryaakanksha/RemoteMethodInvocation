package rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class ServiceThread<T> implements Runnable{
	
	private Socket clientSocket;
    private T obj;
    private Skeleton<T> skeleton;

    public ServiceThread(Socket clientSocket, T obj, Skeleton<T> skeleton) {
        this.clientSocket = clientSocket;
        this.obj = obj;
        this.skeleton = skeleton;
        
    }

    /**
     * Services this thread's client by accepting method name and arguments from client object stream, invoke the method 
     * on the object and return the results
     */
    public void run() {
        try {
        	
        	log("New connection with client at " + clientSocket);
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            String methodName = (String)in.readObject();
            // TODO
            Method method = null;
            Object[] args = new Object[(Integer)in.readObject()];
            for(int i = 0; i < args.length; i++) args[i] = (Object) in.readObject();
            
            System.out.println("Read objects on server side");
            
            Object retObj = method.invoke(obj, args);
            // call the object with method and find result
            
            
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
          
            System.out.println("Wrote object on server side");
            out.writeObject(retObj);
            
        } catch (IOException e) {
            log("Error handling client : " + e);
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log("Couldn't close a socket, what's going on?");
            }
            log("Connection with client closed");
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