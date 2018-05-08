/*
 * Created on Dec 16, 2004
 */
package zz.utils.references;

/**
 * @author gpothier
 */
public class HardRef<L> implements IRef<L>
{
	private L itsValue;
	
	public HardRef(L aValue)
	{
		itsValue = aValue;
	}
	
	public L get()
	{
		return itsValue;
	}
}
