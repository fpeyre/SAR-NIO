package nio.engine;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * This class wraps an end-point of a channel. It allows to send and receive
 * messages, stored in ByteBuffers.
 */
public class MyChannel extends NioChannel {

	SocketChannel mySocketChannel;
	
	
	public MyChannel() {
		super();
		try {
			this.mySocketChannel = SocketChannel.open();
		} catch (IOException e) {
			System.out.println("Problème ouverture SocketChannel : "+e.getMessage());
		}
	}

	/**
	 * Get access to the underlying socket channel.
	 * 
	 * @return
	 */
	@Override
	public SocketChannel getChannel() {
		return mySocketChannel;
	}

	/**
	 * Set the callback to deliver messages to.
	 * 
	 * @param callback
	 */
	@Override
	public void setDeliverCallback(DeliverCallback callback) {

		String message = "Le Message Test";
		byte[] bytes = message.getBytes();
		ByteBuffer src = ByteBuffer.allocate(bytes.length + 8);
		src.put(bytes);
		callback.deliver((NioChannel)this,src);
	}

	/**
	 * Get the Inet socket address for the other side of this channel.
	 * 
	 * @return
	 */
	@Override
	public InetSocketAddress getRemoteAddress() {
		InetSocketAddress myAdress = null ;
		try {
			myAdress = (InetSocketAddress) mySocketChannel.getRemoteAddress();
		} catch (IOException e) {
			System.out.println("Problème récupération remoteAdress");
		}

		return myAdress;
	}
	
	  /**
	   * Send the given byte buffer. No copy is made, so the buffer 
	   * should no longer be used by the code sending it.
	   * @param buf
	   */
	@Override
	public void send(ByteBuffer buf) {

		try {
			this.mySocketChannel.write(buf);
		} catch (IOException e) {
			System.out.println("Problème envoi du buffer");
		}
	}

	  /**
	   * Sending the given byte array, a copy is made into internal buffers,
	   * so the array can be reused after sending it.
	   * @param bytes
	   * @param offset
	   * @param length
	   */
	@Override
	public void send(byte[] bytes, int offset, int length) {
		ByteBuffer src = ByteBuffer.allocate(bytes.length + 8);
		src.put(bytes, offset, length);

		try {
			this.mySocketChannel.write(src);
		} catch (IOException e) {
			System.out.println("Problème envoi du buffer");
		}
		
	}

	@Override
	public void close() {
		try {
			this.mySocketChannel.close();
		} catch (IOException e) {
			System.out.println("Tentative de fermeture du socketChannel : "+e.getMessage());
		}

	}

}
