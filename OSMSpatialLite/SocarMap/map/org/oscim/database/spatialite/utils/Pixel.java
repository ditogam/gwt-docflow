/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.database.spatialite.utils;

public class Pixel implements IGeometry {

	/**
	 * parses a String into a Pixel. The String should have the same format as
	 * the method toString() returns.
	 * 
	 * @param extent
	 * @return
	 */
	public static Pixel parseString(final String pixel) {
		return null;
	}
	/**
	 * Returns a point instance from a pixel instance
	 * 
	 * @param p
	 *            A Pixel instance
	 * @return A Point with the x-y coordinates of the Pixel
	 */
	public static Point toPoint(final Pixel p) {
		return new Point(p.x, p.y);
	}
	private int x;

	private int y;

	private boolean visible = true;

	/**
	 * The constructor
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 */
	public Pixel(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * 
	 * @return A new pixel with the abs values of the coordinates of this
	 *         instance
	 */
	public Pixel abs() {
		return new Pixel(Math.abs(this.x), Math.abs(this.y));
	}

	/**
	 * Adds the x and y values to the x and y coordinates of this instance
	 * 
	 * @param x
	 *            The x coordinate to add
	 * @param y
	 *            The y coordinate to add
	 * @return A new pixel
	 */
	public Pixel add(final int x, final int y) {

		return new Pixel(this.x + x, this.y + y);
	}

	/**
	 * Adds the x and y coordinates of this instance and the one specified
	 * 
	 * @param pixel
	 *            The pixel to add
	 * @return A new pixel
	 */
	public Pixel add(final Pixel pixel) {
		return new Pixel(this.x + pixel.getX(), this.y + pixel.getY());
	}

	/**
	 * 
	 * @return A new instance of this pixel
	 */
	@Override
	public Object clone() {
		return new Pixel(this.x, this.y);
	}

	/**
	 * Does nothing
	 */
	@Override
	public void destroy() {

	}

	/**
	 * Calculates the distance from this pixel to another
	 * 
	 * @param p
	 *            The pixel to calculate the distance
	 * @return The distance (-1 if the square root is negative)
	 */
	public long distance(final Pixel p) {
		long a1 = (x - p.getX()) * (x - p.getX());

		// if (DEBUG)
		// System.out.println("a1: " + a1);
		//
		long a2 = (y - p.getY()) * (y - p.getY());

		// if (DEBUG)
		// System.out.println("a2: " + a2);
		//
		long a3 = a1 + a2;

		// if (DEBUG)
		// System.out.println("a3: " + a3);
		//
		if (a3 < 0)
			return -1;

		long res = (long) (Math.sqrt(a3));

		// if (DEBUG)
		// System.out.println("res: " + res);
		//
		return res;
	}

	/**
	 * Compare this instance to another
	 * 
	 * @param pixel
	 *            The pixel to be compared
	 * @return True if the coordinates are the same
	 */
	public boolean equals(final Pixel pixel) {
		return (this.x == pixel.x && this.y == pixel.y);
	}

	/**
	 * 
	 * @return null
	 */
	@Override
	public Point[] getCoordinates() {
		return null;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * 
	 * @return True if is visible
	 */
	@Override
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Does nothing
	 * 
	 * @param coords
	 */
	@Override
	public void setCoordinates(final Point[] coords) {
	}

	/**
	 * Sets this instance to visible or not
	 * 
	 * @param visible
	 */
	@Override
	public void setVisible(final boolean visible) {
		this.visible = visible;

	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(final int x) {
		this.x = x;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(final int y) {
		this.y = y;
	}

	/**
	 * Substracts the x and y values from the x and y coordinates of this
	 * instance
	 * 
	 * @param x
	 *            The x coordinate to substract
	 * @param y
	 *            The y coordinate to substract
	 * @return A new pixel
	 */
	public Pixel sub(final int x, final int y) {

		return new Pixel(this.x - x, this.y - y);
	}

	/**
	 * Substract the x and y coordinates of the specified pixel from this
	 * instance
	 * 
	 * @param pixel
	 *            The pixel to substract
	 * @return A new pixel
	 */
	public Pixel sub(final Pixel pixel) {
		return new Pixel(this.x - pixel.getX(), this.y - pixel.getY());
	}

	/**
	 * Returns a point instance from this pixel instance
	 * 
	 * @return A Point with the x-y coordinates of this instance
	 */
	public Point toPoint() {
		return new Point(x, y);
	}

	public String toShortString(final int length) {
		String x = "";
		String y = "";
		x = Utilities.trimDecimals(String.valueOf(this.getX()), length);
		y = Utilities.trimDecimals(String.valueOf(this.getY()), length);
		return new StringBuffer().append("x= ").append(x).append(" y= ")
				.append(y).toString();
	}

	@Override
	public String toString() {
		return new StringBuffer().append(this.getX()).append(",")
				.append(this.getY()).toString();
	}
}