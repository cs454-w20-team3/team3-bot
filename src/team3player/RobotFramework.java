package team3player;
import battlecode.common.*;

import java.util.Map;

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
    final int teamCode = 333;
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

    boolean canMoveSafe(Direction dir) throws GameActionException {
        if (rc.canMove(dir) && rc.senseFlooding(rc.getLocation().add(dir)) == false) {
            return true;
        }
        return false;
    }

    boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }

    boolean tryMoveSafe(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && canMoveSafe(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }
    void sendHQLoc (MapLocation loc) throws GameActionException {
        int[] message = new int[7];
        message[0] = teamCode;
        message[1] = 0;     //HQ location info
        message[2] = loc.x;
        message[3] = loc.y;
        if(rc.canSubmitTransaction(message, 3)){
            rc.submitTransaction(message, 3);
        }
    }

    MapLocation getHQLocFromBlockchain () throws GameActionException {
        for (int i=1; i < rc.getRoundNum(); i++) {
            for ( Transaction tx : rc.getBlock(i)) {
                int[] message = tx.getMessage();
                if ( message[0] == teamCode && message[1] == 0)
                    return new MapLocation(message[2], message[3]);
            }
        }
        return null;
    }
    int[] getSpam () throws GameActionException {
        for (int i=1; i < rc.getRoundNum(); i++) {
            for ( Transaction tx : rc.getBlock(i)) {
                int[] message = tx.getMessage();
                if(message != null)
                    return message;
            }
        }
        return null;
    }
    void sendSpam () throws GameActionException {
        int[] message = new int[7];
        message = getSpam();
        if(rc.canSubmitTransaction(message, 10)){
            rc.submitTransaction(message, 10);
        }
    }

}