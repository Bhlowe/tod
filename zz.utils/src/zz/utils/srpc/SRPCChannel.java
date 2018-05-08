package zz.utils.srpc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import zz.utils.ArrayStack;
import zz.utils.Stack;
import zz.utils.Utils;

/**
 * Represents an bidirectional communication channel between two processed.
 * There must be one {@link SRPCChannel} on both ends of the connection.
 * @author gpothier
 */
public class SRPCChannel extends Thread
{
	private static final byte CMD_CALL = 40;
	private static final byte CMD_ACKCALL = 41;
	private static final byte CMD_RETURN = 42;
	private static final byte CMD_GC = 43;
	
	private Socket itsSocket;
	private SRPCObjectInputStream itsIn;
	private SRPCObjectOutputStream itsOut;
	
	/**
	 * We assign id to each endpoint so as to improve the safety of the calls
	 * (to ensure that what the client thinks is at a given port is actually the 
	 * correct object).
	 */
	private long itsNextEndpointId = 10000;
	
	private int itsNextFreePort = 10000;
	private Stack<Integer> itsFreedPorts = new ArrayStack<Integer>();

	private Map<Integer, EndpointInfo> itsEndpointsMap = new HashMap<Integer, EndpointInfo>();
	private Map<IRemote, EndpointInfo> itsRemotesMap = new IdentityHashMap<IRemote, EndpointInfo>();
	
	/**
	 * Map of ports to proxies into remote objects
	 */
	private Map<Integer, ProxyRef> itsProxiesMap = new HashMap<Integer, ProxyRef>();
	
	private ReferenceQueue<Proxy> itsProxyRefQueue = new ReferenceQueue<Proxy>();
	
	/**
	 * Maps remote interfaces with the corresponding, sorted list of methods.
	 */
	private Map<Class, Method[]> itsMethodsMap = new HashMap<Class, Method[]>(); 
	
	private Map<Long, Waiter> itsWaitersMap = new HashMap<Long, Waiter>();
	
	/**
	 * Executor for incoming requests.
	 */
	private final Executor itsExecutor = Executors.newCachedThreadPool();
	
	private long itsNextCommandId = 1;
	
	public SRPCChannel(Socket aSocket) throws IOException
	{
		super("SRPCChannel thread");
		itsSocket = aSocket;
		itsOut = new SRPCObjectOutputStream(new BufferedOutputStream(itsSocket.getOutputStream()));
		itsOut.flush(); // For stream header
		itsIn = new SRPCObjectInputStream(new BufferedInputStream(itsSocket.getInputStream()));
		start();
	}
	
	public void addEndpoint(int aPort, IRemote aEndpoint)
	{
		EndpointInfo theInfo = new EndpointInfo(aEndpoint, aPort, aPort);
		itsEndpointsMap.put(aPort, theInfo);
		itsRemotesMap.put(aEndpoint, theInfo);
	}
	
	/**
	 * Returns the local port used by the given local remote objects,
	 * and assigns a new port if necessary. 
	 */
	private EndpointInfo getEndpointInfo(IRemote aRemote)
	{
		EndpointInfo theInfo = itsRemotesMap.get(aRemote);
		if (theInfo == null)
		{
			// First time we see this endpoint
			theInfo = new EndpointInfo(aRemote, nextFreePort(), nextEndpointId());
			itsRemotesMap.put(aRemote, theInfo);
			itsEndpointsMap.put(theInfo.port, theInfo);
		}
		return theInfo;
	}
	
	private synchronized long nextEndpointId()
	{
		return itsNextEndpointId++;
	}
	
	private synchronized int nextFreePort()
	{
		if (itsFreedPorts.isEmpty()) return itsNextFreePort++;
		else return itsFreedPorts.pop();
	}

	private synchronized void portFreed(int aPort)
	{
		itsFreedPorts.push(aPort);
	}
	
