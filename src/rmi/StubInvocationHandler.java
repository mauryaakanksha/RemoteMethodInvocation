package rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Invocation handler for the client proxy class
 */
public class StubInvocationHandler implements InvocationHandler {

    /**
     * Server socket address (hostname, port)
     */
	
    private InetSocketAddress serverAddr;
	
	public StubInvocationHandler(InetSocketAddress address) {
	    this.serverAddr = address;
	}
	
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        
        Socket socket = null;
        
        if(serverAddr.getHostName() == null)
        	socket = new Socket(serverAddr.getAddress(), serverAddr.getPort());
        else 
        	socket = new Socket(serverAddr.getHostName(), serverAddr.getPort());
        
        Object retVal = null;
        
        try {
        	ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            
            out.writeObject(method.getName());
            out.writeObject(Integer.valueOf(args.length));
            for(Object arg : args) out.writeObject(arg);
            System.out.println("Wrote objects on client side");
            

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            retVal = in.readObject();
            System.out.println("Got result on client side");
            return retVal;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    try {
		        socket.close();
		    } catch (IOException e) {
		        System.out.println("Couldn't close a socket, what's going on?");
		    }
		}
        
        return retVal;
        
    }
}
