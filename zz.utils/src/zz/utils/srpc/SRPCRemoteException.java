/*
 * Created on Dec 30, 2008
 */
package zz.utils.srpc;

public class SRPCRemoteException extends RuntimeException
{
	public SRPCRemoteException()
	{
	}

	public SRPCRemoteException(String aMessage, Throwable aCause)
	{
		super(aMessage, aCause);
	}

	public SRPCRemoteException(String aMessage)
	{
		super(aMessage);
	}

	public SRPCRemoteException(Throwable aCause)
	{
		super(aCause);
	}
}
