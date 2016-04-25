package rmi.proxy;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.*;

/**
 * Invocation handler for the client proxy class
 */
public class StubInvocationHandler implements InvocationHandler {

    /**
     * Server socket address (hostname, port)
     */
	
    private InetAddress serverAddr;
    private int serverPort;
    private String serverHostName;
	private Socket socket;
	
	public StubInvocationHandler(InetAddress address, int port) {
	    this.serverAddr = address;
	    this.serverPort = port;
	    this.serverHostName = "";
	}
	
    public StubInvocationHandler(InetAddress address, int port, String hostName) {
        this.serverAddr = address;
        this.serverPort = port;
        this.serverHostName = hostName;
    }
    
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (serverHostName.isEmpty()) {
        	socket = new Socket(serverAddr , serverPort);
        } else {
        	socket = new Socket(serverHostName, serverPort);
        }
        
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        out.writeObject(method);
        out.writeObject(args);

        Object retVal = in.readObject();

        return retVal;
    }
}
