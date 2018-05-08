/*
 * Created on Jun 2, 2005
 */
package zz.utils.properties;

import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import zz.utils.IFilter;
import zz.utils.list.ICollection;
import zz.utils.list.ICollectionListener;

/**
 * Various utility methods for dealing with properties
 * @author gpothier
 */
public class PropertyUtils
{
	/**
	 * Same as {@link #connect(IRWProperty, IRWProperty, boolean, boolean)} but with hard 
	 * listeners
	 */
	public static <T> Connector<T> connect (
			IRWProperty<T> aSourceProperty,
			IRWProperty<T> aTargetProperty,
			boolean aSymmetric)
	{
		return connect(aSourceProperty, aTargetProperty, aSymmetric, true);
	}
	
	/**
	 * Connects two properties so that when the value of one changes, the value of the other
	 * is changed as well.
	 * @param aSourceProperty The property whose value is monitored
	 * @param aTargetProperty The property whose value is updated
	 * @param aSymmetric if true, source and target properties have similar roles; otherwise
	 * the value of the property is only forwarded from source to target.
	 * @param aHard Whether to add connectors as hard listeners or not.
	 * @return The connector used as listener on the properties. This object
	 * should be kept referenced so as to avoid garbage collection in case of non-hard listeners;
	 * It can be used to disconnect and reconnect the properties.
	 */
	public static <T> Connector<T> connect (
			IRWProperty<T> aSourceProperty,
			IRWProperty<T> aTargetProperty,
			boolean aSymmetric,
			boolean aHard)
	{
		Connector<T> theConnector = new SimpleValueConnector<T>(aSourceProperty, aTargetProperty, aSymmetric, aHard);
		theConnector.connect();
		return theConnector;
	}

	public static <T> SetConnector<T> connectSets(
			ISetProperty<T> aSourceProperty,
			ISetProperty<T> aTargetProperty,
			boolean aSymmetric,
			boolean aHard)
	{
		SetConnector<T> theConnector = new SetConnector<T>(aSourceProperty, aTargetProperty, aSymmetric, aHard);
		theConnector.connect();
		return theConnector;
	}
	
	/**
	 * Same as {@link #connectSizes(IRWProperty, IRWProperty, boolean)} but with hard listeners
	 */
	public static Connector<Rectangle2D> connectSizes (
			IRWProperty<Rectangle2D> aProperty1,
			IRWProperty<Rectangle2D> aProperty2)
	{
		return connectSizes(aProperty1, aProperty2, true);
	}
	
	/**
	 * Connects the sizes of two rectangle properties.
	 * This is a symetric connection.
	 * @param aHard Whether to add connectors as hard listeners or not.
	 * @return The connector used as listener on the properties. This object
	 * should be kept referenced so as to avoid garbage collection in case of non-hard listeners;
	 * It can be used to disconnect and reconnect the properties.
	 */
	public static Connector<Rectangle2D> connectSizes (
			IRWProperty<Rectangle2D> aProperty1,
			IRWProperty<Rectangle2D> aProperty2,
			boolean aHard)
	{
		Connector<Rectangle2D> theConnector = new SimpleSizeConnector(aProperty1, aProperty2, true, aHard);
		theConnector.connect();
		return theConnector;
	}

	/**
	 * Sets the size of the given rectangle property 
	 */
	public static void setSize (
			IRWProperty<Rectangle2D> aTarget,
			double aW, 
			double aH)
	{
		Rectangle2D theTargetRectangle = aTarget.get();

		double theX = theTargetRectangle != null ? theTargetRectangle.getX() : 0;
		double theY = theTargetRectangle != null ? theTargetRectangle.getY() : 0;
		double theW = theTargetRectangle != null ? theTargetRectangle.getWidth() : 0;
		double theH = theTargetRectangle != null ? theTargetRectangle.getHeight() : 0;
		
		if (theW != aW || theH != aH)
		{
			aTarget.set(new Rectangle2D.Double(theX, theY, aW, aH));
		}
	}
	
	/**
	 * Forwards the size of a rectangle property from a source
	 * to a target, without modifying the target's
	 * position.
	 */
	public static void forwardSize (
			IProperty<Rectangle2D> aSource, 
			IRWProperty<Rectangle2D> aTarget)
	{
		Rectangle2D theSourceRectangle = aSource.get();
		Rectangle2D theTargetRectangle = aTarget.get();
		
		if (theSourceRectangle == null) return;
		
		double theNW = theSourceRectangle.getWidth();
		double theNH = theSourceRectangle.getHeight();
		setSize(aTarget, theNW, theNH);
	}

	/**
	 * Returns all the fields that correspond to public properties of the given object
	 */
	public static List<Field> getAvailableProperties(Object aObject)
	{
		return getAvailableProperties(aObject, new IFilter<Class>()
				{
					public boolean accept(Class aClass)
					{
						return aClass == IRWProperty.class;
					}
				});
	}
	
