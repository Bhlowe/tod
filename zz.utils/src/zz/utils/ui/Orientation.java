/*
 * Created on Feb 6, 2007
 */
package zz.utils.ui;

import java.awt.geom.AffineTransform;

public enum Orientation {
	HORIZONTAL
	{
		@Override
		public int getU(int aX, int aY)
		{
			return aX;
		}

		@Override
		public int getV(int aX, int aY)
		{
			return aY;
		}
	}, 
	VERTICAL
	{
		@Override
		public int getU(int aX, int aY)
		{
			return aY;
		}

		@Override
		public int getV(int aX, int aY)
		{
			return aX;
		}
	};
	
	public abstract int getU(int aX, int aY);
	public abstract int getV(int aX, int aY);
	
	public int getX(int aU, int aV)
	{
		return getU(aU, aV);
	}
	
	public int getY(int aU, int aV)
	{
		return getV(aU, aV);
	}
}
