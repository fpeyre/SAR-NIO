package nio.engine;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Hashtable;

public class MyEngine extends NioEngine {

	MyServer server;
	
	public MyEngine(int port) throws Exception {
		super();
		this.server = new MyServer(port);
		// TODO Auto-generated constructor stub
	}
	
	  /**
	   * NIO engine mainloop Wait for selected events on registered channels
	   * Selected events for a given channel may be ACCEPT, CONNECT, READ, WRITE
	   * Selected events for a given channel may change over time
	   */
	@Override
	public void mainloop() {
		// TODO Auto-generated method stub
		
	}
	
	  /**
	   * Ask for this NioEngine to accept connections on the given port,
	   * calling the given callback when a connection has been accepted.
	   * @param port
	   * @param callback
	   * @return an NioServer wrapping the server port accepting connections.
	   * @throws IOException if the port is already used or can't be bound.
	   */
	@Override
	public NioServer listen(int port, AcceptCallback callback)
			throws IOException {
		MyServer server = new MyServer(8080);
		
		//Code de l'acceptation des connections au port
		
	
		callback.accepted(server, server.channel);
		return server;
	}
	
	  /**
	   * Ask this NioEngine to connect to the given port on the given host.
	   * The callback will be notified when the connection will have succeeded.
	   * @param hostAddress
	   * @param port
	   * @param callback
	   */
	
	@Override
	public void connect(InetAddress hostAddress, int port,
			ConnectCallback callback) throws UnknownHostException,
			SecurityException, IOException {
		
		SocketChannel mySocketChannel = this.server.channel.getChannel();
		mySocketChannel = SocketChannel.open();
		mySocketChannel.configureBlocking(false);

		// bind the server socket to the specified address and port
		InetSocketAddress isa = new InetSocketAddress(hostAddress, port);
		mySocketChannel.bind(isa);

		if (mySocketChannel.isConnected())
		{	
			System.out.println("Connecté à la socket");
			callback.connected(this.server.channel);
		}
		
	}

}