	private Method[] getMethods(Class aInterface)
	{
		Method[] theMethods = itsMethodsMap.get(aInterface);
		if (theMethods == null)
		{
			assert aInterface.isInterface();
			assert IRemote.class.isAssignableFrom(aInterface);

			theMethods = aInterface.getMethods();
			Arrays.sort(theMethods, SRPCUtils.MethodComparator.getInstance());
		}
		
		return theMethods;
	}
	
	public RIRegistry getRemoteRegistry()
	{
		return (RIRegistry) getProxy(new RemoteObjectDesc(
				SRPCServer.REGISTRY_PORT, 
				SRPCServer.REGISTRY_PORT, 
				RIRegistry.class));
	}

	/**
	 * Checks if some proxies have been garbage collected, and 
	 * inform the other side of the channel as necessary.
	 */
	private void checkProxyRefs()
	{
		ProxyRef theRef;
		while ((theRef = (ProxyRef) itsProxyRefQueue.poll()) != null) sendGC(theRef.getPort());
	}
	
	private Proxy getProxy(RemoteObjectDesc aDesc)
	{
		ProxyRef theRef = itsProxiesMap.get(aDesc.port);
		Proxy theProxy = theRef != null ? theRef.get() : null;
		if (theProxy == null)
		{
			theProxy = (Proxy) Proxy.newProxyInstance(
					aDesc.cls.getClassLoader(), 
					new Class[] { aDesc.cls }, 
					new MyInvocationHandler(aDesc, getMethods(aDesc.cls)));
			
			itsProxiesMap.put(aDesc.port, new ProxyRef(theProxy, itsProxyRefQueue, aDesc.port));
		}
		return theProxy;
	}
	
	private synchronized long nextCommandId()
	{
		return itsNextCommandId++;
	}
	
	private Waiter createWaiter()
	{
		Waiter theWaiter = new Waiter(nextCommandId());
		itsWaitersMap.put(theWaiter.getCommandId(), theWaiter);
		return theWaiter;
	}
	
	private void wakeup(long aCommandId, Object aData)
	{
		Waiter theWaiter = itsWaitersMap.remove(aCommandId);
		theWaiter.setData(aData);
	}
	
	private void acknowledge(long aCommandId)
	{
		Waiter theWaiter = itsWaitersMap.get(aCommandId);
		theWaiter.acknowledged();
	}
	
