/*
 * Created on Dec 30, 2008
 */
package zz.utils.srpc;

import java.lang.reflect.Method;
import java.util.Comparator;


public class SRPCUtils
{
	public static class MethodComparator implements Comparator<Method>
	{
		private static MethodComparator INSTANCE = new MethodComparator();

		public static MethodComparator getInstance()
		{
			return INSTANCE;
		}

		private MethodComparator()
		{
		}
		
		public int compare(Method aO1, Method aO2)
		{
			return getDesc(aO1).compareTo(getDesc(aO2));
		}
		
	}

	public static String getDesc(Method aMethod)
	{
		StringBuilder theBuilder = new StringBuilder();
		theBuilder.append(aMethod.getName());
		theBuilder.append("(");
		for (Class theClass : aMethod.getParameterTypes())
		{
			theBuilder.append(theClass.getName());
			theBuilder.append(" ");
		}
		theBuilder.append(")");
		
		return theBuilder.toString();
	}

	public static Class getRemoteInterface(Object aObj)
	{
		Class[] theInterfaces = aObj.getClass().getInterfaces();
		for (Class theInterface : theInterfaces)
		{
			if (IRemote.class.isAssignableFrom(theInterface)) return theInterface;
		}
		throw new RuntimeException("Can't happen");
	}

}