	/**
	 * Returns all the fields that correspond to public list properties of the given object
	 */
	public static List<Field> getAvailableProperties(Object aObject, IFilter<Class> aFilter)
	{
		List<Field> theResult = new ArrayList<Field>();
		Field[] theFields = aObject.getClass().getFields();
		for (Field theField : theFields)
		{
			if (! theField.getName().startsWith("p")) continue;
			Type theType = theField.getGenericType();
			if (theType instanceof ParameterizedType)
			{
				ParameterizedType theParameterizedType = (ParameterizedType) theType;
				Type theRawType = theParameterizedType.getRawType();
				if (! (theRawType instanceof Class)) continue;
				if (! aFilter.accept((Class) theRawType)) continue;
				
				theResult.add(theField);
			}
		}
		
		return theResult;
	}
	
	/**
	 * Returns the available properties in the given collection of objects.
	 */
	public static Set<Field> getAvailableProperties(Collection<?> aObjects)
	{
		Set<Field> theResult = new HashSet<Field>();
		for (Object o : aObjects) theResult.addAll(getAvailableProperties(o));
		return theResult;
	}
	
	/**
	 * Returns the properties corresponding to the given property field in all the given
	 * objects, if available.
	 */
	@SuppressWarnings("unchecked")
	public static List<IRWProperty> getProperties(Field aProperty, Collection<?> aObjects)
	{
		List<IRWProperty> theResult = new ArrayList<IRWProperty>();
		for (Object o : aObjects)
		{
			IRWProperty theProperty = null;
			
			try { theProperty = (IRWProperty) aProperty.get(o); }
			catch (IllegalArgumentException e) { continue; }
			catch (IllegalAccessException e) { throw new RuntimeException(e); }
			
			if (theProperty != null) theResult.add(theProperty);
		}
		return theResult;
	}
	
	/**
	 * Returns the properties corresponding to the given property field in all the given
	 * objects, if available.
	 */
	@SuppressWarnings("unchecked")
	public static IProperty getProperty(Field aProperty, Object aObject)
	{
		IProperty theProperty = null;
		
		try { theProperty = (IProperty) aProperty.get(aObject); }
		catch (IllegalAccessException e) { throw new RuntimeException(e); }
		
		return theProperty;
	}
	
	/**
	 * Returns the declared generic parameter of the given field.
	 * The type of the field must be a subtype of {@link IProperty}
	 */
	public static Class getValueClass(Field aField)
	{
		Type theType = aField.getGenericType();
		if (theType instanceof ParameterizedType)
		{
			ParameterizedType theParameterizedType = (ParameterizedType) theType;
			Type theRawType = theParameterizedType.getRawType();
			if ((theRawType instanceof Class) && (IProperty.class.isAssignableFrom((Class< ? >) theRawType)))
			{
				Type[] theTypeArguments = theParameterizedType.getActualTypeArguments();
				return (Class) theTypeArguments[0];
			}
		}
		throw new IllegalArgumentException("Not a property: "+aField);
	}

	
	/**
	 * A property listener that forwards property changes to a target property.
	 * @author gpothier
	 */
	public abstract static class Connector<T> 
	{
		private IProperty<T> itsSourceProperty;
		private IProperty<T> itsTargetProperty;
		
		public Connector(IProperty<T> aSourceProperty, IProperty<T> aTargetProperty)
		{
			itsSourceProperty = aSourceProperty;
			itsTargetProperty = aTargetProperty;
		}

		public IProperty<T> getSourceProperty()
		{
			return itsSourceProperty;
		}

		public IProperty<T> getTargetProperty()
		{
			return itsTargetProperty;
		}

		/**
		 * Forwards the current value of the source property to the target, and
		 * connects the properties: after this method is called
		 * property values are forwarded.
		 */
		public abstract void connect();
		
		/**
		 * Disconnects the properties of this connector.
		 * After this method is called property values are not forwarded anymore.
		 */
		public abstract void disconnect();
	}
	
