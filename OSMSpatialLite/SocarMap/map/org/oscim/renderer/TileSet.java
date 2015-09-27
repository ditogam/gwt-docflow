/*
 * Copyright 2012 Hannes Janetzek
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
package org.oscim.renderer;

import java.util.Comparator;

/**
 * use with TileManager.getActiveTiles(TileSet) to get the current tiles. tiles
 * are locked to not be modifed until getActiveTiles passes them back on a
 * second invocation or TODO: implement TileManager.releaseTiles(TileSet).
 */
public final class TileSet {
	public static class CoordComparator implements Comparator<MapTile> {

		@Override
		public int compare(MapTile lhs, MapTile rhs) {
			if (lhs.tileX == rhs.tileX) {
				if (lhs.tileY == rhs.tileY)
					return 0;

				if (lhs.tileY < rhs.tileY)
					return 1;

				return -1;
			}
			if (lhs.tileX < rhs.tileX)
				return 1;

			return -1;
		}
	}
	public int cnt = 0;

	public MapTile[] tiles;

	int serial;

	public static Comparator<MapTile> coordComparator = new CoordComparator();

	TileSet() {
	}

	TileSet(int numTiles) {
		tiles = new MapTile[numTiles];
	}

	public MapTile getTile(int x, int y) {
		for (int i = 0; i < cnt; i++)
			if (tiles[i].tileX == x && tiles[i].tileY == y)
				return tiles[i];

		return null;
	}
}
