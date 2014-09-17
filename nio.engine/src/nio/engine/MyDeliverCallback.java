package nio.engine;

import java.nio.ByteBuffer;

public class MyDeliverCallback implements DeliverCallback {

	  /**
	   * Callback to notify that a message has been received.
	   * The message is whole, all bytes have been accumulated.
	   * @param channel
	   * @param bytes
	   */
	@Override
	public void deliver(NioChannel channel, ByteBuffer bytes) {
		channel.send(bytes);
		
	}

}
