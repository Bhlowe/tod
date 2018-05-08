/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Oct 29, 2001
 * Time: 3:52:58 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils;

public class TimeMeasurer
{
	protected static long t0;

	public static void reset (String message)
	{
		t0 = System.currentTimeMillis();
		System.out.println ("TimeMeasurer reset: "+message);
	}

	public static void measure (String message)
	{
		long t = System.currentTimeMillis();
		float s = 0.001f*(t-t0);
		System.out.println ("TimeMeasurer: "+s+"s "+message);
	}
}
