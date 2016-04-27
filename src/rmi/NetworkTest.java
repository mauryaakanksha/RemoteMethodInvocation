package rmi;

import java.net.InetSocketAddress;
import java.rmi.server.UnicastRemoteObject;

public class NetworkTest {
	
	public void tes(int v) {
		System.out.println("Primitive");
	}
	
	public void tes(Integer v) {
		System.out.println("Boxed");
	}
	
	private class Server implements TestInterface {

		@Override
		public long getPID(int v) throws RMIException {
			System.out.println("Got value " + v);
			long id = Thread.currentThread().getId();
			return id;
		}
		
	}
	
	public void test() throws RMIException{
		
		Integer v = 2;
		int x =4;
		
		tes(1);
		tes(v);
		
		InetSocketAddress address = new InetSocketAddress("localhost", 9000);
		// creating remote obj and skeleton
		TestInterface obj = new Server();
		Skeleton<TestInterface> skeleton = new Skeleton<TestInterface>(TestInterface.class, obj, address);
		skeleton.start();
		
		
		// creating client stub
		TestInterface client = Stub.create(TestInterface.class, address);
		System.out.println("toString = " + client.toString());
		System.out.println("Client's pid = " + Thread.currentThread().getId());
		long val = client.getPID(123);
		System.out.println(val);
		
		skeleton.stop();
	}
	
	public static void main(String args[]) throws Exception{
		NetworkTest t = new NetworkTest();
		t.test();
	}
}
