package nio.engine;

import java.net.InetAddress;
import java.nio.channels.SelectionKey;

public class Main {

	static ConnectCallback connectCallback;
	static AcceptCallback accepteCallback;
	
	/**
	 * Premier client, pour tester le connect, puis ensuite la main loop
	 * @param args
	 */
	
	public static void main(String[] args) {
		
		try {
			MyEngine engine = new MyEngine(3200);
			System.out.println("------------Test du connect------------");
			engine.connect(InetAddress.getLocalHost(),3201, connectCallback);
			System.out.println("---------------------------------------");
			System.out.println("---------Test de la main loop------------");
			engine.mainloop();
			System.out.println("-----------------------------------------");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
