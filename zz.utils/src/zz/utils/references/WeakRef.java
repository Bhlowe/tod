/*
 * Created on Dec 16, 2004
 */
package zz.utils.references;

import java.lang.ref.WeakReference;

/**
 * @author gpothier
 */
public class WeakRef<L> extends WeakReference<L> implements IRef<L>
{
	public WeakRef(L aValue)
	{
		super(aValue);
	}
}
