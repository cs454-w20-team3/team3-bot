package team3player;
import battlecode.common.*;
public abstract class RobotFramework {
    public RobotController rc;
    static Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST
    };
    RobotFramework(RobotController rc_) {
        rc=rc_;
    }

    abstract public void myTurn() throws GameActionException;

    boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

    Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    boolean tryMove() throws GameActionException {
        for (Direction dir : directions)
            if (tryMove(dir))
                return true;
        return false;
    }
    void waitforcooldown() {
        while (!rc.isReady()) {
            Clock.yield();
        }
    }
    boolean tryToGoTo(MapLocation target, int failures) throws GameActionException {

        while (failures >= 0) {
            waitforcooldown();
            if (rc.canMove(rc.getLocation().directionTo(target))) {
                //move towards target
                rc.move(rc.getLocation().directionTo(target));
            } else if (rc.canMove(rc.getLocation().directionTo(target).rotateLeft())) {
                //maybe something is in the path
                rc.move(rc.getLocation().directionTo(target).rotateLeft());
            } else if (rc.canMove(rc.getLocation().directionTo(target).rotateRight())) {
                //the other way around maybe...
                rc.move(rc.getLocation().directionTo(target).rotateRight());
            }
            else {
                failures--;
            }
        }    
        if (rc.getLocation() == target) {
            return true;
        } else {
            return false;
        }
    }
<<<<<<< HEAD

    boolean canMoveSafe(Direction dir) throws GameActionException {
        if (rc.canMove(dir) && rc.senseFlooding(rc.getLocation().add(dir))) {
            return true;
        }
        return false;
    }
=======
>>>>>>> 107c7853f3a4f63cccd57109dd64a471072a12e8

    boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }
}