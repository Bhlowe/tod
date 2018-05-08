/*
 * Created on Feb 25, 2006
 */
package zz.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Encapsulates access to a properties file.
 * By default, the properties are automatically saved whenever
 * a property is modified.
 * @author gpothier
 */
public class PropertyFile
{
	private File itsFile;
	private String itsComment;
	private Properties itsProperties; 
	private boolean itsAutoSave;

	public PropertyFile(File aFile)
	{
		this(aFile, null, true);
	}
	
	public PropertyFile(File aFile, String aComment)
	{
		this(aFile, aComment, true);
	}

	public PropertyFile(File aFile, String aComment, boolean aAutoSave)
	{
		itsFile = aFile;
		itsComment = aComment;
		itsAutoSave = aAutoSave;
		
		try
		{
			if (! itsFile.exists()) itsFile.createNewFile();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		load();
	}

	private void autoSave()
	{
		if (isAutoSave()) save();
	}
	
	public boolean isAutoSave()
	{
		return itsAutoSave;
	}

	/**
	 * Whether ti automatically save properties when an entry is modified.
	 */
	public void setAutoSave(boolean aAutoSave)
	{
		itsAutoSave = aAutoSave;
	}

	public String getComment()
	{
		return itsComment;
	}

	public void setComment(String aComment)
	{
		itsComment = aComment;
	}

	private void load()
	{
		itsProperties = new Properties();
		try
		{
			itsProperties.load(new FileInputStream(itsFile));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void save()
	{
		try
		{
			itsProperties.store(new FileOutputStream(itsFile), itsComment);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public String get(String aKey)
	{
		return get(aKey, null);
	}
	
	public String get(String aKey, String aDefault)
	{
		String theProperty = itsProperties.getProperty(aKey);
		return theProperty != null ? theProperty : aDefault;
	}
	
	public boolean getBoolean(String aKey)
	{
		return getBoolean(aKey, false);
	}
	
	public boolean getBoolean(String aKey, boolean aDefault)
	{
		String theProperty = get(aKey);
		return theProperty != null ? Boolean.parseBoolean(theProperty) : aDefault;
	}
	
	public int getInteger(String aKey)
	{
		return getInteger(aKey, 0);
	}
	
	public int getInteger(String aKey, int aDefault)
	{
		String theProperty = get(aKey);
		return theProperty != null ? Integer.parseInt(theProperty) : aDefault;
	}
	
	public void set(String aKey, String aValue)
	{
		itsProperties.setProperty(aKey, aValue);
		autoSave();
	}
	
	public void set(String aKey, boolean aValue)
	{
		set(aKey, Boolean.toString(aValue));	
	}
	
	public void set(String aKey, int aValue)
	{
		set(aKey, Integer.toString(aValue));
	}
	
	
}
