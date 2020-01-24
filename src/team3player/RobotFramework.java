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

    boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }

    boolean tryMine(Direction dir) throws GameActionException {
        System.out.println("Miner ready? " + rc.isReady());
        System.out.println("Miner can mine soup? " + rc.canMineSoup(dir));
        System.out.println("Reached soup limit? " + rc.getSoupCarrying() + ":" + rc.getType().soupLimit);
        System.out.println("Remain cooldown turns? " + rc.getCooldownTurns());
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }
}