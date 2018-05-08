/*
 * Created on Dec 30, 2008
 */
package zz.utils.srpc;

import java.util.HashMap;
import java.util.Map;

/**
 * The root object experted by an SRPC server. 
 * Similar to the RMI registry.
 * @author gpothier
 */
public class SRPCRegistry implements RIRegistry
{
	private Map<String, IRemote> itsRemotesMap = new HashMap<String, IRemote>();
	
	public Object lookup(String aName)
	{
		return itsRemotesMap.get(aName);
	}
	
	public void bind(String aName, IRemote aRemote)
	{
		if (itsRemotesMap.containsKey(aName)) throw new RuntimeException("Already bound: "+aName);
		itsRemotesMap.put(aName, aRemote);
	}
	
	public void rebind(String aName, IRemote aRemote)
	{
		itsRemotesMap.put(aName, aRemote);
	}
	
}
