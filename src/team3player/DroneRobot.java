package team3player;
import battlecode.common.*;
import org.mockito.internal.matchers.Null;

class DroneRobot extends RobotFramework {
    MapLocation hqLoc;
    Team myTeam;
    Team enemyTeam;
    Direction heading;
    int targetBot;
    MapLocation targetLoc;
    Direction targetDir;
    MapLocation water; //{};
    RobotInfo[] robots = new RobotInfo[]{};

    DroneRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        myTeam = rc.getTeam();
        enemyTeam = myTeam.opponent();
        heading = randomDirection();
    }

    public void myTurn()throws GameActionException {
        //logic to be run on every turn goes here
        waitforcooldown();

        while (!rc.isCurrentlyHoldingUnit()) {
            waitforcooldown();
            if (water == null) {
                findWater(rc.getLocation());
            }

            // See if there are any enemy robots within capturing range
            robots = rc.senseNearbyRobots(-1, enemyTeam);

            if (robots.length > 0) {
                // Pick up a first robot within range
                targetBot = robots[0].getID();
                targetLoc = robots[0].getLocation();
                while (!rc.canPickUpUnit(targetBot) && robots[0].getTeam() != null) {
                    targetLoc = robots[0].getLocation();
                    heading = moveNextTo(targetLoc);
                }
                if (rc.canPickUpUnit(targetBot)) {
                    rc.pickUpUnit(targetBot);
                    System.out.println("I picked up " + robots[0].getID() + "!");
                } else {
                    heading = moveNextTo(targetLoc);
                }
            } else {
                heading = moveNextTo(rc.adjacentLocation(heading));
            }
        }
        while (rc.isCurrentlyHoldingUnit()) {
            if ((rc.senseFlooding(rc.getLocation().add(Direction.CENTER)))) {
                if (rc.canDropUnit(Direction.CENTER)) {
                    rc.dropUnit(Direction.CENTER);
                    System.out.println("I dropped " + robots[0].getID() + "!");
                }
            } else if (water == null) {
                findWater(rc.getLocation());
            } else {
                heading = moveNextTo(water);
            }

        }
    }

    Direction moveNextTo(MapLocation target) throws GameActionException{
        //while I am not next to my target location
        boolean moved = tryMove(rc.getLocation().directionTo(target));
        /* while (!rc.getLocation().isAdjacentTo(target)) { */
        while (!moved) {
            //try to move towards it
            waitforcooldown();
            MapLocation curLoc = rc.getLocation();
            Direction dir = curLoc.directionTo(target);
            while (!moved) {
                if (!moved) {
                    heading = dir.rotateRight();
                    moved = tryMove(heading);
                }
                if (!moved) {
                    heading = dir.rotateRight().rotateRight().rotateRight();
                    moved = tryMove(heading);
                }
                if (!moved) {
                    heading = dir.rotateLeft();
                    moved = tryMove(heading);
                }
                if (!moved) {
                    heading = dir.rotateLeft().rotateLeft().rotateLeft();
                    moved = tryMove(heading);
                }
            }
        }
    return heading;
    }

    void findWater(MapLocation loc) throws GameActionException {
        for (Direction dir : Direction.values()) {
            MapLocation newLoc = loc.add(dir);
            if (rc.senseFlooding(newLoc)) {
                water = newLoc;
                return;
            }
//            if (rc.getLocation().isWithinDistanceSquared(newLoc, 24)) {
//                waitforcooldown();
//                findWater(newLoc);
//            }
        }
        return;
    }
}