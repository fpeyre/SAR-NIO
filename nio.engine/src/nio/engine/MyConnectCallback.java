package nio.engine;

public class MyConnectCallback implements ConnectCallback {

	private ConnectCallback isClosed;
	private ConnectCallback isConnected;

	/**
	 * Callback to notify that a previously connected channel has been closed.
	 * 
	 * @param channel
	 */
	@Override
	public void closed(NioChannel channel) {
		if (!channel.getChannel().isOpen())
			{
			System.out.println("Notification qu'un channel s'est fermé");
			isClosed.notify();
			}
		

	}

	/**
	 * Callback to notify that a connection has succeeded.
	 * 
	 * @param channel
	 */
	@Override
	public void connected(NioChannel channel) {
		if (channel.getChannel().isConnected()) {
			System.out.println("Le channel est connecté");
			isConnected.notify();
		} 
	}

}
