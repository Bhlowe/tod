/*
 * Created on Jun 19, 2007
 */
package zz.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * A pipe of {@link InputStream}/{@link OutputStream}.
 * @author gpothier
 */
public class StreamPipe
{
	private PipedInputStream2 itsInputStream;
	private PipedOutputStream2 itsOutputStream;
	
	public StreamPipe()
	{
		try
		{
			itsInputStream = new PipedInputStream2();
			itsOutputStream = new PipedOutputStream2(itsInputStream);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void close()
	{
		try
		{
			itsInputStream.close();
			itsOutputStream.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public InputStream getInputStream()
	{
		return itsInputStream;
	}

	public OutputStream getOutputStream()
	{
		return itsOutputStream;
	}
}
