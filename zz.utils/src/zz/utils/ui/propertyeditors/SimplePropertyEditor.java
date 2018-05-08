package zz.utils.ui.propertyeditors;

import java.awt.Color;
import java.lang.reflect.Constructor;

import javax.swing.JPanel;

import zz.utils.Utils;
import zz.utils.properties.IProperty;
import zz.utils.properties.IPropertyListener;
import zz.utils.properties.IRWProperty;
import zz.utils.undo2.UndoStack;

@SuppressWarnings("serial")
public abstract class SimplePropertyEditor<T> extends JPanel
implements IPropertyListener<T>
{
	private UndoStack itsUndoStack;
	private IRWProperty<T> itsProperty;

	public SimplePropertyEditor(UndoStack aUndoStack, IRWProperty<T> aProperty)
	{
		itsUndoStack = aUndoStack;
		itsProperty = aProperty;
	}
	
	@Override
	public void addNotify()
	{
		super.addNotify();
		propertyToUi();
		getProperty().addHardListener(this);
	}
	
	@Override
	public void removeNotify()
	{
		super.removeNotify();
		getProperty().removeListener(this);
	}

	public IRWProperty<T> getProperty()
	{
		return itsProperty;
	}
	
	public void setProperty(IRWProperty<T> aProperty)
	{
		uiToProperty();
		itsProperty = aProperty;
		propertyToUi();
	}

	public void propertyChanged(IProperty<T> aProperty, T aOldValue, T aNewValue)
	{
		T v = uiToValue();
		if (! Utils.equalOrBothNull(v, aNewValue)) valueToUi(aNewValue);
	}
	
	protected final void uiToProperty()
	{
		T v = uiToValue();
		if (! Utils.equalOrBothNull(v, getProperty().get()))
		{
			startOperation();
			getProperty().set(v);
			commitOperation();
		}
	}
	
	protected final void propertyToUi()
	{
		valueToUi(getProperty().get());
	}

	protected abstract void valueToUi(T aValue);
	protected abstract T uiToValue();
	
	protected void startOperation()
	{
		if (itsUndoStack != null) itsUndoStack.startOperation();
	}
	
	protected void commitOperation()
	{
		if (itsUndoStack != null) itsUndoStack.commitOperation();
	}
	
	protected void cancelOperation()
	{
		if (itsUndoStack != null) itsUndoStack.cancelOperation();
	}
	
	public static <T> SimplePropertyEditor<T> createEditor(
			Class<? extends SimplePropertyEditor<T>> aEditorClass,
			UndoStack aUndoStack, 
			IRWProperty<T> aProperty)
	{
		try
		{
			Constructor<? extends SimplePropertyEditor<T>> theConstructor = 
				aEditorClass.getConstructor(UndoStack.class, IRWProperty.class);
			return theConstructor.newInstance(aUndoStack, aProperty);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static <T> Class<? extends SimplePropertyEditor<T>> getDefaultEditorClass(Class<T> aClass)
	{
		if (aClass == Float.class) return (Class) FloatPropertyEditor.LogSlider1000.class;
		else if (aClass == Boolean.class) return (Class) BooleanPropertyEditor.CheckBox.class;
		else if (aClass == String.class) return (Class) StringPropertyEditor.TextField.class;
		else if (aClass == Integer.class) return (Class) IntegerPropertyEditor.Spinner.class;
		else if (aClass == Color.class) return (Class) ColorPropertyEditor.ButtonPopup.class;
		else throw new EditorNotFoundException(aClass);
	}
	
	public static class EditorNotFoundException extends RuntimeException
	{
		public EditorNotFoundException(Class aClass)
		{
			super(aClass.getName());
		}
	}
}
