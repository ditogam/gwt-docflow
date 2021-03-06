package org.proj4;

import java.util.LinkedHashMap;

/**
 * superclass of all the projections the main proj methods are held here
 */
public abstract class Projections {

	double degtorad = .0174532925199432958;
	double radtodeg = 57.29577951308232;

	String proj = null;
	/**
	 * Comment for <code>projParameters</code> it is a hashmap that contains all
	 * the parameters that define the projection. Usually that can be:<BR>
	 * +init<BR>
	 * +proj<BR>
	 * +zone<BR>
	 * +ellps<BR>
	 * +datum<BR>
	 * +units<BR>
	 * +no_defs<BR>
	 * +ellps<BR>
	 * +towgs84<BR>
	 * <BR>
	 * ELLIPSOID INFO:<BR>
	 * name<BR>
	 * a<BR>
	 * e<BR>
	 * es<BR>
	 * ra<BR>
	 * one_es<BR>
	 * rone_es<BR>
	 * lam0<BR>
	 * phi0<BR>
	 * x0<BR>
	 * y0<BR>
	 * k0<BR>
	 * to_meter<BR>
	 * fr_meter<BR>
	 */
	@SuppressWarnings("rawtypes")
	private LinkedHashMap projParameters = null;

	static {
		System.loadLibrary("proj");
	}

	/**
	 * transform latitude and longitude from degree to radiant format
	 * 
	 * @param la
	 * @param lo
	 */
	protected void degreeToRadiant(double[] la, double[] lo) {
		for (int i = 0; i < la.length; i++) {
			la[i] = la[i] * degtorad;
			lo[i] = lo[i] * degtorad;
		}
	}

	/**
	 * do the transform. The srcProjection is passed to the destination proj and
	 * the transformation takes place. Then the resulting transformed data are
	 * passed to the destProj, so that the destProj in case can take care of
	 * final transformation of data (ex. if the destProj is latlong, the values
	 * have to be set beck to degrees
	 * 
	 * @param srcProj
	 *            object holding the source projection
	 * @param dataTP
	 *            the data set
	 * @param point_count
	 * @param point_offset
	 */
	protected void doTheTransform(Projections srcProj, ProjectionData dataTP,
			long point_count, int point_offset) {
		transform(dataTP.x, dataTP.y, dataTP.z, srcProj.proj, proj,
				point_count, point_offset);
	}

	/**
	 * public method to call the native getEllispdInfo
	 * 
	 * @return the list of ellipsoid parameters
	 */
	public String getEllipseInfo() {
		return getEllipsInfo(proj);
	}

	/**
	 * get the ellipsoid parameters from the projection code
	 * 
	 * @param the
	 *            proj code or options
	 * @return the info String
	 */
	protected native String getEllipsInfo(String proj);

	/**
	 * public method to call the native getProjInfo
	 * 
	 * @return quoting the proj api:"Returns the PROJ.4 command string that
	 *         would produce this definition expanded as much as possible. For
	 *         instance, +init= calls and +datum= defintions would be expanded"
	 */
	public String getProjInfo() {
		return getProjInfo(proj);
	}

	/**
	 * get all the projection informations needed from the projection code
	 * (reproduces pj_get_def() of the proj api)
	 * 
	 * @param the
	 *            proj code or options
	 * @return the info String
	 */
	protected native String getProjInfo(String proj);

	/**
	 * return all the proj info into a Linked Hashmap
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LinkedHashMap mapProjInfo() {
		projParameters = new LinkedHashMap();
		String projinfo = getProjInfo();

		String[] infos = projinfo.split("\\+");
		for (int i = 0; i < infos.length - 1; i++) {
			String[] pairs = infos[i + 1].split("=");
			if (pairs.length == 1 && pairs[0].equals("no_defs")) {
				projParameters.put(pairs[0].trim(), "defined");
			} else if (pairs.length == 1 && !pairs[0].equals("no_defs")) {
				projParameters.put(pairs[0].trim(), "");
			} else {
				projParameters.put(pairs[0].trim(), pairs[1].trim());
			}
		}

		String ellipsinfo = getEllipseInfo();
		String[] ellipsoid = ellipsinfo.split(";");
		for (int i = 0; i < ellipsoid.length; i++) {
			String[] pairs2 = ellipsoid[i].split(":");
			projParameters.put(pairs2[0].trim(), pairs2[1].trim());
		}

		return projParameters;
	}

	/**
	 * if there is some operation to perform on the input data, this is the
	 * right moment (ex. latlong from degree to radiant) -> i.e. do whatever is
	 * needed
	 * 
	 * @param dataTP
	 */
	public abstract void prepareData(ProjectionData dataTP);

	/**
	 * this takes care that the reprojected data are in the correct format (ex.
	 * latlong has to be transformed back to radiant)
	 * 
	 * @param dataTP
	 *            the data set
	 */
	public abstract void prepareTransformedData(ProjectionData dataTP);

	/**
	 * print to standard output the proj info in a nice format
	 */
	public void printProjInfo() {
		String projinfo = getProjInfo();

		System.out.println();
		System.out
				.println("******************************************************");
		System.out.println("* PROJECTION INFO:");
		System.out.println("*");
		String[] infos = projinfo.split("\\+");
		for (int i = 0; i < infos.length - 1; i++) {
			System.out.println("*         +" + infos[i + 1].trim());
		}
		System.out.println("*");
		System.out.println("* ELLIPSOID INFO:");
		System.out.println("*");
		String ellipsinfo = getEllipseInfo();
		String[] ellipsoid = ellipsinfo.split(";");
		for (int i = 0; i < ellipsoid.length; i++) {
			System.out.println("*         " + ellipsoid[i].trim());
		}

		System.out
				.println("******************************************************");
		System.out.println();
	}

	/**
	 * transform latitude and longitude from radiant to degree format
	 * 
	 * @param la
	 * @param lo
	 */
	protected void radiantToDegree(double[] la, double[] lo) {
		for (int i = 0; i < la.length; i++) {
			la[i] = la[i] * radtodeg;
			lo[i] = lo[i] * radtodeg;
		}
	}

	/**
	 * native call to the reprojections routines of proj
	 * 
	 * @param firstCoord
	 *            array of x
	 * @param secondCoord
	 *            array of y
	 * @param values
	 *            array of z
	 * @param srcCodeString
	 *            source projection code or option
	 * @param destCodeString
	 *            destination projection code or option
	 * @param pointcount
	 * @param pointoffset
	 */
	protected native void transform(double[] firstCoord, double[] secondCoord,
			double[] values, String srcCodeString, String destCodeString,
			long pointcount, int pointoffset);
}