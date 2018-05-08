/*
 * Created on Jun 19, 2007
 */
package zz.utils;

import java.io.OutputStream;

/**
 * An output stream that accepts data and discards it immediately.
 * @author gpothier
 */
public class NullOutputStream extends OutputStream
{

	@Override
	public void write(byte[] aB, int aOff, int aLen) 
	{
	}

	@Override
	public void write(byte[] aB) 
	{
	}

	@Override
	public void write(int aB) 
	{
	}

}
