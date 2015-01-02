package com.utcn.utils;

import java.util.List;

public class TrafficSimulationUtil {

	// prevent this class from being instanced
	private TrafficSimulationUtil() {
	}

	/**
	 * Convert a List of Integer to a int[]
	 * 
	 * @param list
	 *            the list of integers
	 * @return the new int[]
	 */
	public static int[] convertList(List<Integer> list) {
		int[] x = new int[list.size()];

		for (int i = 0; i < list.size(); i++) {
			x[i] = list.get(i);
		}

		return x;
	}
}
