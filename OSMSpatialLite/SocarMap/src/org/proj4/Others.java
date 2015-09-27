package org.proj4;

/**
 * class representing a generic projection, i.e. with no particular
 * preprocessing needs.
 */
public class Others extends Projections {

	public Others(String src) {
		proj = src;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Projections#prepareData(DataToProject)
	 */
	@Override
	public void prepareData(ProjectionData dataTP) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.Projections#returnTransformedCoordinates()
	 */
	@Override
	public void prepareTransformedData(ProjectionData dataTP) {
	}

}