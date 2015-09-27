package com.socar.map;

import java.util.ArrayList;

public class test1 {
	public static void main(String[] args) {
		MapSelectionArea selectionArea = new MapSelectionArea(42.0223791613885,
				42.1019436051554, 42.3865161337745, 42.3404041913391);

		makeTiles(selectionArea, 8, 18, 0);
	}

	public static ArrayList<ZX1X2Y1Y2> makeTiles(
			MapSelectionArea selectionArea, final int startZoom,
			final int endZoom, int subregion_id) {
		ArrayList<ZX1X2Y1Y2> result = new ArrayList<ZX1X2Y1Y2>();
		int numberTiles = 0;

		for (int zoom = startZoom; zoom <= endZoom; zoom++) {
			int x1 = (int) MapUtils.getTileNumberX(zoom,
					selectionArea.getLon1());
			int x2 = (int) MapUtils.getTileNumberX(zoom,
					selectionArea.getLon2());
			int y1 = (int) MapUtils.getTileNumberY(zoom,
					selectionArea.getLat1());
			int y2 = (int) MapUtils.getTileNumberY(zoom,
					selectionArea.getLat2());
			numberTiles += (x2 - x1 + 1) * (y2 - y1 + 1);
		}
		System.out.println("NUMBER=" + numberTiles);

//		for (int zoom = startZoom; zoom <= endZoom; zoom++) {
//			int x1 = (int) MapUtils.getTileNumberX(zoom,
//					selectionArea.getLon1());
//			int x2 = (int) MapUtils.getTileNumberX(zoom,
//					selectionArea.getLon2());
//			int y1 = (int) MapUtils.getTileNumberY(zoom,
//					selectionArea.getLat1());
//			int y2 = (int) MapUtils.getTileNumberY(zoom,
//					selectionArea.getLat2());
//			int tmp = x1;
//			if (x1 > x2) {
//				tmp = x1;
//				x1 = x2;
//				x2 = tmp;
//			}
//			if (y1 > y2) {
//				tmp = y1;
//				y1 = y2;
//				y2 = tmp;
//			}
//			numberTiles += (x2 - x1 + 1) * (y2 - y1 + 1);
//			result.add(new ZX1X2Y1Y2(zoom, x1, y1, x2, y2, subregion_id));
//			// for (int x = x1; x <= x2; x++) {
//			// for (int y = y1; y <= y2; y++) {
//			// System.out.println(zoom+" "+x+" "+y);
//			// }
//			// }
//			// makeTiles(zxies, zoom, x1, x2, y1, y2);
//		}

		// System.out.println(numberTiles);
		return result;
	}

}