	/**
	 * This class can be used as a superclass for easy connector implementations.
	 * It provides attributes for symetry and hardness of listners.
	 * If memory efficiency is required, other subclasses of {@link Connector}
	 * should be created that replace attributes by behaviors.
	 * @author gpothier
	 */
	public abstract static class SimpleConnector<T> extends Connector<T>
	implements IPropertyListener<T>
	{
		private boolean itsSymmetric;
		private boolean itsHard;
		
		public SimpleConnector(
				IRWProperty<T> aSourceProperty, 
				IRWProperty<T> aTargetProperty, 
				boolean aSymmetric, 
				boolean aHard)
		{
			super(aSourceProperty, aTargetProperty);
			
			itsSymmetric = aSymmetric;
			itsHard = aHard;
		}
		
		public IRWProperty<T> getSourceProperty()
		{
			return (IRWProperty<T>) super.getSourceProperty();
		}

		public IRWProperty<T> getTargetProperty()
		{
			return (IRWProperty<T>) super.getTargetProperty();
		}

		public void propertyChanged(IProperty<T> aProperty, T aOldValue, T aNewValue)
		{
			if (aProperty == getSourceProperty()) 
			{
				forward (getSourceProperty(), getTargetProperty());
			}
			else if (aProperty == getTargetProperty())
			{
				if (itsSymmetric) forward (getTargetProperty(), getSourceProperty());
			}
			else throw new RuntimeException("Unknown property: "+aProperty);
		}
		
		public void propertyValueChanged(IProperty<T> aProperty)
		{
		}

		protected abstract void forward (IRWProperty<T> aSource, IRWProperty<T> aTarget);
		
		public void connect()
		{
			forward(getSourceProperty(), getTargetProperty());
			getSourceProperty().addListener(this, itsHard);
			if (itsSymmetric) getTargetProperty().addListener(this, itsHard);
		}
		
		public void disconnect()
		{
			getSourceProperty().removeListener(this);
			if (itsSymmetric) getTargetProperty().removeListener(this);
		}
	}
	
	/**
	 * A property listener that forwards property changes to a target property.
	 * @author gpothier
	 */
	public static class SimpleValueConnector<T> extends SimpleConnector<T>
	{
		public SimpleValueConnector(IRWProperty<T> aSourceProperty, IRWProperty<T> aTargetProperty, boolean aSymmetric, boolean aHard)
		{
			super (aSourceProperty, aTargetProperty, aSymmetric, aHard);
		}

		@Override
		protected void forward(IRWProperty<T> aSource, IRWProperty<T> aTarget)
		{
			aTarget.set(aSource.get());
			
			// If the target has adapted the change, process that.
			if (aTarget.get() != aSource.get()) aSource.set(aTarget.get());
		}
	}
	
	/**
	 * This property listener forwards the listened property's size to a destination
	 * property
	 * @author gpothier
	 */
	public static class SimpleSizeConnector extends SimpleConnector<Rectangle2D>
	{
		public SimpleSizeConnector(IRWProperty<Rectangle2D> aSourceProperty, IRWProperty<Rectangle2D> aTargetProperty, boolean aSymmetric, boolean aHard)
		{
			super (aSourceProperty, aTargetProperty, aSymmetric, aHard);
		}

		@Override
		protected void forward(IRWProperty<Rectangle2D> aSource, IRWProperty<Rectangle2D> aTarget)
		{
			forwardSize(aSource, aTarget);
		}
	}

	public static class SetConnector<T> extends Connector<Set<T>>
	implements ICollectionListener<T>
	{
		private boolean itsSymmetric;
		private boolean itsHard;
		
		public SetConnector(
				ISetProperty<T> aSourceProperty, 
				ISetProperty<T> aTargetProperty, 
				boolean aSymmetric, 
				boolean aHard)
		{
			super(aSourceProperty, aTargetProperty);
			
			itsSymmetric = aSymmetric;
			itsHard = aHard;
		}

		public ISetProperty<T> getSourceProperty()
		{
			return (ISetProperty<T>) super.getSourceProperty();
		}

		public ISetProperty<T> getTargetProperty()
		{
			return (ISetProperty<T>) super.getTargetProperty();
		}


		public void elementAdded(ICollection<T> aCollection, T aElement)
		{
			if (aCollection == getSourceProperty()) 
			{
				getTargetProperty().add(aElement);
			}
			else if (aCollection == getTargetProperty())
			{
				if (itsSymmetric) getSourceProperty().add(aElement);
			}
			else throw new RuntimeException("Unknown property: "+aCollection);
		}

		public void elementRemoved(ICollection<T> aCollection, T aElement)
		{
			if (aCollection == getSourceProperty()) 
			{
				getTargetProperty().remove(aElement);
			}
			else if (aCollection == getTargetProperty())
			{
				if (itsSymmetric) getSourceProperty().remove(aElement);
			}
			else throw new RuntimeException("Unknown property: "+aCollection);
		}

		protected void forward (ISetProperty<T> aSource, ISetProperty<T> aTarget)
		{
			aTarget.clear();
			for (T theElement : aSource)
			{
				aTarget.add(theElement);
			}
		}
		
		public void connect()
		{
			forward(getSourceProperty(), getTargetProperty());
			
			if (itsHard) getSourceProperty().addHardListener(this);
			else getSourceProperty().addListener(this);
			
			if (itsSymmetric) 
			{
				if (itsHard) getTargetProperty().addHardListener(this);
				else getTargetProperty().addListener(this);
			}
		}
		
		public void disconnect()
		{
			getSourceProperty().removeListener(this);
			if (itsSymmetric) getTargetProperty().removeListener(this);
		}
		
	}
}
