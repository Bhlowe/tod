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

import org.objectweb.asm.Type;

import tod.core.database.structure.ObjectId;
import tod2.agent.Message;

public class UnmonitoredReplayerFrame extends ReplayerFrame
{
	private ObjectId itsRefResult;
	private int itsIntResult;
	private long itsLongResult;
	private float itsFloatResult;
	private double itsDoubleResult;
	
	protected void replay()
	{
		while(true)
		{
			byte m = getNextMessage();
			
			boolean theContinue = replay(m);
			if (! theContinue) break;
		}
	}
	
	/**
	 * Processes an individual message
	 * @return Wheter to continue or not.
	 */
	protected boolean replay(byte aMessage)
	{
		switch(aMessage)
		{
		case Message.EXCEPTION:
		{
			readException();
			break;
		}
		
		case Message.INSCOPE_BEHAVIOR_ENTER: 
			evInScopeBehaviorEnter(getReplayer().getBehIdReceiver().receiveFull(getStream())); 
			break;
			
		case Message.INSCOPE_BEHAVIOR_ENTER_DELTA: 
			evInScopeBehaviorEnter(getReplayer().getBehIdReceiver().receiveDelta(getStream())); 
			break;
			
		case Message.OUTOFSCOPE_BEHAVIOR_ENTER: evOutOfScopeBehaviorEnter(); break;
		case Message.INSCOPE_CLINIT_ENTER: evInScopeClinitEnter(getStream().getInt()); break;
		case Message.OUTOFSCOPE_CLINIT_ENTER: evOutOfScopeClinitEnter(); break;
		case Message.CLASSLOADER_ENTER: evClassloaderEnter(); break;

		case Message.UNMONITORED_BEHAVIOR_CALL_RESULT: readResult(); return false;
		case Message.UNMONITORED_BEHAVIOR_CALL_EXCEPTION: throw new BehaviorExitException();
		case Message.HANDLER_REACHED: throw new HandlerReachedException(readInt());
		case Message.INSCOPE_BEHAVIOR_EXIT_EXCEPTION: throw new BehaviorExitException();
		
		default: throw new RuntimeException("Command not handled: "+Message._NAMES[aMessage]);
		}
		
		return true;
	}
	
	private void evInScopeBehaviorEnter(int aBehaviorId)
	{
		InScopeReplayerFrame theChild = getReplayer().createInScopeFrame(this, aBehaviorId);
		theChild.invoke_OOS();
	}
	
	private void evOutOfScopeBehaviorEnter()
	{
		EnveloppeReplayerFrame theChild = getReplayer().createEnveloppeFrame(this);
		theChild.invoke_OOS();
	}
	
	private void evInScopeClinitEnter(int aBehaviorId)
	{
		InScopeReplayerFrame theChild = getReplayer().createInScopeFrame(this, aBehaviorId);
		theChild.invoke_OOS();
	}
	
	private void evOutOfScopeClinitEnter()
	{
		EnveloppeReplayerFrame theChild = getReplayer().createEnveloppeFrame(this);
		theChild.invoke_OOS();
	}
	
	private void evClassloaderEnter()
	{
		ClassloaderWrapperReplayerFrame theChild = getReplayer().createClassloaderFrame(this);
		theChild.invokeVoid();
	}

	private void readResult()
	{
		switch(getReturnType().getSort())
		{
		case Type.BOOLEAN: 
			itsIntResult = readBoolean() ? 1 : 0;
			break;
			
		case Type.BYTE:
			itsIntResult = readByte();
			break;
			
		case Type.CHAR:
			itsIntResult = readChar();
			break;
			
		case Type.SHORT:
			itsIntResult = readShort();
			break;
			
		case Type.INT:
			itsIntResult = readInt();
			break;
		
		case Type.LONG:
			itsLongResult = readLong();
			break;
		
		case Type.DOUBLE:
			itsDoubleResult = readDouble();
			break;
			
		case Type.FLOAT:
			itsFloatResult = readFloat();
			break;
			
		case Type.VOID:
			break;
		
		default: throw new RuntimeException("Unexpected type: "+getReturnType());
		}
	}
	
	@Override
	public double invokeDouble()
	{
		replay();
		return itsDoubleResult;
	}

	@Override
	public float invokeFloat()
	{
		replay();
		return itsFloatResult;
	}

	@Override
	public int invokeInt()
	{
		replay();
		return itsIntResult;
	}

	@Override
	public long invokeLong()
	{
		replay();
		return itsLongResult;
	}

	@Override
	public ObjectId invokeRef()
	{
		replay();
		return itsRefResult;
	}

	@Override
	public void invokeVoid()
	{
		replay();
	}
	
}