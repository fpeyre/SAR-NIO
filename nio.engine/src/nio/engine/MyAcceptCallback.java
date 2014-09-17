package nio.engine;

public class MyAcceptCallback implements AcceptCallback {

	/**
	 * Callback to notify about an accepted connection.
	 * 
	 * @param server
	 * @param channel
	 */
	@Override
	public void accepted(NioServer server, NioChannel channel) {
		// TODO Auto-generated method stub

	}

	/**
	 * Callback to notify that a previously accepted channel has been closed.
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

}
