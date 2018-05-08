/*
 * Created on May 14, 2008
 */
package zz.utils.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import zz.utils.ArrayStack;
import zz.utils.PipedInputStream2;
import zz.utils.PipedOutputStream2;
import zz.utils.Stack;
import zz.utils.Utils;

/**
 * Manages socket multiplexing.
 * 
 * @author gpothier
 */
public class MultiplexingManager
{
	enum Command 
	{
		CMD_DATA, 
		CONNECT, 
		CONNECTION_ACK,
		CLOSE,
		CLOSE_ACK;

		/**
		 * Cached values; call to values() is costly.
		 */
		public static final Command[] VALUES = values();

	};

	/**
	 * The logical host name assigned to this manager.
	 */
	private final String itsHostName;
	
	private int itsNextFreePort = 0x10000;

	private Stack<Integer> itsFreedPorts = new ArrayStack<Integer>();

	/**
	 * Maps host names to socket managers
	 */
	private final Map<String, SocketManager> itsHostsMap = new HashMap<String, SocketManager>();

	/**
	 * Maps port numbers to virtual sockets.
	 */
	private final Map<Integer, VirtualSocket> itsBoundSockets = new HashMap<Integer, VirtualSocket>();

	private final Map<Integer, VirtualServerSocket> itsListeningSockets = new HashMap<Integer, VirtualServerSocket>();

	public MultiplexingManager(String aHostName)
	{
		itsHostName = aHostName;
	}
	
	/**
	 * The logical name assigned to this host.
	 */
	public String getLocalHostName()
	{
		return itsHostName;
	}
	

	public void addSocket(String aHostName, Socket aSocket) throws IOException
	{
		itsHostsMap.put(aHostName, new SocketManager(aHostName, aSocket));
	}

	private void socketClosed(String aHostName)
	{
		itsHostsMap.remove(aHostName);
	}

	/**
	 * Creates a virtual socket that connects to the specified (virtual) port on
	 * the other end of the actual socket.
	 */
	public Socket createSocket(String aHostName, int aPort) throws IOException
	{
		if (itsHostName.equals(aHostName) 
				|| "localhost".equals(aHostName) 
				|| aHostName == null 
				|| aHostName.length() == 0)
		{
			// For local connections, we create VirtualLocalSockets
			
			VirtualServerSocket theServerSocket = itsListeningSockets.get(aHostName);
			if (theServerSocket == null) throw new IOException("Connection refused on port "+aPort);
			
			VirtualLocalSocket theLocal = new VirtualLocalSocket(0, 0);
			VirtualLocalSocket theRemote = new VirtualLocalSocket(0, 0);
			theLocal.connect(theRemote);
			
			theServerSocket.connected(theRemote);
			return theLocal;
		}
		
		SocketManager theSocketManager = itsHostsMap.get(aHostName);
		if (theSocketManager == null) throw new UnknownHostException(aHostName);
		VirtualSocket theSocket = theSocketManager.connect(aPort);
		if (theSocket == null) throw new IOException("Connection refused to " + aHostName + ":" + aPort);
		return theSocket;
	}

	/**
	 * Creates a virtual server socket that accepts incoming connection on the
	 * specified (virtual) port.
	 * 
	 * @throws IOException
	 */
	public ServerSocket createServerSocket(int aPort) throws IOException
	{
		System.out.println("Creating server socket on port: " + aPort);
		VirtualServerSocket theServerSocket = itsListeningSockets.get(aPort);
		if (theServerSocket != null) throw new IOException("Address already in use: " + aPort);

		theServerSocket = new VirtualServerSocket(aPort);
		itsListeningSockets.put(aPort, theServerSocket);

		return theServerSocket;
	}

	private synchronized void portFreed(int aPort)
	{
		itsFreedPorts.push(aPort);
	}

	private synchronized int getNextFreePort()
	{
		if (itsFreedPorts.isEmpty()) return itsNextFreePort++;
		else return itsFreedPorts.pop();
	}

	/**
	 * Manager for a single actual socket.
	 * 
	 * @author gpothier
	 */
	private class SocketManager extends Thread
	{
		private final String itsHostName;
		private final Socket itsSocket;
		private final DataInputStream itsIn;
		private final DataOutputStream itsOut;
		
