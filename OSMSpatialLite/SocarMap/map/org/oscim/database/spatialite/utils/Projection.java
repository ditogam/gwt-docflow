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

public class Projection {

	private String abbreviation = null;
	private String unit = "?";
	private double metersPerUnit = 1;

	/**
	 * Constructor
	 * 
	 * @param ab
	 *            abbreviation
	 */
	public Projection(String ab, String u, double meters_per_unit) {
		abbreviation = ab;
		unit = u;
		metersPerUnit = meters_per_unit;

	}

	/**
	 * @see es.prodevelop.gvsig.mobile.fmap.proj.IProjection#getAbrev()
	 */
	public String getAbrev() {
		return abbreviation;
	}

	/**
	 * @see es.prodevelop.gvsig.mobile.fmap.proj.IProjection#getMetersPerProjUnit()
	 */
	public double getMetersPerProjUnit() {
		return metersPerUnit;

		// if (abbreviation.compareToIgnoreCase(GeoUtils.CRS_CODE_WGS_84) == 0)
		// {
		// return 10000000.0 / 90.0;
		// } else {
		// return 1.0;
		// }
	}

	/**
	 * @see es.prodevelop.gvsig.mobile.fmap.proj.IProjection#getScale(double,
	 *      double, double, double)
	 */
	public double getScale(double minX, double maxX, double img_width,
			double dpi) {

		double inches = img_width / dpi;
		double meters = inches * 0.0254;
		return GeoUtils.getScale(this, maxX - minX, meters);
	}

	/**
	 * @see es.prodevelop.gvsig.mobile.fmap.proj.IProjection#getUnitsAbbrev()
	 */
	public String getUnitsAbbrev() {

		return unit;
		// if (abbreviation.length() == 9) return "ï¿½";
		// if (abbreviation.indexOf("EPSG:230") != -1) return "m";
		// if (abbreviation.indexOf("EPSG:326") != -1) return "m";
		// if (abbreviation.indexOf("EPSG:327") != -1) return "m";
		// return "u";
	}
}
