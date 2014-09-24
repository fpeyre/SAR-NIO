package ImplemClasses;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

import nio.engine.*;



/**
 * This class wraps an accepted connection. It provides access to the port on
 * which the connection was accepted as well as the ability to close that
 */



public class MyServer extends NioServer {
	
	ServerSocketChannel myServerSocketChannel;
	AcceptCallback myCallback;
	
	public MyServer() {
		super();
	}

	/**
	 * @return the port onto which connections are accepted.
	 */
	@Override
	public int getPort() {
		return myServerSocketChannel.socket().getLocalPort();
	}
	
	  /**
	   * Close the server port, no longer accepting connections.
	   */
	@Override
	public void close() {
		try {
			this.myServerSocketChannel.close();
		} catch (IOException e) {
			System.out.println("Erreur close MyServer : "+e.getMessage());
		}

	}
	
	public void setSSC(ServerSocketChannel ssc){
		this.myServerSocketChannel=ssc;
	}
	
	public void setAcceptCallback(AcceptCallback cb){
		this.myCallback=cb;
	}

	public AcceptCallback getAcceptCallback(){
		return myCallback;
	}

}
