package zz.utils.properties;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

import zz.utils.ArrayStack;
import zz.utils.list.IList;
import zz.utils.list.IListListener;

/**
 * A property whose value depends on the value of other properties.
 * This class automatically gathers and listens to dependent properties.
 * @author gpothier
 *
 * @param <T>
 */
public abstract class ComputedProperty<T> extends AbstractProperty<T>
implements IRWProperty<T>
{
	// Volatile is needed for double-checked locking to work 
	private volatile static ThreadLocal<ArrayStack<Set<IProperty>>> DEPS_STACK;
	
	public static Set<IProperty> peekDeps() {
		if (DEPS_STACK == null) return null;
		ArrayStack<Set<IProperty>> theStack = DEPS_STACK.get();
		return theStack != null ? theStack.peek() : null;
	}
	
	private Set<IProperty> itsDependencies = new HashSet<IProperty>();
	
	private boolean itsDirty = true;
	private boolean itsFireScheduled = false;
	private T itsValue;
	
	private IPropertyListener itsListener = new MyListener();
	
	protected abstract T compute0();
	
	protected void compute() 
	{
		if (DEPS_STACK == null) 
		{
			synchronized(ComputedProperty.class) 
			{
				if (DEPS_STACK == null) DEPS_STACK = new ThreadLocal<ArrayStack<Set<IProperty>>>();
			}
		}
		for(IProperty<Object> p : itsDependencies) removeDepListener(p);
		itsDependencies.clear();
		ArrayStack<Set<IProperty>> theStack = DEPS_STACK.get();
		if (theStack == null) {
			theStack = new ArrayStack<Set<IProperty>>();
			DEPS_STACK.set(theStack);
		}
		theStack.push(itsDependencies);
		try
		{
			itsValue = compute0();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		itsDirty = false;
		theStack.pop();
		for(IProperty<Object> p : itsDependencies) addDepListener(p);
	}
	
	private void addDepListener(IProperty<Object> property) {
		if (property instanceof IListProperty)
		{
			IListProperty listProperty = (IListProperty) property;
			listProperty.addListener((IListListener) itsListener);
		}
		else
		{
			property.addListener(itsListener);
		}
	}
	
	private void removeDepListener(IProperty<Object> property) {
		if (property instanceof IListProperty)
		{
			IListProperty listProperty = (IListProperty) property;
			listProperty.removeListener((IListListener) itsListener);
		}
		else
		{
			property.removeListener(itsListener);
		}
	}
	
	private void scheduleFire() 
	{
		if (SwingUtilities.isEventDispatchThread()) {
			if (itsFireScheduled) return;
			itsFireScheduled = true;
			final T oldValue = itsValue;
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					if (itsDirty) compute();
					if (oldValue != itsValue) firePropertyChanged(oldValue, itsValue);
					itsFireScheduled = false;
				}
			});
		}
		else
		{
			T oldValue = itsValue;
			compute();
			if (oldValue != itsValue) firePropertyChanged(oldValue, itsValue);
		}
	}

	public T get()
	{
		addToDeps();
		if (itsDirty) compute();
		return itsValue;
	}
	
	@Override
	public T set(T aValue)
	{
		throw new RuntimeException("Cannot change value of computed property");
	}

	@Override
	public boolean canSet(T aValue)
	{
		return false;
	}
	
	@Override
	public String toString()
	{
		return getClass().getName() + "@" + System.identityHashCode(this);
	}
	
	/**
	 * Creates a computed property that represents the
	 * conjunction (boolean AND) of other properties.
	 */
	public static IProperty<Boolean> And(final Iterable<IProperty<Boolean>> properties) {
		return new ComputedProperty<Boolean>() {
			@Override
			protected Boolean compute0()
			{
				for(IProperty<Boolean> p : properties) if (! p.get()) return false;
				return true;
			}
		};
	}
	
	/**
	 * Creates a computed property that represents the
	 * conjunction (boolean AND) of other properties.
	 */
	public static IProperty<Boolean> And(IProperty<Boolean>... properties) 
	{
		return And(Arrays.asList(properties));
	}
	
	/**
	 * Creates a computed property that represents the
	 * disjunction (boolean OR) of other properties.
	 */
	public static IProperty<Boolean> Or(final Iterable<IProperty<Boolean>> properties) {
		return new ComputedProperty<Boolean>() {
			@Override
			protected Boolean compute0()
			{
				for(IProperty<Boolean> p : properties) if (p.get()) return true;
				return false;
			}
		};
	}
	
	/**
	 * Creates a computed property that represents the
	 * disjunction (boolean OR) of other properties.
	 */
	public static IProperty<Boolean> Or(IProperty<Boolean>... properties) 
	{
		return Or(Arrays.asList(properties));
	}

	/**
	 * Creates a computed property that represents the
	 * negation of other properties.
	 */
	public static IProperty<Boolean> Not(final IProperty<Boolean> property) 
	{
		return new ComputedProperty<Boolean>() {
			@Override
			protected Boolean compute0()
			{
				return ! property.get();
			}
		};
	}
	
	private class MyListener implements IPropertyListener<Object>, IListListener<Object> 
	{
		private void relay() 
		{
			synchronized(ComputedProperty.this) 
			{
				itsDirty = true;
				scheduleFire();
			}
		}
		
		public void propertyChanged(IProperty aProperty, Object aOldValue, Object aNewValue)
		{
			relay();
		}
		
		
		
		@Override
		public void elementAdded(IList<Object> aList, int aIndex, Object aElement)
		{
			relay();
		}



		@Override
		public void elementRemoved(IList<Object> aList, int aIndex, Object aElement)
		{
			relay();
		}

		public String toString() {
			return "Listener for computed property: "+ComputedProperty.this;
		}
		
	}
}
