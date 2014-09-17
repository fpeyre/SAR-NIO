package nio.engine;

import java.io.IOException;



/**
 * This class wraps an accepted connection. It provides access to the port on
 * which the connection was accepted as well as the ability to close that
 */

public class MyServer extends NioServer {
	MyChannel channel;
	int port;

	public MyServer(int port) {
		super();
		this.channel = new MyChannel();
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
			this.channel.getChannel().close();
		} catch (IOException e) {
			System.out.println("Erreur close MyServer : "+e.getMessage());
		}

	}

}
