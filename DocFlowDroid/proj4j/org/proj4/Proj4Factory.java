package org.proj4;

import java.util.LinkedHashMap;

public interface Proj4Factory {
	/**
	 * @return the destination projection info as a hashmap
	 */
	@SuppressWarnings("rawtypes")
	public LinkedHashMap getDestProjInfo();

	/**
	 * @return the projection info as a hashmap
	 */
	@SuppressWarnings("rawtypes")
	public LinkedHashMap getProjInfo();

	/**
	 * @return the source projection info as a hashmap
	 */
	@SuppressWarnings("rawtypes")
	public LinkedHashMap getSrcProjInfo();

	/**
	 * method to reproject a dataset from the source projection to the
	 * destination projection as defined in the constructor
	 * 
	 * @param dataTP
	 *            the data set to reproject
	 * @param point_count
	 * @param point_offset
	 */
	public void transform(ProjectionData dataTP, long point_count,
			int point_offset);

}