package com.utcn.utils;

import java.util.List;
import java.util.Random;

public class TrafficSimulationUtil {

	// prevent this class from being instanced
	private TrafficSimulationUtil() {
	}

	/**
	 * Convert a List of Integer to a new int[].
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

	/**
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randInt(int min, int max) {
		Random rand = new Random();

		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	/**
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static int distanceBetweenPoints(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

}
