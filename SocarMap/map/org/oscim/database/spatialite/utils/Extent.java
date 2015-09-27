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

public class Extent {

	/**
	 * parses a String into a Extent. The String should have the same format as
	 * the method toString() returns. Uses as separator the string ","
	 * 
	 * @param extent
	 * @return
	 */
	public static Extent parseString(final String extent) {
		return parseString(extent, ",");
	}

	/**
	 * parses a String into a Extent. The String should have the same format as
	 * the method toString() returns.
	 * 
	 * @param extent
	 * @param separator
	 *            The string separator between coordinates
	 * @return
	 */
	public static Extent parseString(final String extent, String separator) {
		// try {
		// s = new StringTokenizer(extent, separator);
		// if (s.length < 4)
		// return null;
		// return new Extent(Double.parseDouble(s.nextToken()),
		// Double.parseDouble(s.nextToken()), Double.parseDouble(s
		// .nextToken()), Double.parseDouble(s.nextToken()));
		// } catch (final Exception e) {
		// e.printStackTrace();
		// return null;
		// } finally {
		// s = null;
		// }
		return null;
	}

	private double minX;
	private double minY;
	private double maxX;

	private double maxY;

	private Point center = null;

	public Extent() {

	}

	/**
	 * Constructor. It also sets the center according to the parameters.
	 * 
	 * @param minX
	 *            The minimum x value
	 * @param minY
	 *            The minimum y value
	 * @param maxX
	 *            The maximum x value
	 * @param maxY
	 *            The maximum y value
	 */
	public Extent(final double minX, final double minY, final double maxX,
			final double maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;

		center = new Point(0, 0);

		this.calculateCenter();
	}

	/**
	 * Constructor. It also sets the center according to the parameters
	 * 
	 * @param leftBottom
	 *            A <code>Point</code> with the minimum x and y values
	 * @param rightTop
	 *            A <code>Point</code> with the maximum x and y values
	 */
	public Extent(final Point leftBottom, final Point rightTop) {
		this.minX = leftBottom.getX();
		this.minY = leftBottom.getY();
		this.maxX = rightTop.getX();
		this.maxY = rightTop.getY();

		center = new Point(0, 0);

		this.calculateCenter();
	}

	/**
	 * Calculates the area of the extent
	 * 
	 * @return The area
	 */
	public double area() {
		return this.getWidth() * this.getHeight();
	}

	private void calculateCenter() {
		this.center.setX((minX + maxX) / 2);
		this.center.setY((minY + maxY) / 2);
	}

	/**
	 * Clones this instance
	 * 
	 * @return a cloned instance of this Extent
	 */
	@Override
	public Object clone() {
		return new Extent(this.minX, this.minY, this.maxX, this.maxY);
	}

	/**
	 * Checks if the extent contains the x and y values
	 * 
	 * @param x
	 *            X value
	 * @param y
	 *            Y value
	 * @return true if contains
	 */
	public boolean contains(final double x, final double y) {
		return ((x >= this.minX) && (x <= this.maxX) && (y >= this.minY) && (y <= this.maxY));
	}

	/**
	 * Checks if the extent contains the x and y values
	 * 
	 * @param point
	 *            an array with x and y values
	 * @return true if contains
	 */
	public boolean contains(final double[] point) {
		if (point == null)
			return false;
		return ((point[0] >= this.minX) && (point[0] <= this.maxX)
				&& (point[1] >= this.minY) && (point[1] <= this.maxY));
	}

	/**
	 * Checks if the extent contains the other
	 * 
	 * @param extent
	 *            The extent to check
	 * @return true if contains
	 */
	public boolean contains(final Extent extent) {

		final boolean inLeft = (extent.minX >= this.minX)
				&& (extent.minX <= this.maxX);
		final boolean inTop = (extent.maxY >= this.minY)
				&& (extent.maxY <= this.maxY);
		final boolean inRight = (extent.maxX >= this.minX)
				&& (extent.maxX <= this.maxX);
		final boolean inBottom = (extent.minY >= this.minY)
				&& (extent.minY <= this.maxY);

		return (inTop && inLeft && inBottom && inRight);
	}

	/**
	 * Check if the extents contains the Point
	 * 
	 * @param point
	 *            The point to check
	 * @return true if contains
	 */
	public boolean contains(final Point point) {
		return this.contains(point.getX(), point.getY());
	}

	/**
	 * sets to NULL the center Point
	 */
	public void destroy() {
		this.center = null;
	}

	/**
	 * Compares this extent whith other
	 * 
	 * @param extent
	 *            The extent to compare
	 * @return true if both are equals
	 */
	public boolean equals(final Extent extent) {
		return (this.minX == extent.minX && this.maxX == extent.maxX
				&& this.minY == extent.minY && this.maxY == extent.maxY);
	}

	/**
	 * @return the center
	 */
	public Point getCenter() {
		this.calculateCenter();
		return center;
	}

	/**
	 * Calculates the height of the extent
	 * 
	 * @return The height of the extent
	 */
	public double getHeight() {
		return Math.abs(maxY - minY);
	}

	/**
	 * 
	 * @return An instance of a Point with the minimum x and y values
	 */
	public Point getLefBottomCoordinate() {
		return new Point(this.minX, this.minY);
	}

	/**
	 * @return the maxX
	 */
	public double getMaxX() {
		return maxX;
	}

	/**
	 * @return the maxY
	 */
	public double getMaxY() {
		return maxY;
	}

	/**
	 * @return the minX
	 */
	public double getMinX() {
		return minX;
	}

	/**
	 * @return the minY
	 */
	public double getMinY() {
		return minY;
	}

