package com.utcn.bl;

import com.utcn.models.Engine;
import com.utcn.models.Vehicle;

public class StandardEngine implements Engine {

    private final static double ACCELERATION = 4.5;
    private final static double MAXIMUM_SPEED = 13.5;
    private final static double DECELERATION = 1;

    /**
     * @param car
     * @return
     */
    public double accelerate(Vehicle car) {
        if (car.getDistanceToObstacle() >= MAXIMUM_SPEED) {
            if (car.getSpeed() < MAXIMUM_SPEED) {
                car.setSpeed(car.getSpeed() + ACCELERATION);
            }
            car.setDistanceToObstacle(car.getDistanceToObstacle()
                    - car.getSpeed());

            car.setCurrentDistance(car.getCurrentDistance() + car
                    .getSpeed());
        } else if (car.getDistanceToObstacle() > 1
                && car.getDistanceToObstacle() < MAXIMUM_SPEED) {
            double decelerate = car.getSpeed() / car.getDistanceToObstacle();
            car.setSpeed(car.getSpeed() - decelerate);
            car.setDistanceToObstacle(car.getDistanceToObstacle()
                    - DECELERATION);
            car.setCurrentDistance(car.getCurrentDistance() + 1);
        } else if (car.getDistanceToObstacle() <= 1) {
            // destination reached
            car.setSpeed(0);
            car.setDistanceToObstacle(0);

            if (car.getCurrentSegment().getSize() - car.getCurrentDistance() <= 1) {
                car.setCurrentDistance(car.getCurrentSegment().getSize());
            }
        }
        return car.getSpeed();
    }
}
