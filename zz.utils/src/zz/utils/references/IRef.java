package zz.utils.references;

/**
 * Reference to an object. Implementations can be weak or hard references.
 */
public interface IRef<T>
{
	public T get();
}