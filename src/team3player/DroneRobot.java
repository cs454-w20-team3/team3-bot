package team3player;
import battlecode.common.*;
import org.mockito.internal.matchers.Null;

class DroneRobot extends RobotFramework {
    Team myTeam;
    Team enemyTeam;
    Direction heading;
    int targetBot;
    Team targetTeam;
    MapLocation water;
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
           notHolding();
        }
        while (rc.isCurrentlyHoldingUnit()) {
            holding();
        }
    }

    void notHolding() throws GameActionException {
        waitforcooldown();
        if (true) {
            findWater(rc.getLocation());
        }

        // See if there are any enemy robots within capturing range
        robots = rc.senseNearbyRobots(-1);
        for (RobotInfo bot : robots) {
            targetBot = bot.getID();
            targetTeam = bot.getTeam();
            if (targetTeam != myTeam && rc.canPickUpUnit(targetBot)) {
                rc.pickUpUnit(targetBot);
                System.out.println("I picked up " + targetBot + "!");
                break;
            }
        }
        heading = moveNextTo(rc.adjacentLocation(heading));
    }

    void holding() throws GameActionException {
        waitforcooldown();
        for (Direction d : directions) {
            if (rc.canDropUnit(d) && rc.senseFlooding(rc.getLocation().add(d)) && targetTeam == enemyTeam) {
                rc.dropUnit(d);
                System.out.println("I dropped " + targetBot + "!");
            }
        }
        if (water == null) {
            heading = moveNextTo(rc.adjacentLocation(heading));
            findWater(rc.getLocation());
        } else {
            heading = moveNextTo(water);
        }
    }

    Direction moveNextTo(MapLocation target) throws GameActionException{
        waitforcooldown();
        MapLocation curLoc = rc.getLocation();
        Direction dir = curLoc.directionTo(target);
        boolean moved = tryMove(dir);
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
            if (!moved) {
                heading = randomDirection();
                moved = tryMove(heading);
            }
        }
    return heading;
    }

    void findWater(MapLocation loc) throws GameActionException {
        for (Direction dir : Direction.values()) {
            if (rc.canSenseLocation(loc.add(dir)) && rc.senseFlooding(loc.add(dir))) {
                water = loc.add(dir);
                return;
            }
        }
    }
}