	/**
	 * 
	 * @return * @return An instance of a Point with the maximum x and y values
	 */
	public Point getRightTopCoordinate() {
		return new Point(this.maxX, this.maxY);
	}

	/**
	 * Calculates the width of the extent
	 * 
	 * @return The width of the extent
	 */
	public double getWidth() {
		return Math.abs(maxX - minX);
	}

	/**
	 * Checks if the extent intersects with other
	 * 
	 * @param extent
	 *            The extent to check
	 * @return true if intersects
	 */
	public boolean intersect(final Extent extent) {

		final boolean inBottom = (extent.minY == this.minY && extent.maxY == this.maxY) ? true
				: (((extent.minY > this.minY) && (extent.minY < this.maxY)) || ((this.minY > extent.minY) && (this.minY < extent.maxY)));
		final boolean inTop = (extent.minY == this.minY && extent.maxY == this.maxY) ? true
				: (((extent.maxY > this.minY) && (extent.maxY < this.maxY)) || ((this.maxY > extent.minY) && (this.maxY < extent.maxY)));
		final boolean inRight = (extent.maxX == this.maxX && extent.minX == this.minX) ? true
				: (((extent.maxX > this.minX) && (extent.maxX < this.maxX)) || ((this.maxX > extent.minX) && (this.maxX < extent.maxX)));
		final boolean inLeft = (extent.maxX == this.maxX && extent.minX == this.minX) ? true
				: (((extent.minX > this.minX) && (extent.minX < this.maxX)) || ((this.minX > extent.minX) && (this.minX < extent.maxX)));

		final boolean res = (this.contains(extent) || extent.contains(this) || ((inTop || inBottom) && (inLeft || inRight)));
		return res;
	}

	/**
	 * Check if the extent intersects and returns an extent that represent the
	 * area that intersects
	 * 
	 * @param extent
	 *            The extent to check
	 * @param ext
	 *            true or false it doesn't matter
	 * @return The intersection extent. NULL if the extents don't intersect
	 */
	public Extent intersect(final Extent extent, final boolean ext) {
		if (this.intersect(extent)) {
			return new Extent(Math.max(extent.getMinX(), this.getMinX()),
					Math.max(extent.getMinY(), this.getMinY()), Math.min(
							extent.getMaxX(), this.getMaxX()), Math.min(
							extent.getMaxY(), this.getMaxY()));
		}
		return null;
	}

	public boolean isValid() {
		boolean isValid = (this.minX <= this.maxX) && (this.minY <= this.maxY)
				&& area() > 0;
		return isValid;
	}

	/**
	 * @param center
	 *            the center to set
	 */
	public void setCenter(final Point center) {
		this.center = center;
	}

	/**
	 * Sets the minimum x and y values
	 * 
	 * @param leftBottom
	 *            A Point with the x and y values to set
	 */
	public void setLeftBottomCoordinate(final Point leftBottom) {
		this.minX = leftBottom.getX();
		this.minY = leftBottom.getY();
	}

	/**
	 * @param maxX
	 *            the maxX to set
	 */
	public void setMaxX(final double maxX) {
		this.maxX = maxX;
	}

	/**
	 * @param maxY
	 *            the maxY to set
	 */
	public void setMaxY(final double maxY) {
		this.maxY = maxY;
	}

	/**
	 * @param minX
	 *            the minX to set
	 */
	public void setMinX(final double minX) {
		this.minX = minX;
	}

	/**
	 * @param minY
	 *            the minY to set
	 */
	public void setMinY(final double minY) {
		this.minY = minY;
	}

	/**
	 * Sets the maximum x and y values
	 * 
	 * @param rightTop
	 *            A Point with the x and y values to set
	 */
	public void setRightTopCoordinate(final Point rightTop) {
		this.maxX = rightTop.getX();
		this.maxY = rightTop.getY();
	}

	/**
	 * This method is used to get a printable String with the x,y values of the
	 * extent
	 * 
	 * @return a String with the minimum and maximum x,y values of the extent in
	 *         this format: "minX= 0.0, minY= 0.0, maxX= 1.0, maxY= 1.0"
	 */
	public String toPrintString() {
		return new StringBuffer().append("minX= ").append(this.minX)
				.append(", minY= ").append(this.minY).append(", maxX= ")
				.append(this.maxX).append(", maxY= ").append(this.maxY)
				.toString();
	}

	/**
	 * This methos is used to build the URL of a tile request
	 * 
	 * @return a String with the minimum and maximum x,y values of the extent in
	 *         this format: "0.0, 0.0, 1.0, 1.0"
	 */
	@Override
	public String toString() {
		return toString(",");
	}

	/**
	 * This method is used to build the URL of a tile request
	 * 
	 * @param separator
	 *            The string separator between coordinates
	 * @return a String with the minimum and maximum x,y values of the extent in
	 *         this format: "0.0 separator 0.0 separator 1.0 separator 1.0"
	 */
	public String toString(String separator) {
		return new StringBuffer().append(this.minX).append(separator)
				.append(this.minY).append(separator).append(this.maxX)
				.append(separator).append(this.maxY).toString();
	}

	/**
	 * This methos is used to build the URL of a tile request
	 * 
	 * @return a StringBuffer with the minimum and maximum x,y values of the
	 *         extent in this format: "0.0, 0.0, 1.0, 1.0"
	 */
	public StringBuffer toStringBuffer() {
		StringBuffer s = new StringBuffer();
		s.append(this.minX).append(",").append(this.minY).append(",")
				.append(this.maxX).append(",").append(this.maxY);
		return s;
	}
}