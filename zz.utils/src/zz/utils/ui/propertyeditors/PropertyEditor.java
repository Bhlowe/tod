package zz.utils.ui.propertyeditors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import zz.utils.properties.IRWProperty;
import zz.utils.undo2.UndoStack;

/**
 * This annotation can be associated to a property so as to indicate
 * which editor to use to edit its value.
 * The specified class must have a constructor that takes the property
 * as its only argument.
 * Use {@link PropertyEditor.NoEditor} if the property should not be edited.
 * @author gpothier
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyEditor {
	Class<? extends SimplePropertyEditor<?>> value();
	
	public static abstract class NoEditor extends SimplePropertyEditor<Object>
	{
		public NoEditor(UndoStack aUndoStack, IRWProperty aProperty)
		{
			super(aUndoStack, aProperty);
		}
	}
}
