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

            String methodName = (String) in.readObject();
            int argsLen = (Integer)in.readObject();
            Object[] args = new Object[argsLen];
            Class<?>[] argsTypes = new Class[argsLen];
            for(int i = 0; i < args.length; i++) {
            	argsTypes[i] = (Class<?>)in.readObject();
            }
            for(int i = 0; i < args.length; i++) {
            	args[i] = (Object)in.readObject();
            }
            log("Read objects on server side");
            Method method = obj.getClass().getMethod(methodName, (Class<?>[]) argsTypes);
            Object retObj = null;
            Throwable serverException = null;
			try {
				retObj = method.invoke(obj, args);
			} catch (IllegalAccessException e) {
				serverException = e;
			}catch (InvocationTargetException e) {
				serverException = e;
			}catch (Exception e ) {
				serverException = e;
			}
            // call the object with method and find result
            
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
          
            log("Wrote object on server side");
            out.writeObject(retObj);
            out.writeObject(serverException);
            
        } catch (IOException e) {
        	
            log("Error handling client : " + e);
        	e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
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

    private Class getOrigClass(Class t) {
    	switch (t.getName()) {
    		case "java.lang.Byte" : return byte.class; 
    		case "java.lang.Short" : return short.class;
    		case "java.lang.Integer" : return int.class;
    		case "java.lang.Float" : return float.class;
    		case "java.lang.Double" : return double.class;
    		case "java.lang.Character" : return char.class;
    		case "java.lang.Boolean" : return boolean.class;
    		default: return t;
    	}
    }
    /**
     * Logs a simple message.  In this case we just write the
     * message to the server applications standard output.
     */
    private void log(String message) {
        System.out.println("S: " + message);
    }
}