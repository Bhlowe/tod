/*
TOD - Trace Oriented Debugger.
Copyright (c) 2006-2008, Guillaume Pothier
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this 
      list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, 
      this list of conditions and the following disclaimer in the documentation 
      and/or other materials provided with the distribution.
    * Neither the name of the University of Chile nor the names of its contributors 
      may be used to endorse or promote products derived from this software without 
      specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE.

Parts of this work rely on the MD5 algorithm "derived from the RSA Data Security, 
Inc. MD5 Message-Digest Algorithm".
*/
package zz.utils.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.server.RMISocketFactory;

public class MultiplexRMISocketFactory extends RMISocketFactory
implements Externalizable
{
	private static final int HANDSHAKE = 0xECEAC;
	private final MultiplexingManager itsManager;
	
	private static MultiplexRMISocketFactory INSTANCE;
	
	public static MultiplexRMISocketFactory get()
	{
		if (INSTANCE == null) throw new RuntimeException("Factory not created");
		return INSTANCE;
	}

	/**
	 * Default constructor for serialization
	 */
	public MultiplexRMISocketFactory()
	{
		itsManager = null;
	}
	
	private MultiplexRMISocketFactory(MultiplexingManager aManager)
	{
		itsManager = aManager;
	}

	public Object readResolve() throws ObjectStreamException
	{
		assert INSTANCE != null;
		return INSTANCE;
	}

	public void readExternal(ObjectInput aIn) throws IOException, ClassNotFoundException
	{
	}

	public void writeExternal(ObjectOutput aOut) 
	{
	}

	/**
	 * Creates the server end of the multiplexing connection.
	 * @param aLocalHostname The name of this host (needs not be a valid network name). 
	 */
	public static void createServer(final String aLocalHostname, int aPort) throws IOException
	{
		if (INSTANCE != null) throw new RuntimeException("Factory already created");
		System.setProperty("java.rmi.server.hostname", aLocalHostname);
		final MultiplexingManager theManager = new MultiplexingManager(aLocalHostname);
		Server theServer = new Server(aPort, true)
		{
			@Override
			protected void accepted(Socket aSocket)
			{
				try
				{
					DataInputStream theIn = new DataInputStream(aSocket.getInputStream());
					DataOutputStream theOut = new DataOutputStream(aSocket.getOutputStream());
					
					theOut.writeInt(HANDSHAKE);
					theOut.writeUTF(aLocalHostname);
					
					int theHandshake = theIn.readInt();
					if (theHandshake != HANDSHAKE)
					{
						System.err.println("Bad handshake");
						aSocket.close();
						return;
					}
					
					String theRemoteHostname = theIn.readUTF();
					
					System.out.println(String.format("Accepted connection - local: %s, remote: %s", aLocalHostname, theRemoteHostname));
					
					theManager.addSocket(theRemoteHostname, aSocket);
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		};
		
		INSTANCE = new MultiplexRMISocketFactory(theManager);
	}
	
	/**
	 * Creates the client end of the multiplexling connection, that tries to connect
	 * to the server end at the specified address.
	 * @param aHostName The resolvable network name of the host to connect to. 
	 * @param aLocalHostname The name of this host (needs not be a valid network name). 
	 */
	public static void createClient(String aLocalHostname, String aHostName, int aPort)
	{
		if (INSTANCE != null) throw new RuntimeException("Factory already created");
		System.setProperty("java.rmi.server.hostname", aLocalHostname);
		try
		{
			Socket theSocket = new Socket(aHostName, aPort);
			DataInputStream theIn = new DataInputStream(theSocket.getInputStream());
			DataOutputStream theOut = new DataOutputStream(theSocket.getOutputStream());
			
			theOut.writeInt(HANDSHAKE);
			theOut.writeUTF(aLocalHostname);
			
			int theHandshake = theIn.readInt();
			if (theHandshake != HANDSHAKE)
			{
				theSocket.close();
				throw new IOException("Bad handshake");
			}
			
			String theRemoteHostname = theIn.readUTF();
			
			System.out.println(String.format("Connected to %s as %s; remote name is: %s.", aHostName, aLocalHostname, theRemoteHostname));
			
			MultiplexingManager theManager = new MultiplexingManager(aLocalHostname);
			theManager.addSocket(theRemoteHostname, theSocket);

			INSTANCE = new MultiplexRMISocketFactory(theManager);
		}
		catch (UnknownHostException e)
		{
			throw new RuntimeException(e);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ServerSocket createServerSocket(int aPort) throws IOException
	{
		return itsManager.createServerSocket(aPort);
	}

	@Override
	public Socket createSocket(String aHost, int aPort) throws IOException
	{
		return itsManager.createSocket(aHost, aPort);
	}

	@Override
	public boolean equals(Object aObj)
	{
		if (aObj instanceof MultiplexRMISocketFactory)
		{
			MultiplexRMISocketFactory theOther = (MultiplexRMISocketFactory) aObj;
			return theOther.itsManager.getLocalHostName().equals(itsManager.getLocalHostName());
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return itsManager.getLocalHostName().hashCode()*122;
	}
}
