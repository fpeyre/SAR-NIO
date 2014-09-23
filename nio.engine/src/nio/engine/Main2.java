package nio.engine;

import java.net.InetAddress;
import java.nio.channels.SelectionKey;

public class Main2 {

	static ConnectCallback connectCallback;
	static AcceptCallback accepteCallback;
	
	/**
	 * 2eme client, pour tester le listen
	 * @param args
	 */
	
	public static void main(String[] args) {
		
		try {
			MyEngine engine = new MyEngine(3201);
			System.out.println("------------Test du connect------------");
			//engine.connect(InetAddress.getLocalHost(),2001, connectCallback);
			engine.listen(3201, accepteCallback);
			System.out.println("---------------------------------------");
			//	System.out.println("------------Test du listen------------");
			//engine.listen(3000, accepteCallback);
			//System.out.println("---------------------------------------");
			
			System.out.println("---------Test de la main loop------------");
			engine.mainloop();
			System.out.println("-----------------------------------------");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
