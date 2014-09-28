package TestJunit;

import static org.junit.Assert.*;

import java.net.InetAddress;

import nio.engine.AcceptCallback;
import nio.engine.ConnectCallback;

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
		testEngine.setMappingServers(null);
		testEngine.listen(3021, testCallback);
		assertTrue("Ajout du serveur a MappingServers",!testEngine.getMappingServers().isEmpty());
	}

	@Test
	public void testConnect() throws Exception {
		MyEngine testEngine=new MyEngine();
		ConnectCallback testCallback = null;
		String adresse ="localhost";
		InetAddress testAdress=null;
		testAdress.getByName(adresse);
		
		testEngine.connect(testAdress,3021,testCallback);
		assertTrue("Ajout du socketChannel a MappingConnectCallback",!testEngine.getMappingConnectCallback().isEmpty());
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
