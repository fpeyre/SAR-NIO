package nio.engine;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MyEngine extends NioEngine {

	Selector selector;
	public MyEngine(int port) throws Exception {
		super();
		this.selector = Selector.open();
	}
	
	  /**
	   * NIO engine mainloop Wait for selected events on registered channels
	   * Selected events for a given channel may be ACCEPT, CONNECT, READ, WRITE
	   * Selected events for a given channel may change over time
	   */
	@Override
	public void mainloop() {
		while(true) {

			  int readyChannels = 0;
			try {
				readyChannels = selector.select();
			} catch (IOException e) {
				System.out.println("Probleme select du selector : "+e.getMessage());
			}

			  if(readyChannels == 0) continue;


			  Set<SelectionKey> selectedKeys = selector.selectedKeys();

			  Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

			  while(keyIterator.hasNext()) {

			    SelectionKey key = keyIterator.next();

			    if(key.isAcceptable()) {
			        // a connection was accepted by a ServerSocketChannel.

			    } else if (key.isConnectable()) {
			        // a connection was established with a remote server.

			    } else if (key.isReadable()) {
			        // a channel is ready for reading

			    } else if (key.isWritable()) {
			        // a channel is ready for writing
			    }

			    keyIterator.remove();
			  }
			}
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
			MyServer server = new MyServer(port);
			System.out.println ("Listening on port " + port);
		    ServerSocketChannel serverChannel = ServerSocketChannel.open();
		    ServerSocket serverSocket = serverChannel.socket();
		    serverSocket.bind (new InetSocketAddress (port));
		    serverChannel.configureBlocking (false);
		    
		    if (serverChannel.accept()==null)
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
		
				MyChannel channel = new MyChannel();
				SocketChannel mySocketChannel = channel.mySocketChannel;
				mySocketChannel = SocketChannel.open();
				mySocketChannel.configureBlocking(false);
		
				// bind the server socket to the specified address and port
				InetSocketAddress isa = new InetSocketAddress(hostAddress, port);
				mySocketChannel.bind(isa);
		
		
				if (mySocketChannel.isConnected())
				{	
					System.out.println("Connecté à la socket");
					callback.connected(channel);
				}
	}
}
