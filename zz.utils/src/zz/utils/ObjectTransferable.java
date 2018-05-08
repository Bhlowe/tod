/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Jan 11, 2002
 * Time: 11:20:47 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * A simple implementation of Transferable that uses DataFlavor.javaJVMLocalObjectMimeType
 * and contains a java Object
 */
public class ObjectTransferable implements Transferable
{
	public static DataFlavor FLAVOR_JVMLOCAL;

	private static DataFlavor[] FLAVORS;
	static
	{
		try
		{
			FLAVOR_JVMLOCAL = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
			FLAVORS = new DataFlavor[] {FLAVOR_JVMLOCAL};
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}


	protected Object itsObject;

	public ObjectTransferable (Object aObject)
	{
		itsObject = aObject;
	}

	public Object getObject ()
	{
		return itsObject;
	}

	public void setObject (Object aObject)
	{
		itsObject = aObject;
	}

	public Object getTransferData (DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		return itsObject;
	}

	public DataFlavor[] getTransferDataFlavors ()
	{
		return FLAVORS;
	}

	public boolean isDataFlavorSupported (DataFlavor flavor)
	{
		return FLAVORS[0].equals(flavor);
	}
}
