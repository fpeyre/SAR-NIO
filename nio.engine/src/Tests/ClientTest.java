package Tests;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import ImplemClasses.MyEngine;
import nio.engine.ConnectCallback;
import nio.engine.DeliverCallback;
import nio.engine.NioChannel;
import nio.engine.NioEngine;

public class ClientTest implements Runnable, ConnectCallback, DeliverCallback {

	NioChannel clientTestChannel = null;
	String addressConnection;
	int portConnection;
	int numMessage;

	public ClientTest(String addressConnection, int portConnection) {
		super();
		this.addressConnection = addressConnection;
		this.portConnection = portConnection;
		this.numMessage=0;
	}

	@Override
	public void run() {
		@SuppressWarnings("unused")
		NioChannel myChannel = null;
		System.out.println("Le client va se connecter � l'adresse "+addressConnection+" sur le port "+portConnection);
		NioEngine myEngine = null;
		try {
			myEngine = new MyEngine();
		} catch (Exception e) {
			System.out.println("Probl�me g�n�ration de l'engine : "+e.getMessage());
		}

			try {
				myEngine.connect(InetAddress.getByName(addressConnection), portConnection, this);
			} catch (SecurityException | IOException e) {
				System.out.println("Probl�me � la connection "+e.getMessage());
			}

			myEngine.mainloop();

	}

	@Override
	
	public void closed(NioChannel channel) {		
		System.out.println("ConnectCallback ferm�");

	}

	@Override
	public void connected(NioChannel channel) {
		System.out.println("ConnectCallback connected");
		clientTestChannel = channel;
		clientTestChannel.setDeliverCallback(this);
		String message = "Message n�"+numMessage;
		numMessage++;
		channel.send(message.getBytes(),0,message.getBytes().length);

	}

	@Override
	//Si message CLOSEDSERVER est recu, je ferme le channel.
	public void deliver(NioChannel channel, ByteBuffer bytes) {

		String msg_send=new String(bytes.array());
		if(msg_send.equals("CLOSEDSERVER")){
			channel.close();	
		}
		else{
			System.out.println("Cot� client : Message envoy� : "+ msg_send);
			String message = "Message n�"+numMessage;
			numMessage++;
			channel.send(message.getBytes(),0,message.getBytes().length);
		}
	}

}
