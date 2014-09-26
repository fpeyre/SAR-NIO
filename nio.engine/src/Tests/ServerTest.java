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
		System.out.println("Server lancé sur le port : "+portConnection);
		NioEngine engine = null;
		try {
			engine = new MyEngine();
		} catch (Exception e) {
			System.out.println("Problème génération nouvel engine : "+e.getMessage());
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
		System.out.println("AcceptCallback fermé");

	}

	@Override
	public void accepted(NioServer server, NioChannel channel) {
		System.out.println("AcceptCallback accepté");
		channel.setDeliverCallback(this);
	}


	@Override
	public void deliver(NioChannel channel, ByteBuffer bytes) {
		System.out.println("Message reçu :"+ new String(bytes.array()));
		String ping = "Message n°"+numMessage;
		numMessage++;
		channel.send(ping.getBytes(),0,ping.getBytes().length);
	}




}
