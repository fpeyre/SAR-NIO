package nio.engine;

public class MyConnectCallback implements ConnectCallback {

	/**
	 * Callback to notify that a previously connected channel has been closed.
	 * 
	 * @param channel
	 */
	@Override
	public void closed(NioChannel channel) {
		if(!channel.getChannel().isOpen())
		{
			System.out.println("Le channel est fermé");
		}
		else 
		{
			System.out.println("Le channel est ouvert");
		}

	}

	/**
	 * Callback to notify that a connection has succeeded.
	 * 
	 * @param channel
	 */
	@Override
	public void connected(NioChannel channel) {
		if(channel.getChannel().isConnected())
		{
			System.out.println("Le channel est connecté");
		}
		else
		{
			System.out.println("Le channel est déconnecté");
		}

	}

}
