package zz.utils.ui;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * A panel that displays OK and Cancel buttons at the bottom,
 * and that lets subclasses add properly aligned rows that
 * consist of a caption and a component.
 * @author gpothier
 */
public abstract class AbstractColumnsOptionPanel extends AbstractOptionPanel
{
	private ColumnsLayout itsLayout = new ColumnsLayout(5);
	private JPanel itsComponent = new JPanel(new GridStackLayout(1, 0, 5));

	public AbstractColumnsOptionPanel(boolean aDeferCreation)
	{
		super(true);
		if (! aDeferCreation) createUI();
	}
	
	public AbstractColumnsOptionPanel()
	{
		this(false);
	}
	
	/**
	 * Subclasses override this method and calls {@link #addRow(String, JComponent)} to add rows.
	 */
	protected abstract void createRows();
	
	protected void addRow(String aCaption, JComponent aComponent)
	{
		addRow(new JLabel(aCaption), aComponent);
	}
	
	protected void addRow(JComponent aCaption, JComponent aComponent)
	{
		JPanel panel = new JPanel(itsLayout);
		itsLayout.addContainer(panel);
		panel.add(aCaption);
		panel.add(aComponent);
		itsComponent.add(panel);
	}
	
	@Override
	protected final JComponent createComponent()
	{
		createRows();
		return itsComponent;
	}

}