		private final byte[] itsDataBuffer = new byte[8192];

		/**
		 * Map of pending connections. Keys are local ports.
		 */
		private final Map<Integer, PendingConnectionData> itsPendingConnections = new HashMap<Integer, PendingConnectionData>();

		protected SocketManager(String aHostName, Socket aSocket) throws IOException
		{
			super("SocketManager");
			setDaemon(true);
			itsHostName = aHostName;
			itsSocket = aSocket;
			itsIn = new DataInputStream(new BufferedInputStream(itsSocket.getInputStream()));
			itsOut = new DataOutputStream(new BufferedOutputStream(itsSocket.getOutputStream()));

			start();
		}

		public String getHostName()
		{
			return itsHostName;
		}

		@Override
		public void run()
		{
			try
			{
				while (!itsSocket.isClosed())
				{
					Command theCommand = Command.VALUES[itsIn.readByte()];
					switch (theCommand)
					{
					case CMD_DATA:
						readData();
						break;
					case CONNECT:
						readConnect();
						break;
					case CONNECTION_ACK:
						readConnectionAck();
						break;
					case CLOSE:
						readClose();
						break;
					case CLOSE_ACK:
						readCloseAck();
						break;

					default:
						throw new RuntimeException("Not handled: " + theCommand);
					}
				}
			}
			catch (EOFException e)
			{
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				socketClosed(itsHostName);
			}
		}

		private void sendCommand(Command aCommand) throws IOException
		{
			itsOut.writeByte(aCommand.ordinal());
		}

		private synchronized void sendConnect(int aLocalPort, int aDestListenPort) throws IOException
		{
			sendCommand(Command.CONNECT);
			itsOut.writeInt(aLocalPort);
			itsOut.writeInt(aDestListenPort);
			itsOut.flush();
		}

		private synchronized void sendConnectionAck(int aRemotePort, int aLocalPort) throws IOException
		{
			sendCommand(Command.CONNECTION_ACK);
			itsOut.writeInt(aRemotePort);
			itsOut.writeInt(aLocalPort);
			itsOut.flush();
		}

		protected synchronized void sendData(int aVirtualPort, byte[] aBuffer, int aOff, int aLen) throws IOException
		{
			sendCommand(Command.CMD_DATA);
			itsOut.writeInt(aVirtualPort);
			itsOut.writeInt(aLen);
			itsOut.write(aBuffer, aOff, aLen);
		}

		protected synchronized void sendClose(int aRemotePort) throws IOException
		{
			sendCommand(Command.CLOSE);
			itsOut.writeInt(aRemotePort);
			itsOut.flush();
		}
		
		protected synchronized void sendCloseAck(int aRemotePort) throws IOException
		{
			sendCommand(Command.CLOSE_ACK);
			itsOut.writeInt(aRemotePort);
			itsOut.flush();
		}
		
		protected void flush() throws IOException
		{
			itsOut.flush();
		}
		
		private void readData() throws IOException
		{
			int theVirtualPort = itsIn.readInt();
			int theLength = itsIn.readInt();
			
			VirtualSocket theSocket = itsBoundSockets.get(theVirtualPort);
			if (theSocket == null)
			{
				System.err.println("Skipping data sent to unbound virtual port " + theVirtualPort);
				while (theLength > 0)
					theLength -= itsIn.skip(theLength);
			}
			else
			{
				Utils.pipe(itsDataBuffer, itsIn, theSocket.getSink(), theLength);
				theSocket.getSink().flush();
			}
		}

		/**
		 * Attempts a connection to the given virtual port.
		 * 
		 * @return If the connection is successful, a positive integer that
		 *         indicates the virtual port of the connection. Otherwise, 0.
		 * @throws IOException
		 */
		protected VirtualSocket connect(int aVirtualPort) throws IOException
		{
			// Allocate local port
			int theLocalPort = getNextFreePort();
			PendingConnectionData theData = new PendingConnectionData(theLocalPort, aVirtualPort);
			itsPendingConnections.put(theLocalPort, theData);

			sendConnect(theLocalPort, aVirtualPort);

			// Wait for peer to accept or reject the connection.
			int theRemotePort;
			try
			{
				theRemotePort = theData.waitConnected();
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException("Interrupted while connecting", e);
			}

			if (theRemotePort == 0)
			{
				portFreed(theLocalPort);
				return null;
			}
			else
			{
				VirtualSocket theSocket = new VirtualSocket(this, theLocalPort, theRemotePort);
				itsBoundSockets.put(theLocalPort, theSocket);
				return theSocket;
			}
		}

