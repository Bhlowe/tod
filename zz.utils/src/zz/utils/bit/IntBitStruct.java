/*
 * Created on Jul 25, 2006
 */
package zz.utils.bit;

/**
 * Similar to {@link ByteBitStruct} but backed by an int array.
 * @author gpothier
 */
public class IntBitStruct extends BitStruct
{
	private int[] itsData;
	
	private int itsPos;
	
	/**
	 * Offset (in array elements) of the first used element in the array.
	 */
	private int itsOffset;
	
	private int itsSize;
	
	public IntBitStruct(int[] aData, int aOffset, int aSize)
	{
		if (aData != null) setData(aData);
		itsOffset = aOffset;
		itsSize = aSize;
	}
	
	public IntBitStruct(int[] aData)
	{
		this(aData, 0, aData.length);
	}
	
	public IntBitStruct(int aBitCount)
	{
		this(new int[(aBitCount+31)/32]);
	}
	
	/**
	 * Construct a struct with an initial size of 64 bits.
	 */
	public IntBitStruct()
	{
		this(64);
	}

	protected void setData(int[] aData)
	{
		itsData = aData;
	}

	protected int[] getData()
	{
		return itsData;
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

	public int getTotalBits()
	{
		return itsSize*32;
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
		assert aPos>=0;
		itsPos = aPos;
	}
	
	/**
	 * Grows the storage space so that it allows for at least for 
	 * the given size (in bits).
	 */
	protected void grow(int aMinSize)
	{
		if (itsOffset != 0 || itsSize != getData().length) throw new UnsupportedOperationException("Cannot grow a struct when offset is not 0");
		
		int theNewSize = Math.max(getData().length*2, (aMinSize+31)/32);
		int[] theNewData = new int[theNewSize];
		System.arraycopy(getData(), 0, theNewData, 0, getData().length);
		setData(theNewData);
	}
	
	private void checkCapacity(int aMinCapacity)
	{
		if (itsSize * 32 < aMinCapacity) throw new RuntimeException("Insufficient capacity");
	}
	
	private void ensureCapacity(int aMinCapacity)
	{
		if (itsSize * 32 < aMinCapacity) grow(aMinCapacity);
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
		
		BitUtils.writeInt(getData(), itsOffset, aValue ? 1 : 0, itsPos, 1);
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
			BitUtils.writeInt(getData(), itsOffset, b, itsPos, theBits);
			itsPos += theBits;
			aBitCount -= theBits;
		}
	}
	
	/**
	 * Writes the bits from the given byte array into this struct.
	 */
	public void writeBytes(byte[] aBytes)
	{
		writeBytes(aBytes, aBytes.length * 8);
	}
	
	@Override
	public void readBytes(int aBitCount, byte[] aBuffer)
	{
		checkCapacity(itsPos+aBitCount);
		
		int i = 0;
		while (aBitCount > 0)
		{
			int theBits = Math.min(aBitCount, 8);
			aBuffer[i++] = (byte) (BitUtils.readInt(getData(), itsOffset, itsPos, theBits) & 0xff);
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

		byte theResult = (byte) (BitUtils.readInt(getData(), itsOffset, itsPos, aBitCount) & 0xff);
		itsPos += aBitCount;
		return theResult;
	}
	
	public boolean readBoolean()
	{
		checkCapacity(itsPos+1);

		int theResult = BitUtils.readInt(getData(), itsOffset, itsPos, 1);
		itsPos += 1;
		return theResult != 0;
	}
	
	@Override
	public String toString()
	{
		StringBuilder theBuilder = new StringBuilder("BitStruct: ");
		for (int j=itsOffset;j<itsOffset+itsSize;j++)
		{
			int theData = getData()[j];
			theBuilder.append(Integer.toHexString(theData));
			theBuilder.append(' ');
		}
		
		return theBuilder.toString();
	}
}
