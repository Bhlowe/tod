/*
 * Created on Jul 25, 2006
 */
package zz.utils.bit;

import junit.framework.TestCase;

import org.junit.Test;
import static org.junit.Assert.*;


public class TestBitStruct 
{
	@Test public void readWriteTest()
	{
		ByteBitStruct theStruct = new ByteBitStruct();
		
		theStruct.writeLong(0xcc, 8);
		System.out.println(theStruct);
		
		theStruct.writeInt(0x3cc, 10);
		System.out.println(theStruct);
		
		theStruct.writeLong(0x2a, 7);
		System.out.println(theStruct);
		
		theStruct.writeLong(0x7f99885544332211L, 63);
		System.out.println(theStruct);
		
		theStruct.reset();
		
		long theValue;
		
		theValue = theStruct.readInt(8);
		assertTrue(theValue == 0xcc);
		
		theValue = theStruct.readInt(10);
		assertTrue(theValue == 0x3cc);
		
		theValue = theStruct.readInt(7);
		assertTrue(theValue == 0x2a);
		
		theValue = theStruct.readLong(63);
		assertTrue(theValue == 0x7f99885544332211L);
	}
}
