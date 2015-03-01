package com.utcn.bl;

import java.util.Random;

import com.utcn.models.Vehicle;

public class VehicleGenerator {

	public static final int MIN = 2;
	public static final int MAX = 8;

	private int counter;

	public VehicleGenerator() {
		generateNewRandomTime();
	}

	public Vehicle generateNewVehicle() {
		Vehicle vehicle = new Vehicle();

		// setare
		// TODO
		vehicle.setEngine(new StandardEngine());

		return vehicle;
	}

	public void generateNewRandomTime() {
		Random rand = new Random();
		counter = rand.nextInt((MAX - MIN) + 1) + MIN;
	}

	public boolean isCounterZero() {
		if (counter == 0) {
			generateNewRandomTime();
			return true;
		}

		counter--;
		return false;
	}
}