	@Override
	public void run()
	{
		try
		{
			while(true)
			{
				byte theCmd;
				
				try
				{
					theCmd = itsIn.readByte();
				}
				catch (EOFException e)
				{
					break;
				}
				
				switch(theCmd)
				{
				case CMD_CALL: processCall(); break;
				case CMD_ACKCALL: processAckCall(); break;
				case CMD_RETURN: processReturn(); break;
				case CMD_GC: processGC(); break;
				default: throw new RuntimeException("Not handled: "+theCmd);
				}
				checkProxyRefs();
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private synchronized void sendCall(long aCommandId, int aPort, long aEndpointId, int aMethod, Object[] aArgs) 
	{
		try
		{
			itsOut.writeByte(CMD_CALL);
			itsOut.writeLong(aCommandId);
			itsOut.writeInt(aPort);
			itsOut.writeLong(aEndpointId);
			itsOut.writeInt(aMethod);
			itsOut.writeObject(aArgs);
			itsOut.reset();
			itsOut.flush();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void processCall()
	{
		try
		{
			final long theCommandId = itsIn.readLong();
			final int thePort = itsIn.readInt();
			final long theEndpointId = itsIn.readLong();
			final int theMethod = itsIn.readInt();
			final Object[] theArgs = (Object[]) itsIn.readObject();
			itsExecutor.execute(new Runnable()
			{
				public void run()
				{
					processCall(theCommandId, thePort, theEndpointId, theMethod, theArgs);
				}
			});
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void processCall(long aCommandId, int aPort, long aEndpointId, int aMethod, Object[] aArgs)
	{
		EndpointInfo theEndpointInfo = itsEndpointsMap.get(aPort);

		if (theEndpointInfo == null) 
			Utils.rtex("No such endpoint: %d", aEndpointId);
		
		if (theEndpointInfo.id != aEndpointId) 
			Utils.rtex("Bad endpoint (expected %d, got %d)", aEndpointId, theEndpointInfo.id);
		
		sendAckCall(aCommandId);
		
		IRemote theRemote = theEndpointInfo.remote;
		Method[] theMethods = getMethods(SRPCUtils.getRemoteInterface(theRemote));
		Object theResult;
		boolean theThrown;
		try
		{
			theResult = theMethods[aMethod].invoke(theRemote, aArgs);
			theThrown = false;
		}
		catch (InvocationTargetException e)
		{
			theResult = e.getCause();
			theThrown = true;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		sendReturn(aCommandId, theResult, theThrown);
	}
	
	private synchronized void sendAckCall(long aCommandId) 
	{
		try
		{
			itsOut.writeByte(CMD_ACKCALL);
			itsOut.writeLong(aCommandId);
			itsOut.reset();
			itsOut.flush();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void processAckCall()
	{
		try
		{
			final long theCommandId = itsIn.readLong();
			processAckCall(theCommandId);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void processAckCall(long aCommandId)
	{
		acknowledge(aCommandId);
	}
	
	private synchronized void sendReturn(long aCommandId, Object aValue, boolean aThrown)
	{
		try
		{
			itsOut.writeByte(CMD_RETURN);
			itsOut.writeLong(aCommandId);
			itsOut.writeObject(aValue);
			itsOut.writeBoolean(aThrown);
			itsOut.reset();
			itsOut.flush();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void processReturn()
	{
		try
		{
			long theCommandId = itsIn.readLong();
			Object theValue = itsIn.readObject();
			boolean theThrown = itsIn.readBoolean();
			processReturn(theCommandId, theValue, theThrown);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void processReturn(long aCommandId, Object aValue, boolean aThrown)
	{
		wakeup(aCommandId, new ReturnData(aValue, aThrown));
	}
	
	private synchronized void sendGC(int aPort)
	{
		try
		{
			itsOut.writeByte(CMD_GC);
			itsOut.writeInt(aPort);
			itsOut.flush();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void processGC()
	{
		try
		{
			int thePort = itsIn.readInt();
			processGC(thePort);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void processGC(int aPort)
	{
		EndpointInfo theEndpointInfo = itsEndpointsMap.remove(aPort);
		IRemote theRemote = theEndpointInfo.remote;
		itsRemotesMap.remove(theRemote);
		portFreed(aPort);
	}
	
	private Object call(int aPort, long aEndpointId, int aMethod, Object[] aArgs)
	{
		checkProxyRefs();
		Waiter theWaiter = createWaiter();
		sendCall(theWaiter.getCommandId(), aPort, aEndpointId, aMethod, aArgs);
		ReturnData theData = (ReturnData) theWaiter.waitReady();
		if (theData.thrown) throw new RuntimeException("Exception occurred in remote host.", (Throwable) theData.result);
		else return theData.result;
	}
	
	/**
	 * Waits for the response to a command
	 * @author gpothier
	 */
	private static class Waiter
	{
		private final long itsCommandId;
		private boolean itsAcknowledged = false;
		private boolean itsReady = false;
		private Object itsData = null;
		
		public Waiter(long aCommandId)
		{
			itsCommandId = aCommandId;
		}

		public long getCommandId()
		{
			return itsCommandId;
		}
		
		public synchronized void setData(Object aData)
		{
			itsReady = true;
			itsData = aData;
			notifyAll();
		}
		
		public synchronized void acknowledged()
		{
			itsAcknowledged = true;
		}
		
		/**
		 * Waits until the result of a call is available.
		 * There is a timeout of 10s if the call is not acknowledged.
		 * @return
		 */
		public synchronized Object waitReady()
		{
			try
			{
				long t0 = System.currentTimeMillis();
				while (! itsReady) 
				{
					wait(10);
					if (! itsAcknowledged)
					{
						long t1 = System.currentTimeMillis();
						if (t1-t0 > 10000) throw new SRPCRemoteException("Call not acknowledged");
					}
				}
				return itsData;
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	private static class ReturnData
	{
		public final Object result;
		public final boolean thrown;
		
		public ReturnData(Object aResult, boolean aThrown)
		{
			result = aResult;
			thrown = aThrown;
		}
	}
	
	private class SRPCObjectOutputStream extends ObjectOutputStream
	{
		public SRPCObjectOutputStream(OutputStream aOut) throws IOException
		{
			super(aOut);
			enableReplaceObject(true);
		}
		
		@Override
		protected Object replaceObject(Object aObj) 
		{
			if (aObj instanceof IRemote)
			{
				IRemote theRemote = (IRemote) aObj;
				EndpointInfo theEndpointInfo = getEndpointInfo(theRemote);
				return new RemoteObjectDesc(
						theEndpointInfo.port, 
						theEndpointInfo.id, 
						SRPCUtils.getRemoteInterface(aObj));
			}
			else return aObj;
		}
		
	}
	
	private class SRPCObjectInputStream extends ObjectInputStream
	{
		public SRPCObjectInputStream(InputStream aIn) throws IOException
		{
			super(aIn);
			enableResolveObject(true);
		}
	
		@Override
		protected Object resolveObject(Object aObj) throws IOException
		{
			if (aObj instanceof RemoteObjectDesc)
			{
				RemoteObjectDesc theDesc = (RemoteObjectDesc) aObj;
				return getProxy(theDesc);
			}
			else return aObj;
		}
	}
	
	/**
	 * Describes a remote object for serialization
	 * @author gpothier
	 */
	public static class RemoteObjectDesc implements Serializable
	{
		public final int port;
		public final long endpointId;
		public final Class cls;

		public RemoteObjectDesc(int aPort, long aEndpointId, Class aClass)
		{
			port = aPort;
			endpointId = aEndpointId;
			cls = aClass;
		}
	}
	
	private static class EndpointInfo
	{
		public final IRemote remote;
		public final int port;
		public final long id;
		
		public EndpointInfo(IRemote aRemote, int aPort, long aId)
		{
			remote = aRemote;
			port = aPort;
			id = aId;
		}
	}
	
	private class MyInvocationHandler implements InvocationHandler
	{
		private final RemoteObjectDesc itsDesc;
		private final Method[] itsMethods;

		public MyInvocationHandler(RemoteObjectDesc aDesc, Method[] aMethods)
		{
			itsDesc = aDesc;
			itsMethods = aMethods;
		}

		private int getMethodId(Method aMethod)
		{
			for (int i = 0; i < itsMethods.length; i++)
			{
				Method theMethod = itsMethods[i];
				if (theMethod.equals(aMethod)) return i;
			}
			throw new RuntimeException("Unknown: "+aMethod);
		}
		
		public Object invoke(Object aProxy, Method aMethod, Object[] aArgs) throws Throwable
		{
			if (aMethod.getDeclaringClass() == Object.class)
			{
				return aMethod.invoke(this, aArgs);
			}
			else
			{
				int theMethodId = getMethodId(aMethod);
				return call(itsDesc.port, itsDesc.endpointId, theMethodId, aArgs);
			}
		}
	}
	
	private static class ProxyRef extends WeakReference<Proxy>
	{
		private final int itsPort;
		
		public ProxyRef(Proxy aReferent, ReferenceQueue<? super Proxy> aQueue, int aPort)
		{
			super(aReferent, aQueue);
			itsPort = aPort;
		}
		
		public int getPort()
		{
			return itsPort;
		}
	}
}