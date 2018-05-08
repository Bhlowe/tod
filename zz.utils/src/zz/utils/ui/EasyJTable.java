/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Jan 25, 2002
 * Time: 4:26:04 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import zz.utils.ui.popup.StickyPopup;

/**
 * A JTable that allows easy editing of cell values.
 * Currently supported classes for editing are String and Double.
 */
public class EasyJTable extends JTable
{
	public EasyJTable (TableModel aTableModel, TableColumnModel aColumnModel)
	{
		super (aTableModel, aColumnModel);
		init ();
	}

	public EasyJTable (TableModel aTableModel)
	{
		super (aTableModel);
		init ();
	}

	private void init ()
	{
		setSurrendersFocusOnKeystroke(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCellSelectionEnabled(false);
		setRowSelectionAllowed(true);

		addMouseListener(new MouseAdapter ()
		{
			public void mouseClicked (MouseEvent e)
			{
				int theRow = rowAtPoint(e.getPoint());
				int theColumn = columnAtPoint(e.getPoint());
				editCellAt(theRow, theColumn);
			}
		});

		addKeyListener(new KeyAdapter ()
		{
			public void keyReleased (KeyEvent e)
			{
				int theOffset = 0;
				switch (e.getKeyCode())
				{
					case KeyEvent.VK_TAB:
						theOffset = e.isShiftDown() ? -1 : +1;

					case KeyEvent.VK_UP:
					case KeyEvent.VK_DOWN:
					case KeyEvent.VK_ENTER:
						int theRow = getSelectedRow();
						int theColumn = (getSelectedColumn() + theOffset) % getColumnCount();
						editCell (theRow, theColumn);
						break;
				}
			}

			private void editCell (int aRow, int aColumn)
			{
				setRowSelectionInterval(aRow, aRow);
				setColumnSelectionInterval(aColumn, aColumn);
				editCellAt(aRow, aColumn);
				JComponent theComponent = (JComponent)getEditorComponent();
				if (theComponent != null) theComponent.grabFocus();
			}


		});

//		getSelectionModel().addListSelectionListener(new ListSelectionListener ()
//		{
//			public void valueChanged (ListSelectionEvent e)
//			{
////				if (e.getValueIsAdjusting()) return;
//
//				int theRow = getSelectedRow();
//				int theColumn = getSelectedColumn();
//
//				System.out.println ("sel. r: "+theRow+" c:"+theColumn);
//
//				int theEditingRow = getEditingRow();
//				int theEditingColumn = getEditingColumn();
//				System.out.println ("sel1. er: "+theEditingRow+" ec:"+theEditingColumn);
//				/*if (! (theRow == theEditingRow && theColumn == theEditingColumn)
//					&& theEditingRow >= 0 && theEditingColumn >= 0) */editCellAt(theRow, theColumn);
//			}
//		});


		registerEditors();
	}

	protected void registerEditors ()
	{
		setDefaultEditor(String.class, StringEditor.getInstance());
		setDefaultEditor(Number.class, new NumberEditor ());
	}

	abstract static class TextFieldEditor extends AbstractCellEditor implements TableCellEditor
	{
		private JTextField itsTextField;

		private int itsCurrentRow;
		private int itsCurrentColumn;

		public TextFieldEditor ()
		{
			itsTextField = new JTextField();
			itsTextField.setBorder(null);

			itsTextField.getDocument().addDocumentListener(new DocumentListener ()
			{
				public void insertUpdate (DocumentEvent e)
				{
					checkInputValid();
				}

				public void removeUpdate (DocumentEvent e)
				{
					checkInputValid();
				}

				public void changedUpdate (DocumentEvent e)
				{
					checkInputValid();
				}
			});

			// Seems to work without it now... it keep it here just in case...
//			itsTextField.addMouseListener(new MouseAdapter ()
//			{
//				public void mouseReleased (MouseEvent e)
//				{
//					// That's the only way I found to have the text properly selected,
//					// textfield focused etc when the mouse is clicked.
//					Utils.simulateDeadKey(itsTextField);
//				}
//			});
//
			itsTextField.addFocusListener(new FocusAdapter ()
			{
				public void focusLost (FocusEvent e)
				{
					if (! e.isTemporary()) stopCellEditing();
				}
			});
		}

		public JTextField getTextField ()
		{
			return itsTextField;
		}

		protected void checkInputValid ()
		{
			itsTextField.setForeground(isInputValid() ? Color.black : Color.red);
		}

		protected boolean isInputValid ()
		{
			return isInputValid(itsTextField.getText());
		}

		public boolean stopCellEditing ()
		{
//			int theEditingRow = getEditingRow();
//			int theEditingColumn = getEditingColumn();
//			System.out.println ("sel. er: "+theEditingRow+" ec:"+theEditingColumn);
//			System.out.println ("cur. r: "+itsCurrentRow+" ec:"+itsCurrentColumn);


//			System.out.println ("TextFieldEditor.stopCellEditing");
			if (isInputValid()) return super.stopCellEditing ();
			else return false;
		}

		public Component getTableCellEditorComponent (JTable aTable, Object aValue,
													  boolean aSelected,
													  int aRow, int aColumn)
		{
			itsCurrentRow = aRow;
			itsCurrentColumn = aColumn;

			itsTextField.setText(getText(aValue));
			itsTextField.grabFocus();
			itsTextField.selectAll();
			return itsTextField;
		}

		public Object getCellEditorValue ()
		{
			return getValue (itsTextField.getText());
		}

		/**
		 * Returns the text to put in the textfield for the specified value.
		 * Called when editing starts
		 */
		protected abstract String getText (Object aValue);

		/**
		 * Returns the value corresponding to the specified text.
		 * Called when editing stops, or something like that...
		 */
		protected abstract Object getValue (String aString);

		protected abstract boolean isInputValid (String aString);
	}

	public static class StringEditor extends TextFieldEditor
	{
		private static final StringEditor INSTANCE = new StringEditor ();

		public static StringEditor getInstance ()
		{
			return INSTANCE;
		}

		private StringEditor ()
		{
		}


		protected String getText (Object aValue)
		{
			return (String) aValue;
		}

		protected Object getValue (String aString)
		{
			return aString;
		}

		protected boolean isInputValid (String aString)
		{
			return true;
		}
	}

	private static final DecimalFormat DECIMAL2 = new DecimalFormat("#.##");

	public static class NumberRenderer extends DefaultTableCellRenderer
	{
		private NumberFormat itsNumberFormat;

		public NumberRenderer ()
		{
			this (DECIMAL2);
		}

		public NumberRenderer (NumberFormat aNumberFormat)
		{
			itsNumberFormat = aNumberFormat;
		}

		public Component getTableCellRendererComponent (JTable table, Object aValue,
														boolean isSelected, boolean hasFocus, int row, int column)
		{
			Number theNumber = (Number) aValue;
            aValue = itsNumberFormat.format(theNumber.doubleValue());

			JLabel theLabel = (JLabel) super.getTableCellRendererComponent (table, aValue, isSelected, hasFocus, row, column);
			theLabel.setHorizontalAlignment(JLabel.RIGHT);
			return theLabel;
		}
	}

	public static class NumberEditor extends TextFieldEditor
	{
		private NumberFormat itsNumberFormat;

		public NumberEditor ()
		{
			this (DECIMAL2);
		}

		public NumberEditor (NumberFormat aNumberFormat)
		{
			itsNumberFormat = aNumberFormat;
			getTextField().setHorizontalAlignment(JTextField.RIGHT);
		}

		protected String getText (Object aValue)
		{
			Number theNumber = (Number) aValue;
			String theResult = itsNumberFormat.format(theNumber.doubleValue());
			return theResult;
		}

		protected Object getValue (String aString)
		{
			try
			{
				Number theNumber = itsNumberFormat.parse(aString);
				return theNumber;
			}
			catch (ParseException e)
			{
				return new Double (Double.NaN);
			}
		}

		protected boolean isInputValid (String aString)
		{
			try
			{
				itsNumberFormat.parse(aString);
				return true;
			}
			catch (ParseException e)
			{
				return false;
			}
		}
	}

	public abstract static class PopupEditor extends AbstractCellEditor implements TableCellEditor, OptionListener
	{
		private JLabel itsLabel;
		private StickyPopup itsPopup;
		private boolean itsShow;

		protected abstract JComponent createPopupComponent ();

		public final Component getTableCellEditorComponent (JTable aTable, Object aValue,
													  boolean aSelected,
													  int aRow, int aColumn)
		{
			return getLabel(aTable, aSelected, aValue);
		}

		public JLabel getLabel (JTable aTable, boolean aSelected, Object aValue)
		{
			if (itsPopup == null)
			{
				itsLabel = new JLabel ()
				{
					public void paint (Graphics g)
					{
						super.paint (g);
						showPopup ();
					}
				};
				itsPopup = new StickyPopup(createPopupComponent(), itsLabel);
			}

			itsShow = true;
			return itsLabel;
		}

		private void showPopup ()
		{
			if (itsShow) itsPopup.show();
			itsShow = false;
		}

		protected void hide ()
		{
			itsPopup.hide();
			stopCellEditing();
		}
	}

	public static class ColorEditor extends PopupEditor implements TableCellEditor, OptionListener
	{
		private zz.utils.ui.ColorChooserPanel itsColorChooserPanel;
		private Color itsColor;

		public Object getCellEditorValue ()
		{
			return itsColor;
		}

		public JLabel getLabel (JTable aTable, boolean aSelected, Object aValue)
		{
			JLabel theLabel = super.getLabel (aTable, aSelected, aValue);
			itsColor = (Color) aValue;
			itsColorChooserPanel.setSelectedColor(itsColor);
			theLabel.setBackground(itsColor);
			theLabel.setOpaque(true);
			return theLabel;
		}

		protected JComponent createPopupComponent ()
		{
			itsColorChooserPanel = new zz.utils.ui.ColorChooserPanel();
			itsColorChooserPanel.addOptionListener(this);

			return itsColorChooserPanel;
		}

		public void optionSelected (OptionListener.Option aOption)
		{
			if (aOption == OptionListener.Option.OK) itsColor = itsColorChooserPanel.getSelectedColor();
			hide();
		}
	}

//	public static class FontEditor extends PopupEditor implements TableCellEditor, OptionListener
//	{
//		private zz.utils.ui.font.FontChooserPanel itsFontChooserPanel;
//		private Font itsFont;
//
//		public Object getCellEditorValue ()
//		{
//			return itsFont;
//		}
//
//		public JLabel getLabel (JTable aTable, boolean aSelected, Object aValue)
//		{
//			JLabel theLabel = super.getLabel (aTable, aSelected, aValue);
//			itsFont = (Font) aValue;
//			itsFontChooserPanel.setSelectedFont(itsFont);
//			theLabel.setText(itsFont.getName());
//			theLabel.setOpaque(true);
//			return theLabel;
//		}
//
//		protected JComponent createPopupComponent ()
//		{
//			itsFontChooserPanel = new zz.utils.ui.font.FontChooserPanel();
//			itsFontChooserPanel.addOptionListener(this);
//
//			return itsFontChooserPanel;
//		}
//
//		public void optionSelected (OptionListener.Option aOption)
//		{
//			if (aOption == OptionListener.Option.OK) itsFont = itsFontChooserPanel.getSelectedFont();
//			hide();
//		}
//	}
//
	public static class BooleanRenderer extends DefaultTableCellRenderer
	{
		private JCheckBox itsCheckBox;

		public BooleanRenderer ()
		{
			itsCheckBox = new JCheckBox();
			itsCheckBox.setOpaque(false);
		}

		public Component getTableCellRendererComponent (JTable table, Object aValue,
														boolean isSelected, boolean hasFocus, int row, int column)
		{
			Boolean theBoolean = (Boolean) aValue;
			itsCheckBox.setSelected(theBoolean.booleanValue());
			return itsCheckBox;
		}
	}


	public static class BooleanEditor extends AbstractCellEditor implements TableCellEditor
	{
		private JCheckBox itsCheckBox;

		public BooleanEditor ()
		{
			itsCheckBox = new JCheckBox();
			itsCheckBox.setOpaque(false);

			itsCheckBox.addActionListener(new ActionListener ()
			{
				public void actionPerformed (ActionEvent e)
				{
					stopCellEditing();
				}
			});
		}

		public Component getTableCellEditorComponent (JTable aTable, Object aValue,
													  boolean aSelected,
													  int aRow, int aColumn)
		{
			Boolean theBoolean = (Boolean) aValue;
			itsCheckBox.setSelected(theBoolean.booleanValue());
			return itsCheckBox;
		}

		public Object getCellEditorValue ()
		{
			return new Boolean (itsCheckBox.isSelected());
		}

	}


}
