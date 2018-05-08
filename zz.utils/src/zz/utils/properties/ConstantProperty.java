package zz.utils.properties;

public class ConstantProperty<T> implements IRWProperty<T>
{
	public final T itsValue;
	
	public ConstantProperty(T aValue)
	{
		itsValue = aValue;
	}

	public T get()
	{
		return itsValue;
	}

	@Override
	public T set(T aValue)
	{
		throw new RuntimeException("Cannot change value of constant property");
	}

	@Override
	public boolean canSet(T aValue)
	{
		return false;
	}

	public void addListener(IPropertyListener< ? super T> aListener)
	{
	}

	public void addHardListener(IPropertyListener< ? super T> aListener)
	{
	}

	public void addListener(IPropertyListener< ? super T> aListener, boolean aHard)
	{
	}

	public void removeListener(IPropertyListener< ? super T> aListener)
	{
	}
}
