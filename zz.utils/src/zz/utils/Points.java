/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 21 mars 2003
 * Time: 01:21:32
 */
package zz.utils;

import java.awt.geom.Point2D;

/**
 * Class for vector arithmetic.
 */
public class Points
{
	public static Point2D add (Point2D aPoint1, Point2D aPoint2)
	{
		return new Point2D.Double (aPoint1.getX() + aPoint2.getX(), aPoint1.getY() + aPoint2.getY());
	}

	/**
	 * Returns p2-p1;
	 */
	public static Point2D sub (Point2D aPoint1, Point2D aPoint2)
	{
		return new Point2D.Double (aPoint2.getX () - aPoint1.getX (), aPoint2.getY () - aPoint1.getY ());
	}
}
