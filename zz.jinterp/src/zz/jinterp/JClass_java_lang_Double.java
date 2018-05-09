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
package zz.jinterp;

import org.objectweb.asm.tree.ClassNode;

import zz.jinterp.JNormalBehavior.JFrame;
import zz.jinterp.JPrimitive.JDouble;
import zz.jinterp.JPrimitive.JLong;


public class JClass_java_lang_Double extends JNormalClass
{
	public static final String NAME = "java/lang/Double";
	
	public JClass_java_lang_Double(JInterpreter aInterpreter, ClassNode aNode)
	{
		super(aInterpreter, aNode);
		JReflectiveClass.initBehaviors(this, getClass());
	}
	
    public static long __doubleToRawLongBits(double value) { return 0; }
	public JObject _doubleToRawLongBits(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		JDouble value = (JDouble) aArgs[0];
		return new JLong(Double.doubleToRawLongBits(value.v));
	}

	public static long __doubleToLongBits(double value) { return 0; }
	public JObject _doubleToLongBits(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		JDouble value = (JDouble) aArgs[0];
		return new JLong(Double.doubleToLongBits(value.v));
	}

	public static double __longBitsToDouble(long bits) { return 0; }
	public JObject _longBitsToDouble(JFrame aParentFrame, JInstance aTarget, JObject[] aArgs)
	{
		JLong bits = (JLong) aArgs[0];
		return new JDouble(Double.longBitsToDouble(bits.v));
	}

}
