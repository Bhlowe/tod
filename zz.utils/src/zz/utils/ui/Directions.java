/*
 * Created on Jun 1, 2004
 */
package zz.utils.ui;

/**
 * Enumeration of directions
 * @author gpothier
 */
public class Directions
{
	public static Directions NORTH = new Directions("north");
	public static Directions SOUTH = new Directions("south");
	public static Directions WEST = new Directions("west");
	public static Directions EAST = new Directions("east");
	
	public static Directions NW = new Directions("nw");
	public static Directions NE = new Directions("ne");
	public static Directions SW = new Directions("sw");
	public static Directions SE = new Directions("se");

	private String itsName;
	
	private Directions(String aName)
	{
		itsName = aName;
	}
}
