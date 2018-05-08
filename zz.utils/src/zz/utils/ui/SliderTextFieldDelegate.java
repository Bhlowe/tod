/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Jan 22, 2002
 * Time: 4:09:46 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

/**
 * Objects implementing this interface can be used by SliderTextField to convert between
 * slider values / text field value
 */
public interface SliderTextFieldDelegate
{
	/**
	 * Returns the text to use for the specified value
	 * (slider -> textfield)
	 */
	public String getText (int aValue);

	/**
	 * Returns the value to use for the specified text
	 * (textfield -> slider)
	 */
	public int getValue (String aText);
}
