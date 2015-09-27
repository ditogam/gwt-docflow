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
package org.oscim.core;

/**
 * A tile represents a rectangular part of the world map. All tiles can be
 * identified by their X and Y number together with their zoom level. The actual
 * area that a tile covers on a map depends on the underlying map projection.
 */
public class Tile {

	/**
	 * Width and height of a map tile in pixel.
	 */
	public static int TILE_SIZE = 256;

	/**
	 * The X number of this tile.
	 */
	public final int tileX;

	/**
	 * The Y number of this tile.
	 */
	public final int tileY;

	/**
	 * The Zoom level of this tile.
	 */
	public final byte zoomLevel;

	/**
	 * the pixel X coordinate of the upper left corner of this tile.
	 */
	public final long pixelX;
	/**
	 * the pixel Y coordinate of the upper left corner of this tile.
	 */
	public final long pixelY;

	private int mHash = 0;

	/**
	 * @param tileX
	 *            the X number of the tile.
	 * @param tileY
	 *            the Y number of the tile.
	 * @param zoomLevel
	 *            the zoom level of the tile.
	 */
	public Tile(int tileX, int tileY, byte zoomLevel) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.pixelX = this.tileX * TILE_SIZE;
		this.pixelY = this.tileY * TILE_SIZE;
		this.zoomLevel = zoomLevel;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof Tile))
			return false;

		Tile o = (Tile) obj;

		if (o.tileX == this.tileX && o.tileY == this.tileY
				&& o.zoomLevel == this.zoomLevel)
			return true;

		return false;
	}

	@Override
	public int hashCode() {
		if (mHash == 0) {
			int result = 7;
			result = 31 * result + this.tileX;
			result = 31 * result + this.tileY;
			result = 31 * result + this.zoomLevel;
			mHash = result;
		}
		return mHash;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[X:");
		stringBuilder.append(this.tileX);
		stringBuilder.append(", Y:");
		stringBuilder.append(this.tileY);
		stringBuilder.append(", Z:");
		stringBuilder.append(this.zoomLevel);
		stringBuilder.append("]");
		return stringBuilder.toString();
	}
}
