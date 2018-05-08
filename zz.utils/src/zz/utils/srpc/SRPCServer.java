/*
 * Created on Dec 30, 2008
 */
package zz.utils.srpc;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;

import zz.utils.net.Server;

/**
 * The server that accetps incoming SRPC connections.
 * Once a connection is established, it will be used for
 * all communications between the server and the client that 
 * initiated that connection.
 * @author gpothier
 */
public class SRPCServer extends Server
{
	public static final int REGISTRY_PORT = 1;
	
	private SRPCRegistry itsRegistry = new SRPCRegistry();

	public SRPCServer(int aPort, boolean aDaemon)
	{
		super(aPort, true, aDaemon);
	}

	public SRPCServer(int aPort)
	{
		super(aPort, true);
	}

	public SRPCRegistry getRegistry()
	{
		return itsRegistry;
	}
	
	@Override
	protected void accepted(Socket aSocket)
	{
		try
		{
			SRPCChannel theChannel = new SRPCChannel(aSocket);
			theChannel.addEndpoint(REGISTRY_PORT, itsRegistry);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static RIRegistry connectTo(String aHost, int aPort) throws UnknownHostException, IOException
	{
		Socket theSocket = new Socket(aHost, aPort);
		SRPCChannel theChannel = new SRPCChannel(theSocket);
		return theChannel.getRemoteRegistry();
	}
	
	/**
	 * Whether the given object is a remote one (ie. it is concretely a stub)
	 */
	public static boolean isRemote(IRemote aRemote)
	{
		return aRemote instanceof Proxy;
	}
	
	/**
	 * The opposite of {@link #isRemote(IRemote)}
	 */
	public static boolean isLocal(IRemote aRemote)
	{
		return !isRemote(aRemote);
	}

}
