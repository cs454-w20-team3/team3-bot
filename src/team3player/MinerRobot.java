package team3player;
import battlecode.common.*;
class MinerRobot extends RobotFramework {
    MapLocation hqLoc;
    MapLocation refineLoc;
    int numOfDesignSchools;
    int numOfRefineries;
    int numOfFulfillments;

    MinerRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        //on robot creation/start up code goes here
        System.out.println("Miner:" + rc.getID() + " initialization");

        numOfDesignSchools = 0;
        numOfRefineries = 0;
        numOfFulfillments = 0;
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
        final int designCost = 150;
        final int refineryCost = 200;
        final int fulfillmentCost = 150;
        final double designCoeff = 0.8;
        final double refineryCoeff = 0.5;
        final double fulfillmentCoeff = 0.7;

        if( rc.getTeamSoup() >= designCost * designCoeff && numOfDesignSchools == 0 && rc.getID() % 4 == 0 ){
            //move to design school location and build
            //after building, go back to HQ.
            tryMoveToTarget(hqLoc.x + 2, hqLoc.y + 2);
            if(buildDesignSchool()){
                Direction dirToHQ = rc.getLocation().directionTo(hqLoc);
                if(!tryMoveSafe(dirToHQ))
                    tryMoveSafe(randomDirection());
            }
        } else {
            //Other robots sense soups and moves to that place and mine the soups.
            //One miner is building refinery and fulfillment center.
            MapLocation[] soupLocs = rc.senseNearbySoup();
            if (soupLocs.length != 0) {
                int soupLocIdx = (int) (soupLocs.length * Math.random());
                Direction dirToSoup = rc.getLocation().directionTo(soupLocs[soupLocIdx]);
                if(!tryMoveSafe(dirToSoup))
                    tryMoveSafe(randomDirection());
                if(rc.getID() % 4 == 1) {
                    if (rc.getTeamSoup() >= refineryCost * refineryCoeff && numOfRefineries == 0)
                        buildRefinery();
                    else if (rc.getTeamSoup() >= fulfillmentCost * fulfillmentCoeff && numOfFulfillments == 0)
                        buildFulfillmentCenter();
                }
                tryMine(Direction.CENTER);
            } else {
                tryMoveSafe(randomDirection());
            }
            //If robots carries soup limit, move to refinery or HQ to refine the soup.
            if (rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
                Direction dirToRefine = getDirToRefine();
                tryMoveSafe(dirToRefine);
                tryRefine(Direction.CENTER);
            }
        }
    }

    public Direction getDirToRefine() throws GameActionException {
        Direction dirToRefine;
        if(senseNearbyRefinery()) {
            dirToRefine = rc.getLocation().directionTo(refineLoc);
        } else {
            dirToRefine = rc.getLocation().directionTo(hqLoc);
        }
        return dirToRefine;
    }
    public boolean senseNearbyRefinery() throws GameActionException {
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

    public boolean tryMoveToTarget(int targetX, int targetY) throws GameActionException {
        MapLocation tLoc = new MapLocation(targetX, targetY);
        Direction dirToTarget = rc.getLocation().directionTo(tLoc);
        if(tryMoveSafe(dirToTarget))
            return true;
        else
            return false;
    }
    public boolean buildDesignSchool()throws GameActionException {
        for (Direction dir : directions) {
            if (tryBuild(RobotType.DESIGN_SCHOOL, dir)) {
                numOfDesignSchools++;
                return true;
            }
        }
        return false;
    }
    public void buildRefinery()throws GameActionException {
        for (Direction dir : directions) {
            if (tryBuild(RobotType.REFINERY, dir))
                numOfRefineries++;
        }
    }
    public void buildFulfillmentCenter()throws GameActionException {
        for (Direction dir : directions) {
            if (tryBuild(RobotType.FULFILLMENT_CENTER, dir))
                numOfFulfillments++;
        }
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