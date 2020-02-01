package team3player;
import battlecode.common.*;
class MinerRobot extends RobotFramework {
    MapLocation hqLoc;
    MapLocation refineLoc;
    int numOfDesignSchools;
    int numOfRefineries;

    MinerRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        //on robot creation/start up code goes here
        System.out.println("Miner:" + rc.getID() + " initialization");

        numOfDesignSchools = 0;
        numOfRefineries = 0;
        for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if (bot.getType() == RobotType.HQ) {
                hqLoc = bot.getLocation();
                System.out.println("Found HQ Location: " + hqLoc.x + ":" + hqLoc.y);
            }
        }
    }

    public void gatherMode() {

    }

    public void buildMode() {

    }

    public void myTurn() throws GameActionException {
        if( numOfDesignSchools == 0 && rc.getID() % 4 == 0 ){
            //move to design school location and build
            //after building, go back to HQ.
            System.out.println("Miner tries to build the design school");
            if(tryMoveBuildTarget(RobotType.DESIGN_SCHOOL,hqLoc.x + 3, hqLoc.y + 3))
                numOfDesignSchools++;
        } else if( numOfRefineries == 0 && rc.getID() % 4 == 1 ){
            //move to refinery location and build
            //after building, go back to HQ.
            System.out.println("Miner tries to build the refinery");
            if(tryMoveBuildTarget(RobotType.REFINERY,hqLoc.x + 3, hqLoc.y - 3))
                numOfRefineries++;
        } else {
            //Other robots sense soups and moves to that place and mine the soups.
            MapLocation[] soupLocs = rc.senseNearbySoup();
            if (soupLocs.length != 0) {
                System.out.println("Miner found the soup location");
                int soupLocIdx = (int) (soupLocs.length * Math.random());
                Direction dirToSoup = rc.getLocation().directionTo(soupLocs[soupLocIdx]);
                tryMove(dirToSoup);
                if (tryMine(Direction.CENTER))
                    System.out.println("I mined soup! " + rc.getSoupCarrying());
            } else {
                System.out.println("Miner couldn't find the soup location");
                tryMove(randomDirection());
            }
            //If robots carries soup limit, move to refinery or HQ to refine the soup.
            if (rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
                Direction dirToRefine = null;
                if(senseNearbyRefinery()) {
                    System.out.println("Go Refinery to refine");
                    dirToRefine = rc.getLocation().directionTo(refineLoc);
                } else {
                    System.out.println("Go HQ to refine");
                    dirToRefine = rc.getLocation().directionTo(hqLoc);
                }
                if (tryMove(dirToRefine))
                    System.out.println("Move HQ or Refinery");
                if (tryRefine(Direction.CENTER))
                    System.out.println("I refined soup! " + rc.getTeamSoup());
            }
        }
    }
    boolean senseNearbyRefinery() throws GameActionException {
        //Return true if the robot senses refinery nearby, otherwise return false.
        for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if (bot.getType() == RobotType.REFINERY) {
                refineLoc = bot.getLocation();
                System.out.println("Found Refinery Location: " + refineLoc.x + ":" + refineLoc.y);
                return true;
            }
        }
        return false;
    }
    boolean tryMoveBuildTarget(RobotType targetType, int targetX, int targetY) throws GameActionException {
        //The robot moves to target place and try to build targetType.
        //If the robot succeed to build, moves back to HQ or random direction.
        MapLocation tLoc = new MapLocation(targetX, targetY);
        Direction dirToTarget = rc.getLocation().directionTo(tLoc);
        tryMove(dirToTarget);
        //try to build target
        for (Direction dir : directions) {
            if (tryBuild(targetType, dir)) {
                System.out.println("Miner build" + targetType);
                Direction dirToHQ = rc.getLocation().directionTo(hqLoc);
                if(!tryMove(dirToHQ))
                    tryMove(randomDirection());
                return true;
            }
        }
        return false;
    }
    boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }
    boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }

}