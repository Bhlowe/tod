/*
 * Created on Oct 19, 2006
 */
package zz.utils;

public abstract class Future<T> extends Thread
{
	private T itsResult;
	private Throwable itsException;
	private boolean itsDone = false;
	
	public Future()
	{
		this(true);
	}
	
	protected Future(boolean aStart)
	{
		super("Future");
		if (aStart) start();
	}

	@Override
	public synchronized final void run()
	{
		try
		{
			done(fetch());
		}
		catch (Throwable e)
		{
			exception(e);
		}
	}
	
	/**
	 * Signals that this future obtained its value.
	 */
	protected void done(T aResult)
	{
		itsResult = aResult;
		itsDone = true;
		notifyAll();		
	}
	
	/**
	 * Signals that the execution of this furure failed.
	 */
	protected void exception(Throwable aThrowable)
	{
		itsException = aThrowable;
		done(null);
	}
	
	protected abstract T fetch() throws Throwable;
	
	public synchronized T get()
	{
		try
		{
			while (! itsDone) wait();
			
			if (itsException != null) throw new RuntimeException(itsException);
			return itsResult;
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public boolean isDone()
	{
		return itsDone;
	}
}