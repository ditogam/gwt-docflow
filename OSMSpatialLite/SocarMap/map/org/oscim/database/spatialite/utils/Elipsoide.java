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

public class Elipsoide {
	private static final Elipsoide WGS84 = new Elipsoide(6378137.0,
			1 / 298.257223563);
	private static final Elipsoide ED50 = new Elipsoide(6378388.0, 1 / 297.0);

	public static Elipsoide getED50() {
		return ED50;
	}
	public static Elipsoide getWGS84() {
		return WGS84;
	}
	/**
	 * Semimayor Axis
	 */
	private final double a;
	/**
	 * Flattening
	 */
	private final double f;
	/**
	 * Semiminor Axis
	 */
	private final double b;
	/**
	 * First Excentricity
	 */
	private final double pe;
	/**
	 * First Excentricity ^2
	 */
	private final double pe2;
	/**
	 * Second Excentricity
	 */
	private final double se;

	/**
	 * Second Excentricity ^2
	 */
	private final double se2;

	/**
	 * Radio de curvatura polar
	 */
	private final double c;

	/**
	 * Constructor
	 * 
	 * @param _a
	 *            Semimayor Axis elipsoide
	 * @param _f
	 *            Flattening elipsoide
	 */
	public Elipsoide(double _a, double _f) {
		this.a = _a;
		this.f = _f;

		b = a * (1 - f);
		pe = Math.sqrt(((Float11.pow(a, 2)) - (Float11.pow(b, 2)))
				/ ((Float11.pow(a, 2))));
		se = Math.sqrt(((Float11.pow(a, 2)) - (Float11.pow(b, 2)))
				/ ((Float11.pow(b, 2))));
		pe2 = Float11.pow(pe, 2);
		se2 = Float11.pow(se, 2);
		c = (Float11.pow(a, 2)) / b;
	}

	/**
	 * Get Semimayor Axis
	 */
	public double getA() {
		return a;
	}

	/**
	 * Get Semiminor Axis
	 */
	public double getB() {
		return b;
	}

	/**
	 * Get Radio de curvatura polar
	 */
	public double getC() {
		return c;
	}

	/**
	 * Get Flattening
	 */
	public double getF() {
		return f;
	}

	/**
	 * Get First Excentricity
	 */
	public double getPe() {
		return pe;
	}

	/**
	 * Get First Excentricity ^2
	 */
	public double getPe2() {
		return pe2;
	}

	/**
	 * Get Second Excentricity
	 */
	public double getSe() {
		return se;
	}

	/**
	 * Get Second Excentricity ^2
	 */
	public double getSe2() {
		return se2;
	}

	/**
	 * 
	 * @param lat
	 *            = latitude
	 * @return double[] = {rm, rn, rg}
	 */
	public double[] radios(double lat) {

		double rlat = lat * Math.PI / 180;
		double denomin = (1 - pe2 * (Float11.pow(Math.sin(rlat), 2)));
		/* radio meridiano */
		double rm = (a * (1 - pe2)) / (Float11.pow(denomin, 1.5));
		/* radio primer vertical */
		double rn = a / (Math.sqrt(denomin));
		/* radio gaussiano */
		double rg = Math.sqrt(rm * rn);

		double[] lRadios = { rm, rn, rg };
		return lRadios;

	}
}