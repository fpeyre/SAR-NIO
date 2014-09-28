package TestJunit;

import static org.junit.Assert.*;

import java.net.InetAddress;

import nio.engine.AcceptCallback;

import org.junit.Test;

import ImplemClasses.MyEngine;

public class MyEngineTest {

	@Test
	public void testMainloop() {
		fail("Not yet implemented");
	}

	@Test
	public void testListen() throws Exception {
		MyEngine testEngine=new MyEngine();
		AcceptCallback testCallback = null;
		testEngine.listen(1024, testCallback);
		assertTrue("Ajout du serveur a MappingServers",!testEngine.getMappingServers().isEmpty());
	}

	@Test
	public void testConnect() throws Exception {
		MyEngine testEngine=new MyEngine();
		AcceptCallback testCallback = null;
		InetAddress testAdress=new InetAddress();
		testEngine.listen(1024, testCallback);
		assertTrue("Ajout du serveur a MappingServers",!testEngine.getMappingServers().isEmpty());
	}

	@Test
	public void testMyEngine() {
		fail("Not yet implemented");
	}

	@Test
	public void testAskWrite() {
		fail("Not yet implemented");
	}

}
