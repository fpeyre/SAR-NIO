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
	//Si le message CLOSEDCLIENT est recu, je ferme le channel
	public void deliver(NioChannel channel, ByteBuffer bytes) {
		String msg_recu=new String(bytes.array());
		if(msg_recu.equals("CLOSEDCLIENT")){
			channel.close();
		}
		else{
			System.out.println("Cot� serveur : Message envoy� : "+ msg_recu );
			String ping = "Message n�"+numMessage;
			numMessage++;
			if(numMessage==100){
				channel.send("CLOSEDSERVER".getBytes(),0,"CLOSEDSERVER".getBytes().length);
			}
			else{
				channel.send(ping.getBytes(),0,ping.getBytes().length);
			}

		}
	}




}
