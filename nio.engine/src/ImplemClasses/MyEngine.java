package ImplemClasses;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Iterator;

import nio.engine.AcceptCallback;
import nio.engine.ConnectCallback;
import nio.engine.NioEngine;
import nio.engine.NioServer;

public class MyEngine extends NioEngine {

	Selector selector;
	// buffers for writing data
	Hashtable<SocketChannel, ByteBuffer> outBuffers;

	Hashtable<SocketChannel, ConnectCallback> MappingConnectCallback;
	Hashtable<ServerSocketChannel, MyServer> MappingServers;
	Hashtable<SocketChannel, MyChannel> MappingChannels;

	public MyEngine() throws Exception {
		super();
		this.selector = Selector.open();

		// Initialisation des différentes Hashtables
		outBuffers = new Hashtable<SocketChannel, ByteBuffer>();
		MappingConnectCallback = new Hashtable<SocketChannel, ConnectCallback>();
		MappingServers = new Hashtable<ServerSocketChannel, MyServer>();
		MappingChannels = new Hashtable<SocketChannel, MyChannel>();
	}

	/**
	 * NIO engine mainloop Wait for selected events on registered channels
	 * Selected events for a given channel may be ACCEPT, CONNECT, READ, WRITE
	 * Selected events for a given channel may change over time
	 */
	@Override
	public void mainloop() {

		while (true) {
			try {
				selector.select();
				Iterator<?> selectedKeys = this.selector.selectedKeys()
						.iterator();

				while (selectedKeys.hasNext()) {

					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid()) {
						System.out.println("Clé invalide");
						continue;

					} else if (key.isAcceptable()) {
						System.out.println("Clé acceptable");
						handleAccept(key);

					} else if (key.isReadable()) {
						System.out.println("Clé Lisible");
						handleRead(key);

					} else if (key.isWritable()) {
						System.out.println("Clé Inscriptible");
						handleWrite(key);

					} else if (key.isConnectable()) {
						System.out.println("Clé connectable");
						handleConnection(key);
					} else
						System.out.println("  ---> unknow key=");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Ask for this NioEngine to accept connections on the given port, calling
	 * the given callback when a connection has been accepted.
	 * 
	 * @param port
	 * @param callback
	 * @return an NioServer wrapping the server port accepting connections.
	 * @throws IOException
	 *             if the port is already used or can't be bound.
	 */
	@Override
	public NioServer listen(int port, AcceptCallback callback)
			throws IOException {
		MyServer server = new MyServer();

		System.out.println("Listening on port " + port);

		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);

		InetSocketAddress myAdress = new InetSocketAddress("localhost", port);
		serverChannel.socket().bind(myAdress);

		server.setSSC(serverChannel);
		server.setAcceptCallback(callback);

		serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
		System.out.println("ServerChannel bindé, enregistré dans le selector");
		
		MappingServers.put(serverChannel, server);
		return server;
	}

	/**
	 * Ask this NioEngine to connect to the given port on the given host. The
	 * callback will be notified when the connection will have succeeded.
	 * 
	 * @param hostAddress
	 * @param port
	 * @param callback
	 */

	@Override
	public void connect(InetAddress hostAddress, int port,
			ConnectCallback callback) throws UnknownHostException,
			SecurityException, IOException {

		SocketChannel mySocketChannel = SocketChannel.open();
		mySocketChannel.configureBlocking(false);

		mySocketChannel.register(this.selector, SelectionKey.OP_CONNECT);
		// connection to the hostAddress at the given port
		System.out.println("Connection reussi : "
				+ mySocketChannel.connect(new InetSocketAddress(hostAddress,
						port)));

		MappingConnectCallback.put(mySocketChannel, callback);

	}

	/**
	 * Accept a connection and make it non-blocking
	 * 
	 * @param the
	 *            key of the channel on which a connection is requested
	 */
	private void handleAccept(SelectionKey key) {
		System.out.println("----------HandleAccept------------");
		SocketChannel socketChannel = null;
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key
				.channel();
		try
		{
		System.out.println("On va accepter se mettre en accept de connection");
		socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);

		System.out.println("On va enregistrer la connection");
		socketChannel.register(this.selector, SelectionKey.OP_READ);
		
		System.out.println("Enregistrement du callback");
		MyServer server = MappingServers.get(serverSocketChannel);
		AcceptCallback callback = server.getAcceptCallback();
		
		System.out.println("Création du channel et enregistrement dans la table");
		MyChannel myChannel = new MyChannel(socketChannel, this);
		MappingChannels.put(socketChannel, myChannel);

		System.out.println("Lancement du callback");
		callback.accepted(MappingServers.get(serverSocketChannel), myChannel);
		System.out.println("-----------------------------------");
		}
		catch(IOException e){
			System.out.println("IO Exception : "+e.getMessage());
		}
	}

	/**
	 * Finish to establish a connection
	 * 
	 * @param the
	 *            key of the channel on which a connection is requested
	 */
	private void handleConnection(SelectionKey key) {
		System.out.println("----------HandleConnection------------");
		SocketChannel socketChannel = (SocketChannel) key.channel();

		try {
			System.out.println("On réalise le finishConnect");
			socketChannel.finishConnect();
		} catch (IOException e) {
			// cancel the channel's registration with our selector
			System.out.println("IO Exception : " + e.getMessage());
			key.cancel();
			return;
		}
		System.out.println("On change l'interestOps de la clé");
		key.interestOps(SelectionKey.OP_READ);
		MyChannel myChannel = new MyChannel(socketChannel, this);
		MappingChannels.put(socketChannel, myChannel);
		MappingConnectCallback.get(socketChannel).connected(myChannel);
		System.out.println("-----------------------------------");
	}

	/**
	 * Handle incoming data event
	 * 
	 * @param the
	 *            key of the channel on which the incoming data waits to be
	 *            received
	 */
	private void handleRead(SelectionKey key) throws IOException {

		System.out.println("------------handleRead----------------");

		SocketChannel socketChannel = (SocketChannel) key.channel();

		MappingChannels.get(socketChannel).Automata_for_read();

		System.out.println("--------------------------------------");

	}

	/**
	 * Handle outgoing data event
	 * 
	 * @param the
	 *            key of the channel on which data can be sent
	 * @throws IOException
	 */
	private void handleWrite(SelectionKey key) throws IOException {

		SocketChannel socketChannel = (SocketChannel) key.channel();
		MyChannel myChannel = MappingChannels.get(socketChannel);
		boolean outBufferIsEmpty = myChannel.Automata_for_write();

		if (outBufferIsEmpty)
			key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);

	}

	public void askWrite(MyChannel myChannel) {
		try {
			myChannel.getChannel().register(this.selector,
					SelectionKey.OP_WRITE | SelectionKey.OP_READ);
		} catch (ClosedChannelException e) {
			e.printStackTrace();
		}
	}

}
