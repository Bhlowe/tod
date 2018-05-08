package zz.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * See {@link RandomAccessInputStream}
 * @author gpothier
 */
public class RandomAccessOutputStream extends OutputStream
{
	private final RandomAccessFile itsFile;

	public RandomAccessOutputStream(RandomAccessFile aFile)
	{
		itsFile = aFile;
	}

	@Override
	public void write(byte[] aB, int aOff, int aLen) throws IOException
	{
		itsFile.write(aB, aOff, aLen);
	}

	@Override
	public void write(int aB) throws IOException
	{
		itsFile.write(aB);
	}
}
