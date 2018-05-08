/*
 * Created on 05-nov-2004
 */
package zz.utils.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Interface for classes that are potentially interested in key events.
 * It is based on AWT's {@link java.awt.event.KeyListener}
 * @author gpothier
 */
public interface IKeyAware extends KeyListener
{
	/**
	 * Indicates if this object is currently interested in key events.
	 */
	public boolean isKeyAware ();
}
