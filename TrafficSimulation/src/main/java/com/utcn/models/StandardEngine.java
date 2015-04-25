package com.utcn.models;

public class StandardEngine implements Engine {

    private final static double STOP = 0.0;
    private final static double ACCELERATION = 4.5;
    private final static double INTER = 9;
    private final static double MAXIMUM_SPEED = 13.5;
    private final static double DOUBLE_SPEED = 22.5;


    public double accelerate(Vehicle car) {
        if (car.getDistanceToObstacle() >= MAXIMUM_SPEED) {
            if (car.getSpeed() < MAXIMUM_SPEED) {
                car.setSpeed(car.getSpeed() + ACCELERATION);
                if (car.getSpeed() > MAXIMUM_SPEED) {
                    car.setSpeed(MAXIMUM_SPEED);
                }
            }
            if (car.getDistanceToObstacle() >= DOUBLE_SPEED) {
                updateParameters(car);
            } else if (car.getDistanceToObstacle() < DOUBLE_SPEED && car.getDistanceToObstacle() >= MAXIMUM_SPEED) {
                if (car.getSpeed() == MAXIMUM_SPEED || car.getSpeed() == INTER) {
                    updateAllParameters(car, INTER);
                } else {
                    updateParameters(car);
                }
            }
        } else if (car.getDistanceToObstacle() > ACCELERATION && car.getDistanceToObstacle() < MAXIMUM_SPEED) {
            updateAllParameters(car, ACCELERATION);
        } else if (car.getDistanceToObstacle() > STOP && car.getDistanceToObstacle() <= ACCELERATION) {
            updateAllParameters(car, STOP);
            car.setSpeed(STOP);
        }
        return car.getSpeed();
    }

    /**
     * @param car
     */
    private void updateParameters(Vehicle car) {
        car.setDistanceToObstacle(car.getDistanceToObstacle() - car.getSpeed());
        car.setCurrentDistance(car.getCurrentDistance() + car.getSpeed());
    }

    /**
     * @param car
     * @param variable
     */
    private void updateAllParameters(Vehicle car, double variable) {
        car.setSpeed(car.getDistanceToObstacle() - variable);
        car.setDistanceToObstacle(car.getDistanceToObstacle() - car.getSpeed());
        car.setCurrentDistance(car.getCurrentDistance() + car.getSpeed());
    }
}