		private void readConnectionAck() throws IOException
		{
			int theLocalPort = itsIn.readInt();
			int theRemotePort = itsIn.readInt();

			PendingConnectionData theData = itsPendingConnections.remove(theLocalPort);
			if (theData == null || theData.localPort != theLocalPort) { throw new RuntimeException(
					"Received invalid accept"); }

			theData.setRemotePort(theRemotePort);
		}

		private void readConnect() throws IOException
		{
			int theRemotePort = itsIn.readInt();
			int theListenPort = itsIn.readInt();

			VirtualServerSocket theServerSocket = itsListeningSockets.get(theListenPort);
			if (theServerSocket == null)
			{
				sendConnectionAck(theRemotePort, 0);
				return;
			}
			else
			{
				int theLocalPort = getNextFreePort();

				VirtualSocket theClientSocket = new VirtualSocket(this, theLocalPort, theRemotePort);
				itsBoundSockets.put(theLocalPort, theClientSocket);

				sendConnectionAck(theRemotePort, theLocalPort);
				theServerSocket.connected(theClientSocket);
			}
		}
		
		/**
		 * Called when a local socket is closed.
		 */
		protected void close(VirtualSocket aSocket) throws IOException
		{
			sendClose(aSocket.getRemotePort());
		}
		
		private void readClose() throws IOException
		{
			int theLocalPort = itsIn.readInt();
			VirtualSocket theSocket = itsBoundSockets.remove(theLocalPort);
			if (theSocket == null) throw new RuntimeException("Attempted to close non-existing socket: "+theLocalPort);
			sendCloseAck(theSocket.getRemotePort());
		}
		
		private void readCloseAck() throws IOException
		{
			int theLocalPort = itsIn.readInt();
			VirtualSocket theSocket = itsBoundSockets.remove(theLocalPort);
			if (theSocket == null) throw new RuntimeException("Closed non-existing socket: "+theLocalPort);
		}
	}

	/**
	 * Represents a pending connection request
	 * 
	 * @author gpothier
	 */
	private static class PendingConnectionData
	{
		public final int localPort;

		public final int remoteListenPort;

		public int itsRemotePort = -1;

		public PendingConnectionData(int aLocalPort, int aListenRemotePort)
		{
			localPort = aLocalPort;
			remoteListenPort = aListenRemotePort;
		}

		/**
		 * Sets the remote port of the connection, once known. Awakes
		 * {@link #waitConnected()}.
		 */
		public synchronized void setRemotePort(int aPort)
		{
			itsRemotePort = aPort;
			notifyAll();
		}

		/**
		 * Waits until the remote end accepts (or refuses) the connection.
		 * Returns 0 if refused, otherwise the remote port to use for the
		 * connection.
		 */
		public synchronized int waitConnected() throws InterruptedException
		{
			while (itsRemotePort == -1)
				wait();
			return itsRemotePort;
		}
	}

	private class MultiplexedOutputStream extends OutputStream
	{
		private final SocketManager itsSocketManager;
		private final int itsRemotePort;
		
		private boolean itsClosed = false;

		public MultiplexedOutputStream(SocketManager aSocketManager, int aVirtualPort)
		{
			itsSocketManager = aSocketManager;
			itsRemotePort = aVirtualPort;
		}
		
		public int getRemotePort()
		{
			return itsRemotePort;
		}

		@Override
		public void write(byte[] aB, int aOff, int aLen) throws IOException
		{
			if (itsClosed) throw new IOException("Stream closed");
			itsSocketManager.sendData(itsRemotePort, aB, aOff, aLen);
		}

		@Override
		public void write(int aB) throws IOException
		{
			write(new byte[]
			{ (byte) aB });
		}
		
