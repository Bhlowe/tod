/*
 * Created on Dec 30, 2008
 */
package zz.utils.srpc;

/**
 * Remote interface to the {@link SRPCRegistry} object.
 * @author gpothier
 */
public interface RIRegistry extends IRemote
{
	/**
	 * Returns the object registered with the specified name.
	 */
	public Object lookup(String aName);
}
