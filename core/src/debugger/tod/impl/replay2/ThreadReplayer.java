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
package tod.impl.replay2;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

import tod.core.config.TODConfig;
import tod.core.database.browser.LocationUtils;
import tod.core.database.structure.IBehaviorInfo;
import tod.core.database.structure.IMutableStructureDatabase;
import tod.core.database.structure.IStructureDatabase;
import tod.core.database.structure.ObjectId;
import tod.impl.server.BufferStream;
import tod.impl.server.ModeChangesList;
import tod.impl.server.ModeChangesList.ModeChange;
import tod2.agent.Message;
import tod2.agent.MonitoringMode;
import tod2.agent.ValueType;
import zz.utils.Utils;
import zz.utils.primitive.ByteArray;
import zz.utils.primitive.IntStack;

public abstract class ThreadReplayer
{
	private static final int FRAMETYPE_UNMONITORED = -1;
	private static final int FRAMETYPE_ENVELOPPE = -2;
	private static final int FRAMETYPE_CLASSLOADERWRAPPER = -3;
	private static final int FRAMETYPE_INITIAL = -4;
	
	public static final boolean ECHO = true;
	public static boolean ECHO_FORREAL = true;

	private final int itsThreadId;
	private final TODConfig itsConfig;
	private final IMutableStructureDatabase itsDatabase;
	
	private int itsMessageCount = 0;
	private IntStack itsStack = new IntStack();

	private BufferStream itsStream;
	private final TmpIdManager itsTmpIdManager;
	private final IntDeltaReceiver itsBehIdReceiver = new IntDeltaReceiver();
	private final LongDeltaReceiver itsObjIdReceiver = new LongDeltaReceiver();
	
	/**
	 * The monitoring modes of each behavior, indexed by behavior id.
	 * The mode is updated whenever we receive a {@link Message#TRACEDMETHODS_VERSION} message.
	 */
	private final ByteArray itsMonitoringModes = new ByteArray();
	private int itsCurrentTracedMethodsVersion = 0;
	
	private final List<Type> itsBehaviorReturnTypes = new ArrayList<Type>();

	private ExceptionInfo itsLastException;
	
	private final EventCollector itsCollector;
	private final ReplayerLoader itsLoader;
	
	public ThreadReplayer(
			ReplayerLoader aLoader,
			int aThreadId,
			TODConfig aConfig, 
			IMutableStructureDatabase aDatabase, 
			EventCollector aCollector,
			TmpIdManager aTmpIdManager,
			BufferStream aBuffer)
	{
		itsLoader = aLoader;
		itsThreadId = aThreadId;
		itsConfig = aConfig;
		itsDatabase = aDatabase;
		itsCollector = aCollector;
		itsTmpIdManager = aTmpIdManager;
		itsStream = aBuffer;
	}
	
	protected abstract ReplayerGenerator createReplayerGenerator(ReplayerLoader aLoader, TODConfig aConfig, IMutableStructureDatabase aDatabase);
	
	protected ReplayerGenerator getGenerator()
	{
		if (itsLoader.getGenerator() == null) itsLoader.setGenerator(createReplayerGenerator(itsLoader, itsConfig, itsDatabase));
		return (ReplayerGenerator) itsLoader.getGenerator();
	}

	protected BufferStream getStream()
	{
		return itsStream;
	}
	
	public IntStack getStack()
	{
		return itsStack;
	}
	
	public IStructureDatabase getDatabase()
	{
		return itsDatabase;
	}
	
	public EventCollector getCollector()
	{
		return itsCollector;
	}
	
	public final void echo(String aText, Object... aArgs)
	{
//		Utils.printlnIndented(itsStack.size()*2, aText, aArgs);
		Utils.println(aText, aArgs);
	}
	
	public abstract void replay();
	
	public abstract LocalsSnapshot createSnapshot(int aProbeId);
	
	public abstract int getSnapshotSeq();
	public abstract LocalsSnapshot getSnapshotForResume();
	public abstract void registerSnapshot(LocalsSnapshot aSnapshot);
	public abstract int getStartProbe();

	public byte getNextMessage()
	{
		processStatelessMessages();
		return nextMessage();
	}
	
	public boolean hasMoreMessages()
	{
		return itsStream.remaining() > 0;
	}
	
	private byte nextMessage()
	{
		byte theMessage = itsStream.get();
		if (ECHO) 
		{
			if (!ECHO_FORREAL && itsMessageCount > 1713000000) ECHO_FORREAL = true;
			if (theMessage != Message.REGISTER_OBJECT) itsMessageCount++;
			if (ECHO_FORREAL) echo("Message (%d): [#%d @%d] %s", itsThreadId, itsMessageCount, itsStream.position(), Message._NAMES[theMessage]);
			if (itsMessageCount == 15360423)
			{
				System.out.println("ThreadReplayer.nextMessage()");
			}
		}
		return theMessage;
	}
	
