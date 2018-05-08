/*
 * Created on Jul 21, 2006
 */
package zz.utils.bit;

/**
 * A structure that contains fields of variable length, where the lengths
 * are expressed in bits.
 * To facilitate operation, a pointer to a current position is maintained and
 * updated by certain methods.
 * @author gpothier
 */
public class ByteBitStruct extends BitStruct
{
	private byte[] itsBytes;
	
	private int itsPos;
	
	/**
	 * Offset (in slots) of the first used byte in the array.
	 */
	private int itsOffset;
	
	/**
	 * Number of array slots  that can be used.
	 */
	private int itsSize;
	
	public ByteBitStruct(byte[] aBytes, int aOffset, int aSize)
	{
		if (aBytes != null) setBytes(aBytes);
		itsOffset = aOffset;
		itsSize = aSize;
	}
	
	public ByteBitStruct(byte[] aBytes)
	{
		this(aBytes, 0, aBytes.length);
	}
	
	public ByteBitStruct(int aBitCount)
	{
		this(new byte[(aBitCount+7)/8]);
	}
	
	/**
	 * Construct a struct with an initial size of 64 bits.
	 */
	public ByteBitStruct()
	{
		this(64);
	}

	protected void setBytes(byte[] bytes)
	{
		itsBytes = bytes;
	}

	protected byte[] getData()
	{
		return itsBytes;
	}
	
	public int getOffset()
	{
		return itsOffset;
	}

	public void setOffset(int aOffset)
	{
		itsOffset = aOffset;
	}
	
	public int getSize()
	{
		return itsSize;
	}

	public void setSize(int aSize)
	{
		itsSize = aSize;
	}

	
	@Override
	public int getTotalBits()
	{
		return itsSize*8;
	}

	
	/**
	 * Returns the position of the next bit read or written.
	 */
	public int getPos()
	{
		return itsPos;
	}

	/**
	 * Sets the position of the next bit read or written.
	 */
	public void setPos(int aPos)
	{
		itsPos = aPos;
	}
	
	/**
	 * Returns the bytes of this struct that are actually used,
	 * according to the current position.
	 */
	public byte[] packedBytes()
	{
		byte[] theResult = new byte[(itsPos+7)/8];
		System.arraycopy(getData(), itsOffset, theResult, 0, theResult.length);
		return theResult;
	}
	
	/**
	 * Grows the storage space so that it allows for at least for 
	 * the given size (in bits).
	 */
	protected void grow(int aMinSize)
	{
		if (itsOffset != 0 || itsSize != getData().length) throw new UnsupportedOperationException("Cannot grow a struct when offset is not 0");
		
		int theNewSize = Math.max(getData().length*2, (aMinSize+7)/8);
		byte[] theNewBytes = new byte[theNewSize];
		System.arraycopy(getData(), 0, theNewBytes, 0, getData().length);
		setBytes(theNewBytes);
	}
	
	private void checkCapacity(int aMinCapacity)
	{
		if (itsSize * 8 < aMinCapacity) throw new RuntimeException("Insufficient capacity");;
	}
	
	private void ensureCapacity(int aMinCapacity)
	{
		if (itsSize * 8 < aMinCapacity) grow(aMinCapacity);
	}
	
	public void writeLong(long aValue, int aBitCount)
	{
		ensureCapacity(itsPos+aBitCount);
		
		BitUtils.writeLong(getData(), itsOffset, aValue, itsPos, aBitCount);
		itsPos += aBitCount;
	}

	public void writeInt(int aValue, int aBitCount)
	{
		ensureCapacity(itsPos+aBitCount);
		
		BitUtils.writeInt(getData(), itsOffset, aValue, itsPos, aBitCount);
		itsPos += aBitCount;
	}
	
	public void writeBoolean(boolean aValue)
	{
		ensureCapacity(itsPos+1);
		
		BitUtils.writeBoolean(getData(), itsOffset, aValue, itsPos);
		itsPos += 1;
	}
	
	/**
	 * Writes a number of bits of the given byte array into this struct
	 * @param aBitCount The number of bits to write.
	 */
	public void writeBytes(byte[] aBytes, int aBitCount)
	{
		ensureCapacity(itsPos+aBitCount);
		
		int i = 0;
		while (aBitCount > 0)
		{
			byte b = aBytes[i++];
			int theBits = Math.min(aBitCount, 8);
			BitUtils.writeByte(getData(), itsOffset, b, itsPos, theBits);
			itsPos += theBits;
			aBitCount -= theBits;
		}
	}
	
	@Override
	public void readBytes(int aBitCount, byte[] aBuffer)
	{
		checkCapacity(itsPos+aBitCount);

		int i = 0;
		while (aBitCount > 0)
		{
			int theBits = Math.min(aBitCount, 8);
			aBuffer[i++] = BitUtils.readByte(getData(), itsOffset, itsPos, theBits);
			aBitCount -= theBits;
			itsPos += theBits;
		}
	}
	
	public long readLong(int aBitCount)
	{
		checkCapacity(itsPos+aBitCount);

		long theResult = BitUtils.readLong(getData(), itsOffset, itsPos, aBitCount);
		itsPos += aBitCount;
		return theResult;
	}
	
	public int readInt(int aBitCount)
	{
		checkCapacity(itsPos+aBitCount);

		int theResult = BitUtils.readInt(getData(), itsOffset, itsPos, aBitCount);
		itsPos += aBitCount;
		return theResult;
	}
	
	public byte readByte(int aBitCount)
	{
		checkCapacity(itsPos+aBitCount);

		byte theResult = BitUtils.readByte(getData(), itsOffset, itsPos, aBitCount);
		itsPos += aBitCount;
		return theResult;
	}
	
	public boolean readBoolean()
	{
		checkCapacity(itsPos+1);

		byte theResult = BitUtils.readByte(getData(), itsOffset, itsPos, 1);
		itsPos += 1;
		return theResult != 0;
	}
	
	@Override
	public String toString()
	{
		StringBuilder theBuilder = new StringBuilder("BitStruct: ");
		for (int j=itsOffset;j<itsOffset+itsSize;j++)
		{
			byte b = getData()[j];
			for(int i=0;i<8;i++)
			{
				theBuilder.append((b & 1) == 1 ? "1" : "0");
				b >>>= 1;
			}
			theBuilder.append(' ');
		}
		
		return theBuilder.toString();
	}
}
