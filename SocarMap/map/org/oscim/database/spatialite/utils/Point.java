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

public class Point implements IGeometry {

	/**
	 * parses a String into a Point. The String should have the same format as
	 * the method toString() returns.
	 * 
	 * @param extent
	 * @return
	 */
	public static Point parseString(final String point) {
		String[] st;
		try {
			st = point.split(",");
			if (st.length < 2) {
				return null;
			}
			return new Point(Double.parseDouble(st[0]),
					Double.parseDouble(st[1]));
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			st = null;
		}
	}

	private double x;
	private double y;

	private boolean visible = true;

	public Point() {
	}

	/**
	 * The constructor
	 * 
	 * @param x
	 *            X Coordinate
	 * @param y
	 *            Y coordinate
	 */
	public Point(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * The constructor
	 * 
	 * @param coords
	 *            An array with the x and y coordinate
	 */
	public Point(final double[] coords) {
		this.x = coords[0];
		this.y = coords[1];
	}

	/**
	 * 
	 * @return A new point with the abs values of the coordinates of this
	 *         instance
	 */
	public Point abs() {
		return new Point(Math.abs(this.x), Math.abs(this.y));
	}

	/**
	 * Adds the x and y values to the x and y coordinates of this instance
	 * 
	 * @param x
	 *            The x coordinate to add
	 * @param y
	 *            The y coordinate to add
	 * @return A new Point
	 */
	public Point add(final double x, final double y) {

		return new Point(this.x + x, this.y + y);
	}

	/**
	 * Adds the x and y coordinates of this instance and the one specified
	 * 
	 * @param point
	 *            The Point to add
	 * @return A new Point
	 */
	public Point add(final Point point) {
		return new Point(this.x + point.getX(), this.y + point.getY());
	}

	/**
	 * 
	 * @return A new instance of this Point
	 */
	@Override
	public Object clone() {
		return new Point(this.x, this.y);
	}

	/**
	 * Does nothing
	 */
	@Override
	public void destroy() {
	}

	/**
	 * Calculates the distance from this Point to the coordinates
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return The distance
	 */
	public double distance(final double x, final double y) {
		return this.distance(new Point(x, y));
	}

	/**
	 * Calculates the distance from this Point to the coordinates of the array
	 * 
	 * @param xy
	 *            X and y coordinates to calculate the distance
	 * @return The distance
	 */
	public double distance(final double[] xy) {
		return this.distance(new Point(xy[0], xy[1]));
	}

	/**
	 * Calculates the distance from this Point to another
	 * 
	 * @param p
	 *            The Point to calculate the distance
	 * @return The distance
	 */
	public double distance(final Point p) {
		double a1 = (x - p.getX()) * (x - p.getX());

		// if (DEBUG)
		// System.out.println("a1: " + a1);
		//
		double a2 = (y - p.getY()) * (y - p.getY());

		// if (DEBUG)
		// System.out.println("a2: " + a2);
		//
		double a3 = a1 + a2;

		// if (DEBUG)
		// System.out.println("a3: " + a3);
		//
		if (a3 < 0)
			return -1;

		double res = (Math.sqrt(a3));

		// if (DEBUG)
		// System.out.println("res: " + res);
		//
		return res;
	}

	/**
	 * Compares the coordinates of two Points
	 * 
	 * @param point
	 *            The point to be compared
	 * @return True if the coordinates are the same
	 */
	public boolean equals(final Point point) {
		return (this.x == point.x && this.y == point.y);
	}

	/**
	 * 
	 * @return An array with one Point with the coordinates of this instance
	 */
	@Override
	public Point[] getCoordinates() {
		return new Point[] { this };
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	public boolean isValid() {
		return this.getX() != 0 && this.getY() != 0;
	}

	/**
	 * True if is visible
	 * 
	 * @return
	 */
	@Override
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets the xy coordinates of this instance
	 * 
	 * @param coords
	 *            An array with the coordinates
	 */
	public void setCoordinates(final double[] coords) {
		try {
			this.x = coords[0];
			this.y = coords[1];
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the x y coordinates of this instance
	 * 
	 * @param coords
	 *            An array with one Point containing the coordinates
	 */
	@Override
	public void setCoordinates(final Point[] coords) {
		try {
			if (coords != null) {
				this.x = coords[0].x;
				this.y = coords[0].y;
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets to visible or not
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
	public void setX(final double x) {
		this.x = x;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(final double y) {
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
	 * @return A new Point
	 */
	public Point sub(final double x, final double y) {

		return new Point(this.x - x, this.y - y);
	}

	/**
	 * Substract the x and y coordinates of the specified pixel from this
	 * instance
	 * 
	 * @param point
	 *            The point to substract
	 * @return A new Point
	 */
	public Point sub(final Point point) {
		return new Point(this.x - point.getX(), this.y - point.getY());
	}

	public String toShortString(int length) {
		String x = "";
		String y = "";
		x = Utilities.trimDecimals(String.valueOf(this.getX()), length);
		y = Utilities.trimDecimals(String.valueOf(this.getY()), length);
		return new StringBuffer().append(x).append(",").append(y).toString();
	}

	@Override
	public String toString() {
		return new StringBuffer().append(this.getX()).append(",")
				.append(this.getY()).toString();
	}
}