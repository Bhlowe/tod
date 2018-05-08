/*
 * Created on Jul 25, 2006
 */
package zz.utils.bit;

public class BitUtils
{
	public static void writeLong(byte[] aDest, long aValue, int aStart, int aBitCount)
	{
		writeLong(aDest, 0, aValue, aStart, aBitCount);
	}

	public static void writeInt(byte[] aDest, int aOffset, int aValue, int aStart, int aBitCount)
	{
		assert aBitCount <= 32;
		writeLong(aDest, aOffset, aValue, aStart, aBitCount);
	}

	public static void writeBoolean(byte[] aDest, int aOffset, boolean aValue, int aStart)
	{
		writeByte(aDest, aOffset, (byte) (aValue ? 1 : 0), aStart, 1);
	}

	public static void writeLong(byte[] aDest, int aOffset, long aValue, int aStart, int aBitCount)
	{
		assert aBitCount > 0;
		assert aBitCount <= 64;
	
		while (aBitCount > 0)
		{
			int theBits = Math.min(aBitCount, 8);
			writeByte(aDest, aOffset, (byte) (aValue & 0xff), aStart, theBits);
			
			aValue >>>= 8;
			aBitCount -= theBits;
			aStart += theBits;
		}
	}

	public static void writeInt(byte[] aDest, int aValue, int aStart, int aBitCount)
	{
		writeInt(aDest, 0, aValue, aStart, aBitCount);
	}

	public static void writeByte(byte[] aDest, int aOffset, byte aValue, int aStart, int aBitCount)
	{
		assert aBitCount > 0;
		assert aBitCount <= 8;
		
		int theBitShift = aStart % 8;
	
		int theIntMask = pow2i(aBitCount)-1;
		int theIntValue = aValue & theIntMask;
		
		theIntValue <<=  theBitShift;
		theIntMask <<= theBitShift;
		
		int thePos = aStart;
		int theCount = aBitCount;
		
		while (theCount > 0) 
		{
			byte theByteMask = (byte) (theIntMask & 0xff);
			byte theByteValue = (byte) (theIntValue &0xff);
			
			aDest[aOffset + thePos/8] &= ~theByteMask;
			aDest[aOffset + thePos/8] |= theByteValue;
			
			int theWrittenBits = 8 - (thePos % 8);
			theCount -= theWrittenBits;
			thePos += theWrittenBits;
			theIntMask >>>= 8;
			theIntValue >>>= 8;
		}
	}

	public static void writeInt(int[] aDest, int aOffset, int aValue, int aStart, int aBitCount)
	{
		assert aBitCount > 0;
		assert aBitCount <= 32;
		
		int theBitShift = aStart % 32;
		int thePos = aOffset + aStart / 32;
		
		int theMask = pow2i(aBitCount)-1;
		aValue &= theMask;
		
		// First group
		int theMask1 = theMask << theBitShift;
		int theValue1 = aValue << theBitShift;
		aDest[thePos] = (aDest[thePos] & ~theMask1) | theValue1;
		
		// Second group
		if (theBitShift + aBitCount > 32)
		{
			thePos++;
			int theRemainingBits = 32-theBitShift;
			int theMask2 = theMask >>> theRemainingBits;
			int theValue2 = aValue >>> theRemainingBits;
			aDest[thePos] = (aDest[thePos] & ~theMask2) | theValue2;
		}
	}
	
	public static void writeLong(int[] aDest, int aOffset, long aValue, int aStart, int aBitCount)
	{
		assert aBitCount <= 64;

		writeInt(aDest, aOffset, (int) (aValue & -1), aStart, Math.min(aBitCount, 32));
		if (aBitCount > 32) writeInt(aDest, aOffset, (int) (aValue >>> 32), aStart+32, aBitCount-32);
	}
	
	public static long readLong(byte[] aBytes, int aStart, int aBitCount)
	{
		return readLong(aBytes, 0, aStart, aBitCount);
	}

	public static long readLong(byte[] aBytes, int aOffset, int aStart, int aBitCount)
	{
		assert aBitCount > 0;
		long theResult = 0;
		
		int theReadBits = 0;
		while (aBitCount > 0)
		{
			int theBits = Math.min(aBitCount, 8);
			long theLong = readByte(aBytes, aOffset, aStart, theBits) & 0xffL;
			theLong <<= theReadBits;
			theResult |= theLong;
			theReadBits += 8;
			aStart += 8;
			aBitCount -= 8;
		}
		
		return theResult;
	}

	public static int readInt(byte[] aBytes, int aStart, int aBitCount)
	{
		return readInt(aBytes, 0, aStart, aBitCount);
	}

