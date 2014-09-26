package Tests;

import java.io.IOException;
import java.nio.ByteBuffer;
import nio.engine.AcceptCallback;
import nio.engine.DeliverCallback;
import nio.engine.NioChannel;
import nio.engine.NioEngine;
import nio.engine.NioServer;
import ImplemClasses.MyEngine;

public class ServerTest implements Runnable,AcceptCallback,DeliverCallback {

	int portConnection;
	int numMessage;

	public ServerTest(int portConnection) {
		this.portConnection = portConnection;
		numMessage=1;
	}

	
	@Override
	public void run() {
		System.out.println("Server lanc� sur le port : "+portConnection);
		NioEngine engine = null;
		try {
			engine = new MyEngine();
		} catch (Exception e) {
			System.out.println("Probl�me g�n�ration nouvel engine : "+e.getMessage());
		}

		try {
			engine.listen(portConnection,this);

		} catch (IOException e) {
			System.out.println("Erreur listen : "+e.getMessage());
		}

		engine.mainloop();

	}	
	
	@Override
	public void closed(NioChannel channel) {
		System.out.println("AcceptCallback ferm�");

	}

	@Override
	public void accepted(NioServer server, NioChannel channel) {
		System.out.println("AcceptCallback accept�");
		channel.setDeliverCallback(this);
	}


	@Override
	public void deliver(NioChannel channel, ByteBuffer bytes) {
		System.out.println("Message re�u :"+ new String(bytes.array()));
		String ping = "Message n�"+numMessage;
		numMessage++;
		channel.send(ping.getBytes(),0,ping.getBytes().length);
	}




}
