/**
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Sep 20, 2003
 * Time: 5:22:41 PM
 * To change this template use Options | File Templates.
 */
package zz.utils.ui;

import javax.swing.JComponent;

/**
 * A concrete subclass of {@link AbstractOptionPanel} that lets
 * the user specify the panel to use in the constructor.
 */
public class SimpleOptionPanel extends AbstractOptionPanel
{
	public SimpleOptionPanel(JComponent aComponent)
	{
		setComponent(aComponent);
	}

	protected JComponent createComponent()
	{
		return null;
	}
}