	public byte peekNextMessage()
	{
		processStatelessMessages();
		return itsStream.peek();
	}
	
	private void processStatelessMessages()
	{
		while(true)
		{
			byte theMessage = itsStream.peek();
			
			switch(theMessage)
			{
			case Message.TRACEDMETHODS_VERSION:
				nextMessage();
				processTracedMethodsVersion(itsStream.getInt());
				break;
				
			case Message.REGISTER_REFOBJECT:
				nextMessage();
				processRegisterRefObject(itsObjIdReceiver.receiveFull(itsStream), itsStream);
				break;
				
			case Message.REGISTER_REFOBJECT_DELTA:
				nextMessage();
				processRegisterRefObject(itsObjIdReceiver.receiveDelta(itsStream), itsStream);
				break;
				
			case Message.REGISTER_OBJECT: 
				nextMessage();
				processRegisterObject(itsStream); 
				break;
				
			case Message.REGISTER_OBJECT_DELTA: 
				nextMessage();
				throw new UnsupportedOperationException();
				
			case Message.REGISTER_THREAD: 
				nextMessage();
				processRegisterThread(itsStream); 
				break;
				
			case Message.REGISTER_CLASS: 
				nextMessage();
				processRegisterClass(itsStream); 
				break;
				
			case Message.REGISTER_CLASSLOADER: 
				nextMessage();
				processRegisterClassLoader(itsStream); 
				break;
				
			case Message.SYNC: 
				nextMessage();
				processSync(itsStream); 
				break;
			
			default:
				return;
			}
		}
	}
	
	private void processTracedMethodsVersion(int aVersion)
	{
		if (ThreadReplayer.ECHO && ThreadReplayer.ECHO_FORREAL)
			System.out.println("Traced methods version: "+aVersion);

		setTracedMethodsVersion(aVersion);
	}
	
	protected void setTracedMethodsVersion(int aVersion)
	{
		assert aVersion >= itsCurrentTracedMethodsVersion;
		for(int i=itsCurrentTracedMethodsVersion;i<aVersion;i++)
		{
			ModeChange theChange = ModeChangesList.get(i);
			itsMonitoringModes.set(theChange.behaviorId, theChange.mode);
			if (ThreadReplayer.ECHO && ThreadReplayer.ECHO_FORREAL) 
				Utils.println("Mode changed (%d): %d -> %d (%s)", itsThreadId, theChange.behaviorId, theChange.mode, LocationUtils.toMonitoringModeString(theChange.mode));
		}
		
		itsCurrentTracedMethodsVersion = aVersion;
	}
	
	public int getTracedMethodsVersion()
	{
		return itsCurrentTracedMethodsVersion;
	}
	
	/**
	 * Returns the current monitoring mode for the given method
	 * @return One of the constants in {@link MonitoringMode}.
	 */
	public int getBehaviorMonitoringMode(int aBehaviorId)
	{
		return itsMonitoringModes.get(aBehaviorId);
	}
	
	public Type getBehaviorReturnType(int aBehaviorId)
	{
		Type theType = Utils.listGet(itsBehaviorReturnTypes, aBehaviorId);
		if (theType == null)
		{
			IBehaviorInfo theBehavior = getDatabase().getBehavior(aBehaviorId, true);
			String theSignature = theBehavior.getSignature();
			theType = Type.getReturnType(theSignature);
//			theType = MethodReplayerGenerator.getActualType(theType);
			Utils.listSet(itsBehaviorReturnTypes, aBehaviorId, theType);
		}
		
		return theType;
	}
	
	private void processRegisterObject(BufferStream aBuffer)
	{
		int theDataSize = aBuffer.getInt();
		long theId = aBuffer.getLong();
		boolean theIndexable = aBuffer.get() != 0;
		
		byte[] theData = new byte[theDataSize];
		aBuffer.get(theData, 0, theDataSize);
		
		//TODO: register object
	}
	
	private void processRegisterRefObject(long aId, BufferStream aBuffer)
	{
		int theClassId = aBuffer.getInt();
		
		// TODO: register object
	}
	
	private void processRegisterThread(BufferStream aBuffer)
	{
		long theId = aBuffer.getLong();
		String theName = aBuffer.getString();
		
		if ("DestroyJavaVM".equals(theName))
		{
			// Temporary hack: the destroyer thread is not recorded until the end, so just skip it
			getStream().skipAll();
			throw new SkipThreadException();
		}
		
		// TODO: register
	}
	
	private void processRegisterClass(BufferStream aBuffer)
	{
		int theClassId = aBuffer.getInt();
		long theLoaderId = aBuffer.getLong();
		String theName = aBuffer.getString();
		
		// TODO: register
	}
	
