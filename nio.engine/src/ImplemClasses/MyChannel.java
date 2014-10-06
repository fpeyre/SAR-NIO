package ImplemClasses;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedList;

import nio.engine.*;

enum StatesForReading {
	stateReadingDone, stateReadingLength, stateReadingMsg
}

enum StatesForWriting {
	stateWritingDone, stateWritingLength, stateWritingMsg
}

/**
 * This class wraps an end-point of a channel. It allows to send and receive
 * messages, stored in ByteBuffers.
 */

public class MyChannel extends NioChannel {

	SocketChannel mySocketChannel;
	MyEngine myEngine;
	DeliverCallback callback;

	
	private LinkedList<ByteBuffer> bufferSortie;
	private ByteBuffer bufferSortieCourant;
	private ByteBuffer longueurBufferSortie;
	private ByteBuffer lengthBuffer;
	private ByteBuffer readingBuffer = null;

	StatesForReading currentReadState = StatesForReading.stateReadingDone;
	StatesForWriting currentWriteState = StatesForWriting.stateWritingDone;

	public MyChannel(SocketChannel mySocketChannel, MyEngine myEngine) {
		super();
		this.mySocketChannel = mySocketChannel;
		this.myEngine = myEngine;
		longueurBufferSortie = ByteBuffer.allocate(4);
		lengthBuffer= ByteBuffer.allocate(4);
		setBufferSortie(new LinkedList<ByteBuffer>());
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

		this.callback = callback;
	}

	/**
	 * Get the Inet socket address for the other side of this channel.
	 * 
	 * @return
	 */
	@Override
	public InetSocketAddress getRemoteAddress() {
		InetSocketAddress myAdress = null;
		try {
			myAdress = (InetSocketAddress) mySocketChannel.getRemoteAddress();
		} catch (IOException e) {
			System.out.println("Probl�me r�cup�ration remoteAdress");
		}

		return myAdress;
	}

	/**
	 * Send the given byte buffer. No copy is made, so the buffer should no
	 * longer be used by the code sending it.
	 * 
	 * @param buf
	 */
	@Override
	public void send(ByteBuffer buf) {

			getBufferSortie().add(buf);
			myEngine.askWrite(this);
		
	}

	/**
	 * Sending the given byte array, a copy is made into internal buffers, so
	 * the array can be reused after sending it.
	 * 
	 * @param bytes
	 * @param offset
	 * @param length
	 */
	@Override
	public void send(byte[] bytes, int offset, int length) {

		if(bytes.length <= length && offset < bytes.length){
		int i;
		ByteBuffer src = ByteBuffer.allocate(length);

		for (i = offset; i < offset + length; i++) {
			src.put(bytes[i]);
		}
		src.toString();
		send(src);
		}
		else {
			NioEngine.panic("Erreur methode send(byte[] bytes, int offset, int length): condition bytes.length <= length && offset < bytes.length non respectee");
		}

	}

	@Override
	public void close() {
		try {
			this.mySocketChannel.close();
		} catch (IOException e) {
			System.out.println("Tentative de fermeture du socketChannel : "
					+ e.getMessage());
		}

	}

	public void Automata_for_read() {
		if (currentReadState == StatesForReading.stateReadingDone)// R�initialisation
																	// des
																	// buffers
		{
			readingBuffer = null;
			lengthBuffer.position(0); // On Repositionne le buffer
			currentReadState = StatesForReading.stateReadingLength;
		}

		if (currentReadState == StatesForReading.stateReadingLength) { // On lit
																		// la
																		// taille
																		// du
																		// message
			try {
				mySocketChannel.read(lengthBuffer);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			if (lengthBuffer.remaining() == 0) {
				lengthBuffer.position(0);
				int messageLenght = lengthBuffer.getInt();
				readingBuffer = ByteBuffer.allocate(messageLenght);
				currentReadState = StatesForReading.stateReadingMsg;
			}
		}

		if (currentReadState == StatesForReading.stateReadingMsg) {
			try {
				mySocketChannel.read(readingBuffer);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			if (readingBuffer.remaining() == 0) {
				callback.deliver(this, readingBuffer.duplicate()); // Risque de
																	// perte du
																	// message
																	// si non
																	// dupliqu�

				currentReadState = StatesForReading.stateReadingDone;
			}

		}

	}
	
	
	public boolean Automata_for_write() {
		//Si il n'y a plus rien � envoy�, on retourne true
		if(currentWriteState == StatesForWriting.stateWritingDone)
		{
			bufferSortieCourant = getBufferSortie().pop();
			bufferSortieCourant.position(0);
			longueurBufferSortie.position(0);
			longueurBufferSortie.putInt(bufferSortieCourant.capacity());
			longueurBufferSortie.position(0);
			currentWriteState = StatesForWriting.stateWritingLength;
		}
		
		if (currentWriteState == StatesForWriting.stateWritingLength) { 
			try {
				mySocketChannel.write(longueurBufferSortie);
			} catch (IOException e) {
				System.out.println("Erreur IO : "+e.getMessage());
			}
			if(longueurBufferSortie.remaining()==0){
				currentWriteState = StatesForWriting.stateWritingMsg;
			}
		}
		
		if(currentWriteState == StatesForWriting.stateWritingMsg)
		{
			try {
				mySocketChannel.write(bufferSortieCourant);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			if( bufferSortieCourant.remaining() == 0 ) {
				currentWriteState = StatesForWriting.stateWritingDone;
			}
		}
		

		return getBufferSortie().size()==0;
	}

	public LinkedList<ByteBuffer> getBufferSortie() {
		return bufferSortie;
	}

	public void setBufferSortie(LinkedList<ByteBuffer> bufferSortie) {
		this.bufferSortie = bufferSortie;
	}
	

}
