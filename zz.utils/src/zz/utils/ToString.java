package zz.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to implement automatic toString methods
 * @author gpothier
 */
public class ToString
{
	private Map<Class, List<Field>> itsClassesMap =
		new HashMap<Class, List<Field>>();
	
	public String toString(Object aObject)
	{
		Class theClass = aObject.getClass();
		
		StringBuilder theBuilder = new StringBuilder(theClass.getSimpleName());
		
		theBuilder.append(" [");
		boolean theFirst = true;
		for (Field theField : getFields(theClass))
		{
			if (theFirst) theFirst = false;
			else theBuilder.append(", ");
			
			// format name
			String theName = theField.getName();
			if (theName.startsWith("its"))
			{
				theBuilder.append(Character.toLowerCase(theName.charAt(3)));
				theBuilder.append(theName, 4, theName.length());
			}
			else theBuilder.append(theName);
			
			theBuilder.append(": ");
			
			try
			{
				Object theValue = theField.get(aObject);
				theBuilder.append(theValue);
			}
			catch (IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		theBuilder.append("]");
		return theBuilder.toString();
	}
	
	private List<Field> getFields(Class aClass)
	{
		List<Field> theFields = itsClassesMap.get(aClass);
		
		if (theFields == null)
		{
			theFields = new ArrayList<Field>();
			while (aClass != null)
			{
				for(Field theField : aClass.getDeclaredFields()) 
				{
					String theName = theField.getName();
					if (theField.isSynthetic()) continue;
					if (theName.equals("serialVersionUID")) continue;
					if (Modifier.isStatic(theField.getModifiers())) continue;

					theFields.add(theField);
					theField.setAccessible(true);
				}
				aClass = aClass.getSuperclass();
			}
		}
		
		return theFields;
	}
	

}