	private void processRegisterClassLoader(BufferStream aBuffer)
	{
		long theLoaderId = aBuffer.getLong();
		long theLoaderClassId = aBuffer.getLong();
		
		// TODO: register
	}
	
	protected void processSync(BufferStream aBuffer)
	{
		long theTimestamp = aBuffer.getLong();
		itsCollector.sync(theTimestamp);
	}
	
	private static boolean isInScope(ReplayerFrame aFrame)
	{
		return aFrame != null ? aFrame.isInScope() : false;
	}
	
	public InScopeReplayerFrame createInScopeFrame(ReplayerFrame aParent, int aBehaviorId, String aDebugInfo)
	{
		InScopeReplayerFrame theFrame = getGenerator().createInScopeFrame(aBehaviorId);
		theFrame.setup(this, itsStream, aDebugInfo, isInScope(aParent), null);
		itsStack.push(aBehaviorId);
		return theFrame;
	}
	
	public EnveloppeReplayerFrame createEnveloppeFrame(ReplayerFrame aParent, Type aReturnType, String aDebugInfo)
	{
		EnveloppeReplayerFrame theFrame = getGenerator().createEnveloppeFrame();
		theFrame.setup(this, itsStream, aDebugInfo, isInScope(aParent), aReturnType);
		itsStack.push(FRAMETYPE_ENVELOPPE);
		return theFrame;
	}
	
	public UnmonitoredReplayerFrame createUnmonitoredFrame(ReplayerFrame aParent, Type aReturnType, String aDebugInfo)
	{
		UnmonitoredReplayerFrame theFrame = getGenerator().createUnmonitoredFrame();
		theFrame.setup(this, itsStream, aDebugInfo, isInScope(aParent), aReturnType);
		itsStack.push(FRAMETYPE_UNMONITORED);
		return theFrame;
	}
	
	public ClassloaderWrapperReplayerFrame createClassloaderFrame(ReplayerFrame aParent, String aDebugInfo)
	{
		ClassloaderWrapperReplayerFrame theFrame = getGenerator().createClassloaderWrapperFrame();
		theFrame.setup(this, itsStream, aDebugInfo, isInScope(aParent), null);
		itsStack.push(FRAMETYPE_CLASSLOADERWRAPPER);
		return theFrame;
	}
	
	protected void pushInitialFrame()
	{
		itsStack.push(FRAMETYPE_INITIAL);
	}
	
	public void popped()
	{
		itsStack.pop();
	}
	
	public IntDeltaReceiver getBehIdReceiver()
	{
		return itsBehIdReceiver;
	}
	
	public LongDeltaReceiver getObjIdReceiver()
	{
		return itsObjIdReceiver;
	}
	
	public TmpIdManager getTmpIdManager()
	{
		return itsTmpIdManager;
	}
	
	public ObjectId readRef()
	{
		byte theType = itsStream.get();
		switch(theType)
		{
		case ValueType.OBJECT_ID: return new ObjectId(itsObjIdReceiver.receiveFull(itsStream));
		case ValueType.OBJECT_ID_DELTA: return new ObjectId(itsObjIdReceiver.receiveDelta(itsStream));
		case ValueType.NULL: return null;
		default: throw new RuntimeException("Not handled: "+theType); 
		}
	}
	
	public ExceptionInfo readExceptionInfo()
	{
		String theMethodName = itsStream.getString();
		String theMethodSignature = itsStream.getString();
		String theDeclaringClassSignature = itsStream.getString();
		short theBytecodeIndex = itsStream.getShort();
		ObjectId theException = readRef();
		
		String theClassName;
		try
		{
			theClassName = Type.getType(theDeclaringClassSignature).getClassName();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Bad declaring class signature: "+theDeclaringClassSignature, e);
		}
		
		int theBehaviorId = getDatabase().getBehaviorId(theClassName, theMethodName, theMethodSignature);

		itsLastException = new ExceptionInfo(
				theMethodName, 
				theMethodSignature, 
				theDeclaringClassSignature,
				theBehaviorId,
				theBytecodeIndex, 
				theException);
		
		return itsLastException;
	}
	
	public ExceptionInfo getLastException()
	{
		return itsLastException;
	}
	
	public static class ExceptionInfo
	{
		public final String methodName;
		public final String methodSignature;
		public final String declaringClassSignature;
		public final int behaviorId;
		public final short bytecodeIndex;
		public final ObjectId exception;
		
		public ExceptionInfo(
				String aMethodName,
				String aMethodSignature,
				String aDeclaringClassSignature,
				int aBehaviorId,
				short aBytecodeIndex,
				ObjectId aException)
		{
			methodName = aMethodName;
			methodSignature = aMethodSignature;
			declaringClassSignature = aDeclaringClassSignature;
			behaviorId = aBehaviorId;
			bytecodeIndex = aBytecodeIndex;
			exception = aException;
		}
	}

}
