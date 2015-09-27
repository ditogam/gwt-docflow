package org.proj4;

public class ProjectionData {

	// the variables are kept public, since they are transformed passing through
	// two classes
	public double[] x = null;
	public double[] y = null;
	public double[] z = null;
	public int rows = 0;

	/**
	 * object to hold the data to be transformed. This will be passed from the
	 * starting projection object to the destinantion projection passing through
	 * the transformation.
	 */
	public ProjectionData(double[][] _coord, double[] _values) {
		rows = _coord.length;
		x = new double[rows];
		y = new double[rows];

		for (int i = 0; i < rows; i++) {
			x[i] = _coord[i][0];
			y[i] = _coord[i][1];
		}
		z = _values;
	}
}