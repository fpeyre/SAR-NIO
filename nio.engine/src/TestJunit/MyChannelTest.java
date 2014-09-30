package TestJunit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.junit.Test;

import ImplemClasses.MyChannel;
import ImplemClasses.MyEngine;

public class MyChannelTest {

	@Test
	public void testGetChannel() throws Exception {
		SocketChannel sockTest=SocketChannel.open();
		MyEngine testEngine=new MyEngine();
		MyChannel testChannel=new MyChannel(sockTest,testEngine);
		assertTrue("renvoi du socket channel",testChannel.getChannel()==sockTest);
	}

	@Test
	public void testSetDeliverCallback() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRemoteAddress() throws Exception {
		SocketChannel sockTest=SocketChannel.open();
		MyEngine testEngine=new MyEngine();
		MyChannel testChannel=new MyChannel(sockTest,testEngine);
		assertTrue("renvoi de l'adresse",testChannel.getRemoteAddress()==(InetSocketAddress)sockTest.getRemoteAddress());
		
	}

	@Test
	public void testSendByteBuffer() throws Exception {
		//Testé via testSendByteArrayIntInt	
		fail("Not yet implemented");
	}

	@Test
	public void testSendByteArrayIntInt() throws Exception {
		SocketChannel sockTest=SocketChannel.open();
		MyEngine testEngine=new MyEngine();
		MyChannel testChannel=new MyChannel(sockTest,testEngine);
		String message = "Message test";
		
		testChannel.send(message.getBytes(),0,message.getBytes().length);
		assertTrue("buffersortie non vide",!testChannel.getBufferSortie().isEmpty());
	}

	@Test
	public void testClose() throws Exception {
		SocketChannel sockTest=SocketChannel.open();
		MyEngine testEngine=new MyEngine();
		MyChannel testChannel=new MyChannel(sockTest,testEngine);
		testChannel.close();
		assertTrue("fermeture socket",!sockTest.isOpen());
	}

	@Test
	public void testMyChannel() {
		fail("Not yet implemented");
	}

	@Test
	public void testAutomata_for_read() {
		fail("Not yet implemented");
	}

	@Test
	public void testAutomata_for_write() {
		fail("Not yet implemented");
	}

}
