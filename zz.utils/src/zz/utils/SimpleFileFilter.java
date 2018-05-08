/*
 * Created on Nov 25, 2004
 */
package zz.utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * A simple file filter for {@link javax.swing.JFileChooser}.
 * It accepts files based on their extension.
 * It accepts all directories.
 * @author gpothier
 */
public class SimpleFileFilter extends FileFilter
{
	private String itsDescription;
	private String[] itsExtensions;
	
	/**
	 * Constructs a new file filter.
	 * @param aDescription A description for this filter
	 * @param aExtensions An array of extensions, eg. ".txt", ".png"...
	 */
	public SimpleFileFilter(String aDescription, String... aExtensions)
	{
		itsDescription = aDescription;
		itsExtensions = aExtensions;
	}
	
	public boolean accept(File aF)
	{
		if (aF.isDirectory()) return true;
		
		String theName = aF.getName();
		for (String theExtension : itsExtensions)
		{
			if (theName.endsWith(theExtension)) return true;
		}
		return false;
	}
	
	public String getDescription()
	{
		return itsDescription;
	}
}
