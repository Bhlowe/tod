/*
 * Created on Jan 20, 2007
 */
package zz.utils.ui;

public enum VerticalAlignment {
	TOP()
	{
		@Override
		public double getOffset(double aItemSpace, double aAvailableSpace)
		{
			return 0;
		}
	},
	CENTER()
	{
		@Override
		public double getOffset(double aItemSpace, double aAvailableSpace)
		{
			return (aAvailableSpace - aItemSpace)/2.0;
		}
	},
	BOTTOM()
	{
		@Override
		public double getOffset(double aItemSpace, double aAvailableSpace)
		{
			return aAvailableSpace - aItemSpace;
		}
	};
	
	public abstract double getOffset(double aItemSpace, double aAvailableSpace);
	
	public float getOffset(float aItemSpace, float aAvailableSpace)
	{
		return (float) getOffset((double) aItemSpace, (double) aAvailableSpace);
	}
	
	public int getOffset(int aItemSpace, int aAvailableSpace)
	{
		return (int) getOffset(aItemSpace, aAvailableSpace);
	}
}