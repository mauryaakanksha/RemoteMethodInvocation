package rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Invocation handler for the client proxy class
 */
public class StubInvocationHandler implements InvocationHandler {

    /**
     * Server socket address (hostname, port)
     */
	
    public InetSocketAddress serverAddr;
    public Class c; // Interface
	
	public StubInvocationHandler(Class c, InetSocketAddress address) {
	    this.serverAddr = address;
	    this.c = c;
	}
	
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

    	if(method.getName().equalsIgnoreCase("toString") ) {
    		return "Interface name = " + c.toString() + ", Skeleton address =  " + serverAddr.toString() + "\n"; 
    	} else if(method.getName().equalsIgnoreCase("hashCode") ) {
    		
    		int prime = 31;
    		int code = prime * serverAddr.hashCode();
    		code  += prime * c.hashCode();
    		return code;
    		
    	}else if(method.getName().equalsIgnoreCase("equals") && args.length == 1) {
    		Object ob = args[0];
    		if(ob == null) return false;
    		InvocationHandler handler = Proxy.getInvocationHandler(ob);
    		if( handler instanceof StubInvocationHandler) {
    			StubInvocationHandler stubHandler = (StubInvocationHandler) handler;
    			
    			if ( !stubHandler.c.equals(c) ) {
    				return false;
    			}
    			
    			if ( !stubHandler.serverAddr.equals(serverAddr)){
    				return false;
    			}
    			return true;
    		}
    	   
    	    return false;

    	}
    	
        Socket socket = null;
        
        if(serverAddr == null) System.out.println("Server address is null! Error!");
        
        
        if(serverAddr.getHostName() == null)
        	socket = new Socket(serverAddr.getAddress(), serverAddr.getPort());
        else 
        	socket = new Socket(serverAddr.getHostName(), serverAddr.getPort());
        
        Object retVal = null;
        
        try {
        	ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            
        	String methodName = method.getName();
            out.writeObject(methodName);
            out.writeObject(Integer.valueOf(args.length));
            for(Object arg : args) out.writeObject(arg);
            System.out.println("Wrote objects on client side");
            

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            retVal = in.readObject();
            System.out.println("Got result on client side");
            return retVal;
		} catch (Exception e) {
			// TODO ensureStubConnects() -- check what's the error if you don't throw RMI exception
			throw new RMIException(e);
			//e.printStackTrace();
		} finally {
		    try {
		        socket.close();
		    } catch (IOException e) {
		        System.out.println("Couldn't close a socket, what's going on?");
		    }
		}
        
    }
}
