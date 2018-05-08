package zz.utils.ui.propertyeditors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be associated to a property to define a sorting order
 * for editing various properties.
 * @author gpothier
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyOrder {
	int value();
}