		@Override
		public void flush() throws IOException
		{
			itsSocketManager.flush();
		}
		
		@Override
		public void close() throws IOException
		{
			itsClosed = true;
		}

	}

	private class VirtualSocket extends Socket
	{
		private final SocketManager itsSocketManager;
		private final int itsLocalPort;
		private final InetAddress itsRemoteAddress;
		private final MultiplexedOutputStream itsOutputStream;
		private final PipedInputStream itsInputStream = new PipedInputStream();
		private final PipedOutputStream itsSink;
		
		private boolean itsClosed = false;
		
		public VirtualSocket(SocketManager aSocketManager, int aLocalPort, int aRemotePort) throws IOException
		{
			itsSocketManager = aSocketManager;
			itsLocalPort = aLocalPort;
			itsRemoteAddress = InetAddress.getByAddress(
					aSocketManager.getHostName(), 
					new byte[] { 0, 0, 0, 0 });
			
			itsOutputStream = new MultiplexedOutputStream(aSocketManager, aRemotePort);
			itsSink = new PipedOutputStream(itsInputStream);
		}

		public int getRemotePort()
		{
			return itsOutputStream.getRemotePort();
		}
		
		PipedOutputStream getSink()
		{
			return itsSink;
		}

		@Override
		public InputStream getInputStream() throws IOException
		{
			return itsInputStream;
		}

		@Override
		public OutputStream getOutputStream() throws IOException
		{
			return itsOutputStream;
		}

		@Override
		public synchronized void close() throws IOException
		{
			itsOutputStream.close();
			itsSink.close();
			itsClosed = true;
			itsSocketManager.close(this);
		}

		@Override
		public boolean isBound()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isClosed()
		{
			return itsClosed;
		}

		@Override
		public void bind(SocketAddress aBindpoint) throws IOException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void connect(SocketAddress aEndpoint, int aTimeout) throws IOException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void connect(SocketAddress aEndpoint) throws IOException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public SocketChannel getChannel()
		{
			return null;
		}

		@Override
		public InetAddress getInetAddress()
		{
			return itsRemoteAddress;
		}

		@Override
		public boolean getKeepAlive() throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public InetAddress getLocalAddress()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public int getLocalPort()
		{
			return itsLocalPort;
		}

		@Override
		public SocketAddress getLocalSocketAddress()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean getOOBInline() throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public int getPort()
		{
			return getRemotePort();
		}

		@Override
		public synchronized int getReceiveBufferSize() throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public SocketAddress getRemoteSocketAddress()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean getReuseAddress() throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public synchronized int getSendBufferSize() throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public int getSoLinger() throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public synchronized int getSoTimeout() throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean getTcpNoDelay() throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public int getTrafficClass() throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isConnected()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isInputShutdown()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isOutputShutdown()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void sendUrgentData(int aData) throws IOException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void setKeepAlive(boolean aOn) throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void setOOBInline(boolean aOn) throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void setPerformancePreferences(int aConnectionTime, int aLatency, int aBandwidth)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public synchronized void setReceiveBufferSize(int aSize) throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void setReuseAddress(boolean aOn) throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public synchronized void setSendBufferSize(int aSize) throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void setSoLinger(boolean aOn, int aLinger) throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public synchronized void setSoTimeout(int aTimeout) throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void setTcpNoDelay(boolean aOn) throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void setTrafficClass(int aTc) throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void shutdownInput() throws IOException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void shutdownOutput() throws IOException
		{
			throw new UnsupportedOperationException();
		}
	}

	private class VirtualLocalSocket extends Socket
	{
		private final int itsLocalPort;
		private final int itsRemotePort;
		private final InetAddress itsRemoteAddress;
		
		private final PipedInputStream2 itsInputStream = new PipedInputStream2();
		private final PipedOutputStream2 itsOutputStream = new PipedOutputStream2();
		
		private boolean itsClosed = false;
		
		public VirtualLocalSocket(int aLocalPort, int aRemotePort) throws IOException
		{
			itsLocalPort = aLocalPort;
			itsRemotePort = aRemotePort;
			itsRemoteAddress = InetAddress.getByAddress(
					null, 
					new byte[] { 0, 0, 0, 0 });
		}
		
