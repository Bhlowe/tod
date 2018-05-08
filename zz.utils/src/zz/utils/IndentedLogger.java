/*
 * Created on Oct 14, 2005
 */
package zz.utils;

public class IndentedLogger
{
	public static final IndentedLogger DEFAULT = new IndentedLogger();
	
	private int itsIndent = 0;
	
	public void log (String aMessage)
	{
		StringBuilder theBuilder = new StringBuilder();
		for (int i=0;i<itsIndent;i++) theBuilder.append('-');
		theBuilder.append(aMessage);
		
		System.out.println(theBuilder.toString());
	}
	
	public void inc(String aMessage)
	{
		log (aMessage);
		itsIndent++;
	}
	
	public void dec(String aMessage)
	{
		itsIndent--;
		log (aMessage);
	}
}
