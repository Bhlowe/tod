/*
 * Created on Aug 9, 2006
 */
package zz.utils.bit;

/**
 * Abstract base class for bit structs
 * @author gpothier
 */
public abstract class BitStruct
{
	
	/**
	 * Returns the offset of the first used slot in this struct's backing array.
	 */
	public abstract int getOffset();

	/**
	 * Sets the offset of the firsy used slot in this struct's backing array.
	 */
	public abstract void setOffset(int aOffset);
	
	/**
	 * Returns the number of slots that can be used in the backing array.
	 */
	public abstract int getSize();
	
	/**
	 * Sets the number of slots that can be used in the backing array.
	 */
	public abstract void setSize(int aSize);
	
	/**
	 * Sets both the offset and size of this struct.
	 */
	public void setRange(int aOffset, int aSize)
	{
		setSize(aSize);
		setOffset(aOffset);
	}

	/**
	 * Returns the number of bits that can be stored withou
	 * growing the backing array, according to the current
	 * position.
	 */
	public final int getRemainingBits()
	{
		return getTotalBits() - getPos();
	}

	
	/**
	 * Returns the total number of bits that can be stored in
	 * this struct. 
	 */
	public abstract int getTotalBits();
	
	/**
	 * Returns the position of the next bit read or written.
	 */
	public abstract int getPos();

	/**
	 * Sets the position of the next bit read or written.
	 */
	public abstract void setPos(int aPos);
	
	/**
	 * Skips a number of bits.
	 */
	public final void skip(int aBits)
	{
		setPos(getPos() + aBits);
	}
	
	/**
	 * Resets the current bit pointer.
	 */
	public final void reset()
	{
		setPos(0);
	}
	
	/**
	 * Returns the bytes of this struct that are actually used,
	 * according to the current position.
	 */
	public byte[] packedBytes()
	{
		throw new UnsupportedOperationException();
	}
		
	public void writeLong(long aValue, int aBitCount)
	{
		throw new UnsupportedOperationException();
	}

	public void writeInt(int aValue, int aBitCount)
	{
		throw new UnsupportedOperationException();
	}
	
	public void writeBoolean(boolean aValue)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Writes a number of bits of the given byte array into this struct
	 * @param aBitCount The number of bits to write.
	 */
	public void writeBytes(byte[] aBytes, int aBitCount)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Writes the bits from the given byte array into this struct.
	 */
	public void writeBytes(byte[] aBytes)
	{
		writeBytes(aBytes, aBytes.length * 8);
	}
	
	/**
	 * Reads the specified number of bits from the struct and places them
	 * in a newly allocated array of bytes.
	 * The array is as small as possible ((bits+7)/8).
	 */
	public final byte[] readBytes(int aBitCount)
	{
		byte[] theResult = new byte[(aBitCount+7)/8];
		readBytes(aBitCount, theResult);
		return theResult;
	}
	
	/**
	 * Reads the specified number of bits from the struct and places them
	 * in the specified buffer
	 */
	public void readBytes(int aBitCount, byte[] aBuffer)
	{
		throw new UnsupportedOperationException();
	}
	
	public long readLong(int aBitCount)
	{
		throw new UnsupportedOperationException();
	}
	
	public int readInt(int aBitCount)
	{
		throw new UnsupportedOperationException();
	}
	
	public byte readByte(int aBitCount)
	{
		throw new UnsupportedOperationException();
	}
	
	public boolean readBoolean()
	{
		throw new UnsupportedOperationException();
	}
}