	public static int readInt(byte[] aBytes, int aOffset, int aStart, int aBitCount)
	{
		assert aBitCount <= 32;
		return (int) readLong(aBytes, aOffset, aStart, aBitCount);
	}

	public static byte readByte(byte[] aBytes, int aOffset, int aStart, int aBitCount)
	{
		assert aBitCount > 0;
		assert aBitCount <= 8;
		int theBitShift = aStart % 8;
	
		int theIntMask = pow2i(aBitCount)-1;
		int theIntValue = 0;
		
		theIntMask <<= theBitShift;
		
		int thePos = aStart;
		int theCount = aBitCount;
		
		int theWrittenBits = 0;
		while (theCount > 0) 
		{
			long theByteValue = aBytes[aOffset + thePos/8] & 0xffL;
			
			theByteValue &= theIntMask;
			theIntValue |= shiftLeft(theByteValue, theWrittenBits - theBitShift);
			
			int theReadBits = 8 - (thePos % 8);
			theCount -= theReadBits;
			thePos += theReadBits;
			theIntMask >>>= 8;
		
			theWrittenBits += 8;
		}
		
		return (byte) (theIntValue & 0xff);
	}
	
	public static int readInt(int[] aSrc, int aOffset, int aStart, int aBitCount)
	{
		assert aBitCount > 0;
		assert aBitCount <= 32;
		
		int theBitShift = aStart % 32;
		int thePos = aOffset + aStart / 32;

		int theValue;
		
		// First group
		theValue = aSrc[thePos] >>> theBitShift;
		
		// Second group
		if (theBitShift + aBitCount > 32)
		{
			thePos++;
			int theReadBits = 32-theBitShift;
			int theRemainingBits = aBitCount - theReadBits;
			int theMask2 = pow2i(theRemainingBits)-1;
			int theValue2 = aSrc[thePos] & theMask2;
			theValue2 <<= theReadBits;
			theValue |= theValue2;
		}
		else
		{
			int theMask = pow2i(aBitCount)-1;
			theValue &= theMask;
		}
		
		return theValue;
	}

	public static long readLong(int[] aSrc, int aOffset, int aStart, int aBitCount)
	{
		assert aBitCount > 0;
		assert aBitCount <= 64;
		long theValue = readInt(aSrc, aOffset, aStart, Math.min(aBitCount, 32)) & 0xffffffffL;
		if (aBitCount > 32) 
		{
			long theHiValue = readInt(aSrc, aOffset, aStart+32, aBitCount-32);
			theValue |= theHiValue << 32; 
		}
		
		return theValue;
	}

	

	/**
	 * Shifts bits left if n is positive, right otherwise.
	 */
	public static long shiftLeft(long aValue, int aBits)
	{
		if (aBits == 0) return aValue;
		else if (aBits > 0) return aValue << aBits;
		else return aValue >>> -aBits;
	}

	public static final long pow2(int aN)
	{
		return 1L << aN;
	}

	public static final int pow2i(int aN)
	{
		return aN < 32 ? 1 << aN : 0;
	}
	
	/**
	 * Computes x^n
	 */
	public static final int powi(int x, int n)
	{
		assert n>=0;
		int theResult = 1;
		for(int i=0;i<n;i++) theResult *= x;
		return theResult;
	}
	
	/**
	 * Returns the base-2 logarithm of the argument
	 */
	public static final double log2(int aN)
	{
		return Math.log(aN)/Math.log(2);
	}
	
	/**
	 * Returns the smallest integer that is greater than the 
	 * log of the specified number.
	 */
	public static final int log2ceil(int aN)
	{
		return (int) Math.ceil(log2(aN));
	}
	
	/**
	 * Returns the base-2 logarithm of the argument if the argument
	 * is a power of 2, otherwise returns -1;
	 */
	public static final int log2ex(int aN)
	{
		if (aN <= 0) return -1;
		
		int k = 1;
		int l = 0;
		
		while (k != 0)
		{
			if (aN == k) return l;
			l++;
			k <<= 1;
		}
		
		return -1;
	}
	
	public static boolean isOverflow(int aValue, int aBits)
	{
		if (aBits >= 32) return false;
		else 
		{
			int theMask = ~(pow2i(aBits)-1);
			return (aValue & theMask) != 0; 
		}
	}
	
	public static boolean isOverflow(long aValue, int aBits)
	{
		if (aBits >= 64) return false;
		else 
		{
			long theMask = ~(pow2(aBits)-1);
			return (aValue & theMask) != 0; 
		}
	}

}