		public void connect(VirtualLocalSocket aPeer) throws IOException
		{
			itsInputStream.connect(aPeer.itsOutputStream);
			itsOutputStream.connect(aPeer.itsInputStream);
		}
		
		public int getRemotePort()
		{
			return itsRemotePort;
		}
		
		@Override
		public InputStream getInputStream() throws IOException
		{
			return itsInputStream;
		}
		
		@Override
		public OutputStream getOutputStream() throws IOException
		{
			return itsOutputStream;
		}
		
		@Override
		public synchronized void close() throws IOException
		{
			itsOutputStream.close();
			itsInputStream.close();
			itsClosed = true;
		}
		
		@Override
		public boolean isBound()
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean isClosed()
		{
			return itsClosed;
		}
		
		@Override
		public void bind(SocketAddress aBindpoint) throws IOException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void connect(SocketAddress aEndpoint, int aTimeout) throws IOException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void connect(SocketAddress aEndpoint) throws IOException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public SocketChannel getChannel()
		{
			return null;
		}
		
		@Override
		public InetAddress getInetAddress()
		{
			return itsRemoteAddress;
		}
		
		@Override
		public boolean getKeepAlive() throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public InetAddress getLocalAddress()
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public int getLocalPort()
		{
			return itsLocalPort;
		}
		
		@Override
		public SocketAddress getLocalSocketAddress()
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean getOOBInline() throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public int getPort()
		{
			return getRemotePort();
		}
		
		@Override
		public synchronized int getReceiveBufferSize() throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public SocketAddress getRemoteSocketAddress()
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean getReuseAddress() throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public synchronized int getSendBufferSize() throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public int getSoLinger() throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public synchronized int getSoTimeout() throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean getTcpNoDelay() throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public int getTrafficClass() throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean isConnected()
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean isInputShutdown()
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean isOutputShutdown()
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void sendUrgentData(int aData) throws IOException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setKeepAlive(boolean aOn) throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setOOBInline(boolean aOn) throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setPerformancePreferences(int aConnectionTime, int aLatency, int aBandwidth)
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public synchronized void setReceiveBufferSize(int aSize) throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setReuseAddress(boolean aOn) throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public synchronized void setSendBufferSize(int aSize) throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setSoLinger(boolean aOn, int aLinger) throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public synchronized void setSoTimeout(int aTimeout) throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setTcpNoDelay(boolean aOn) throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setTrafficClass(int aTc) throws SocketException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void shutdownInput() throws IOException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void shutdownOutput() throws IOException
		{
			throw new UnsupportedOperationException();
		}
	}
	
	private class VirtualServerSocket extends ServerSocket
	{
		private final int itsPort;

		private final BlockingQueue<Socket> itsAcceptQueue = new LinkedBlockingQueue<Socket>();

		public VirtualServerSocket(int aPort) throws IOException
		{
			itsPort = aPort;
		}

		@Override
		public Socket accept() throws IOException
		{
			try
			{
				return itsAcceptQueue.take();
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}

		/**
		 * This method is called when a client connects to this server socket.
		 */
		private void connected(Socket aSocket)
		{
			itsAcceptQueue.offer(aSocket);
		}

		@Override
		public boolean isBound()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void close() throws IOException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isClosed()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void bind(SocketAddress aEndpoint, int aBacklog) throws IOException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void bind(SocketAddress aEndpoint) throws IOException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public ServerSocketChannel getChannel()
		{
			return null;
		}

		@Override
		public InetAddress getInetAddress()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public int getLocalPort()
		{
			return itsPort;
		}

		@Override
		public SocketAddress getLocalSocketAddress()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public synchronized int getReceiveBufferSize() throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean getReuseAddress() throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public synchronized int getSoTimeout() throws IOException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void setPerformancePreferences(int aConnectionTime, int aLatency, int aBandwidth)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public synchronized void setReceiveBufferSize(int aSize) throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void setReuseAddress(boolean aOn) throws SocketException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public synchronized void setSoTimeout(int aTimeout) throws SocketException
		{
			throw new UnsupportedOperationException();
		}
	}
}
