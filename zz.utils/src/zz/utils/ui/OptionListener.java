/**
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Apr 8, 2003
 * Time: 12:21:35 PM
 */
package zz.utils.ui;

import javax.swing.JOptionPane;

/**
 * This interface is for listener that are interested in events
 * of the same type that the return values of the {@link JOptionPane}
 */
public interface OptionListener
{
	/**
	 * Called when an option is selected (eg, a button clicked)
	 * @param aOption
	 */
	public void optionSelected (Option aOption);

	/**
	 * Enumeration of the various options.
	 */
	public static class Option
	{
		public static final Option OK = new Option("Ok");
		public static final Option CANCEL = new Option("Cancel");

		private String itsName;

		private Option (String aName)
		{
			itsName = aName;
		}

		public String getName ()
		{
			return itsName;
		}
	}
}
