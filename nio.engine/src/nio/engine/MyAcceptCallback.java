package nio.engine;


public class MyAcceptCallback implements AcceptCallback {

	AcceptCallback accepted;
	AcceptCallback closed;
	
	/**
	 * Callback to notify about an accepted connection.
	 * 
	 * @param server
	 * @param channel
	 */
	@Override
	public void accepted(NioServer server, NioChannel channel) {
		// Condition du accepted entre server et channel à faire
		
		//Notification du status accepted
		

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
			closed.notify();
		}

	}

}
