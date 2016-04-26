package rmi;

import java.net.InetSocketAddress;

public class NetworkTest {
	
	
	private class Server implements TestInterface {

		@Override
		public long getPID(int v) throws RMIException {
			System.out.println("Got value " + v);
			long id = Thread.currentThread().getId();
			return id;
		}
		
	}
	public void test() throws RMIException{
		
		InetSocketAddress address = new InetSocketAddress("localhost", 9000);
		
		// creating remote obj and skeleton
		TestInterface obj = new Server();
		Skeleton<TestInterface> skeleton = new Skeleton<TestInterface>(TestInterface.class, obj, address);
		skeleton.start();
		
		// creating client stub
		TestInterface client = Stub.create(TestInterface.class, address);
		
		System.out.println("Client's pid = " + Thread.currentThread().getId());
		long val = client.getPID(123);
		System.out.println(val);
	}
	
	public static void main(String args[]) throws Exception{
		NetworkTest t = new NetworkTest();
		t.test();
	}
}
