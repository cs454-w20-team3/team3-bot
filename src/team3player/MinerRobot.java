package team3player;
import battlecode.common.*;


class MinerRobot extends RobotFramework {
    MapLocation hqLoc;
    int numOfDesignSchools;
    int numOfRefineries;
    MinerType myType;
    int numOfFulfillments;

    MinerRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        //on robot creation/start up code goes here
        // System.out.println("Miner:" + rc.getID() + " initialization");
        myType = determineMyType(90, 20, 700);
        numOfDesignSchools = 0;
        numOfRefineries = 0;
        numOfFulfillments = 0;
        for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if (bot.getType() == RobotType.HQ) {
                hqLoc = bot.getLocation();
                // System.out.println("Found HQ Location: " + hqLoc.x + ":" + hqLoc.y);
            }
        }
    }
    void gatherer() throws GameActionException {
        MapLocation[] soupLocs = rc.senseNearbySoup();
        if (soupLocs.length != 0) {
            int soupLocIdx = (int) (soupLocs.length * Math.random());
            Direction dirToSoup = rc.getLocation().directionTo(soupLocs[soupLocIdx]);
            tryMoveSafe(dirToSoup);
        } else {
            boolean found = false;
            while (!found) {
                waitforcooldown();
                found = lookForSoup();
            }
        }
        getSoup();
    }
    boolean lookForSoup() throws GameActionException {
        tryMoveSafe(randomDirection());
        MapLocation[] soupLocs = rc.senseNearbySoup();
        if (soupLocs.length != 0) {
            int soupLocIdx = (int) (soupLocs.length * Math.random());
            Direction dirToSoup = rc.getLocation().directionTo(soupLocs[soupLocIdx]);
            tryMoveSafe(dirToSoup);
            return true;
        }
        else
            return false;
    }
    void getSoup() throws GameActionException {
        final int refCost = 200;
        while (rc.getSoupCarrying() != RobotType.MINER.soupLimit) {
            waitforcooldown();
            if ( rc.getTeamSoup() >= refCost ) {
                if( !checkForRefinery())
                    buildRefinery();
            }
            tryMine(Direction.CENTER);
        }
        processSoup();
    }

    boolean checkForRefinery() throws GameActionException {
        for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if (bot.getType() == RobotType.REFINERY) {
                return true;
            }
        }
        return false;
    }

    void buildRefinery() throws GameActionException {
        for (Direction dir : directions) {
            if (tryBuild(RobotType.REFINERY, dir)) {
                break;
            }
        }
    }

    void processSoup() throws GameActionException {
        MapLocation refineLoc = null;
        for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if (bot.getType() == RobotType.REFINERY) {
                refineLoc = bot.getLocation();
                break;
            }
        }
        if(refineLoc != null) {
            goToRefinery(refineLoc);
        } else {
            goToHQ();
        }
    }

    void goToHQ() throws GameActionException {
        Direction dirToHq = rc.getLocation().directionTo(hqLoc);
        tryMoveSafe(dirToHq);
        tryRefine(Direction.CENTER);
        gatherer();
    }

    void goToRefinery(MapLocation refineLoc) throws GameActionException {
        Direction dirToRefine = rc.getLocation().directionTo(refineLoc);
        tryMoveSafe(dirToRefine);
        tryRefine(Direction.CENTER);
        gatherer();
    }
    void builder() {

    }

    void moveToGoodDSArea() {

    }

    void buildDS() {

    }

    void buildFC() {

    }

    public void myTurn() throws GameActionException {
        if (myType == MinerType.GATHERER) {
            System.out.println("I am gatherer");
            gatherer();
        } else {
            System.out.println("I am builder");
            builder();
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
    enum MinerType {
        GATHERER, BUILDER;
    }
    MinerType determineMyType(int builderPercentageStart, int builderPercentageEnd, int lastRound){
        int currentRound = rc.getRoundNum();
        int id = rc.getID();
        //linear change from start to end
        double slope = Double.valueOf(builderPercentageEnd - builderPercentageStart) / Double.valueOf(lastRound - 1);
        //This is the slope of a line otherwise known as m in y=mx+b
        double y = slope * currentRound + builderPercentageStart;
        //y is the percentage of the current round to make a builder
        if (currentRound > lastRound) {
            y = builderPercentageEnd;
        }
        if (id % 100 < y) {
            return MinerType.BUILDER;
        } else {
            return MinerType.GATHERER;
        }
    }
    RobotType buildingToMake() {
        RobotType possible[] = {RobotType.VAPORATOR, RobotType.FULFILLMENT_CENTER, RobotType.DESIGN_SCHOOL};
        return possible[rc.getID() & possible.length];
    }
}