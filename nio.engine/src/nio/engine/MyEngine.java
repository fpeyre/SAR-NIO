package nio.engine;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Hashtable;
import java.util.Iterator;

public class MyEngine extends NioEngine {

	Selector selector;
	// buffers for writing data
	Hashtable<SocketChannel,ByteBuffer> outBuffers;
	int port;
	int stateReadingLength=1;
	int stateReadingMsg=2;
	int stateWritingLength=3;
	int stateWritingMsg=4;
	int stateWriteDone=5;
	int currentReadState=stateReadingLength;
	int currentWriteState=stateWritingLength;
		
	public MyEngine(int port) throws Exception {
		super();
		
		this.port=port;
	
		this.selector = SelectorProvider.provider().openSelector();
		this.selector.wakeup();
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

				//System.out.println("Nombre de clé selectionnée au cours de la loop : "+selector.select());

				Iterator<?> selectedKeys = this.selector.selectedKeys().iterator();
				//System.out.println("Selector initialisé, Itérator défini, clé suivantes: "+selectedKeys.hasNext());
				//System.out.println("Boucle sur les selected Keys : ");
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
		//System.out.println("Fin du tour de boucle ");
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
		MyServer server = new MyServer(port);
		MyChannel channel = new MyChannel();
		channel.getChannel().configureBlocking(false);
		System.out.println("Listening on port " + port);
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		SocketAddress myAdress = new InetSocketAddress(port);
		serverChannel.socket().bind(myAdress);
		SocketChannel sc = channel.getChannel();
		System.out.println("Recupération du channel reussi, acceptation de connection");
		sc = serverChannel.accept();
		System.out.println("Connection acceptée");
		if (sc != null)
		{
			System.out.println("Serveur channel non null");
			sc.configureBlocking(false);
			SelectionKey key = sc.register(selector, SelectionKey.OP_READ);
			send(key, (new String("Plop").getBytes()));
			//sc.register(this.selector, SelectionKey.OP_ACCEPT);
			//callback.accepted(server, channel);
		}
			

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

		MyChannel channel = new MyChannel();
		SocketChannel mySocketChannel = channel.getChannel();
		mySocketChannel = SocketChannel.open();
		mySocketChannel.configureBlocking(false);

		//connection to the hostAddress at the given port
		mySocketChannel.connect(new InetSocketAddress(hostAddress, port));
		
		if (mySocketChannel.finishConnect())
		{
			//Si la connexion a reussi
			System.out.println("Connection à la socket reussi");
			mySocketChannel.register(this.selector, SelectionKey.OP_CONNECT);
			ByteBuffer bb = ByteBuffer.wrap("Test message".getBytes()); 
			outBuffers.put(mySocketChannel, bb);
			System.out.println("Socket Enregistré, appel au callback(callback commenté)");
			//callback.connected(channel);
		}
	}
	
	/**
	 * Accept a connection and make it non-blocking
	 * @param the key of the channel on which a connection is requested
	 */
	private void handleAccept(SelectionKey key) {
		System.out.println("Server[HandleAccept]");
		SocketChannel socketChannel = null;
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		try {
			socketChannel = serverSocketChannel.accept();
			socketChannel.configureBlocking(false);
		} catch (IOException e) {
			// as if there was no accept done
			return;
		}

		// be notified when there is incoming data 
		try {
			socketChannel.register(this.selector, SelectionKey.OP_READ);
		} catch (ClosedChannelException e) {
			handleClose(socketChannel);
		}
		outBuffers.put(socketChannel, ByteBuffer.allocate(128));
	}


	/**
	 * Finish to establish a connection
	 * @param the key of the channel on which a connection is requested
	 */
	private void handleConnection(SelectionKey key) {
		System.out.println("Server[HandleConnection]");
		SocketChannel socketChannel = (SocketChannel) key.channel();

		try {
			socketChannel.finishConnect();
		} catch (IOException e) {
			// cancel the channel's registration with our selector
			System.out.println(e);
			key.cancel();
			return;
		}
		key.interestOps(SelectionKey.OP_READ);	
	}


	/**
	 * Close a channel 
	 * @param the key of the channel to close
	 */
	private void handleClose(SocketChannel socketChannel) {
		System.out.println("Server[HandleClose]");
		try{
			socketChannel.close();
		} catch (IOException e) {
			// nothing to do, the channel is already closed
		}
		socketChannel.keyFor(selector).cancel();
	}

	/**
	 * Handle incoming data event
	 * @param the key of the channel on which the incoming data waits to be received 
	 */
	private void handleRead(SelectionKey key) throws IOException{
		
		SocketChannel sChannel = (SocketChannel) key.channel();
		ByteBuffer inBuffer=null;
		ByteBuffer lenBuffer=ByteBuffer.allocate(4);
		int nbRead;
		
		if(currentReadState==stateReadingLength){
			nbRead=0;
			
			try{
			nbRead=sChannel.read(lenBuffer);
			}catch (IOException e) {
				key.cancel();
				sChannel.close();
				System.out.println(e);
				return;
			}			
			if(nbRead == -1) {
				key.channel().close();
				key.cancel();
				return;
			}
			if(lenBuffer.remaining()==0){
				int length= lenBuffer.getInt();
				inBuffer = ByteBuffer.allocate(length);
				lenBuffer.position(0);
				currentReadState=stateReadingMsg;				
			}			
		}else if(currentReadState==stateReadingMsg){		
		nbRead=0;		
		try {
			 nbRead=sChannel.read(inBuffer);
		} catch (IOException e) {
			key.cancel();
			sChannel.close();
			System.out.println(e);
			return;
		}
		
		if(nbRead == -1) {
			key.channel().close();
			key.cancel();
			return;
		}}
		if (inBuffer.remaining()==0){
		System.out.println("Server[HandleRead]--> "+inBuffer.toString());
		send(key, inBuffer.array());
		}
		
	}


	/**
	 * Handle outgoing data event
	 * @param the key of the channel on which data can be sent 
	 * @throws IOException 
	 */
	private void handleWrite(SelectionKey key) throws IOException {
		
		SocketChannel sChannel = (SocketChannel) key.channel();

		ByteBuffer outBuffer = outBuffers.get(sChannel);
		ByteBuffer outLength=ByteBuffer.allocate(4);
		int nbWrite=0;
		
		if(currentWriteState==stateWritingLength){
			sChannel.write(outLength);
			if(outLength.remaining()==0)
			{
				currentWriteState=stateWritingMsg;
			}
		}
		else if (currentWriteState==stateWritingMsg){
			
		if(outBuffer.remaining() > 0) {			
			try {
				nbWrite = sChannel.write(outBuffer);
			} catch (IOException e) {
				key.cancel();
				sChannel.close();
				e.printStackTrace();
				return;
			}
			if(outBuffer.remaining()==0){
				currentWriteState=stateWriteDone;
			}

		}
	}
		
		if(currentWriteState==stateWriteDone){
		System.out.println("Server[HandleWrite]--> "+outBuffer.toString());
		outBuffer.clear();
		key.interestOps(SelectionKey.OP_READ);
		}
		
	}
	
public void send(SelectionKey key, byte[] data) {
		
		ByteBuffer outBuffer = outBuffers.get(key.channel()); 
		//outBuffers null, probleme de mapping du channel avec son buffer
		outBuffer = ByteBuffer.wrap(data);
		key.interestOps(SelectionKey.OP_WRITE);
		
	}
}
