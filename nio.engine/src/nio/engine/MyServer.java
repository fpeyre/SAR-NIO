package nio.engine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;



/**
 * This class wraps an accepted connection. It provides access to the port on
 * which the connection was accepted as well as the ability to close that
 */

public class MyServer extends NioServer {
	int port;
	public MyServer(int port) {
		super();
		this.port = port;
	}

	/**
	 * @return the port onto which connections are accepted.
	 */
	@Override
	public int getPort() {
		return port;
	}
	
	  /**
	   * Close the server port, no longer accepting connections.
	   */
	@Override
	public void close() {
		try {
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			SocketAddress myAdress = new InetSocketAddress(this.port);
			serverChannel.socket().bind(myAdress);
			serverChannel.close();
		} catch (IOException e) {
			System.out.println("Erreur close MyServer : "+e.getMessage());
		}

	}

}
