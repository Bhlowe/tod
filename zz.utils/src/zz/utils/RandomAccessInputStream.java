package zz.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Wraps a {@link RandomAccessFile} in an {@link InputStream}.
 * The current position of the file is taken as a start position.
 * If another thread modifies the offset of the {@link RandomAccessFile},
 * unexpected results can occur.
 * @author gpothier
 *
 */
public class RandomAccessInputStream extends InputStream
{
	private final RandomAccessFile itsFile;

	public RandomAccessInputStream(RandomAccessFile aFile)
	{
		itsFile = aFile;
	}

	@Override
	public int read(byte[] aB, int aOff, int aLen) throws IOException
	{
		return itsFile.read(aB, aOff, aLen);
	}

	@Override
	public int read() throws IOException
	{
		return itsFile.read();
	}

	@Override
	public long skip(long aN) throws IOException
	{
		itsFile.seek(itsFile.getFilePointer()+aN);
		return aN;
	}
	
	
}
