package TestJunit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

import org.junit.Test;

import ImplemClasses.MyServer;

public class MyServerTest {

	@Test
	public void testGetPort() throws IOException {
		ServerSocketChannel testServerSocketChannel=ServerSocketChannel.open();
		MyServer serverTest=new MyServer(testServerSocketChannel);
		assertTrue("retourne le bon port",serverTest.getPort()==testServerSocketChannel.socket().getLocalPort());
	}

	@Test
	public void testClose() throws IOException {
		ServerSocketChannel testServerSocketChannel=ServerSocketChannel.open();
		MyServer serverTest=new MyServer(testServerSocketChannel);
		serverTest.close();
		assertTrue("socket serveur fermé",!testServerSocketChannel.isOpen());
	}

	@Test
	public void testMyServer() {
		fail("Not yet implemented");
	}

	@Test
	public void testMyServerServerSocketChannel() {
		fail("Not yet implemented");
	}

	@Test
	public void testMyServerServerSocketChannelAcceptCallback() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetSSC() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetAcceptCallback() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAcceptCallback() {
		fail("Not yet implemented");
	}

}
