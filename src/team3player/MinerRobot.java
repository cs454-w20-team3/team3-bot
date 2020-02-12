package team3player;
import battlecode.common.*;
class MinerRobot extends RobotFramework {
    int failedMoves =0;
    MapLocation hqLoc;
    MapLocation refineLoc;
    int numOfDesignSchools;
    int numOfRefineries;
    MinerType myType;
    int numOfFulfillments;
    //builder mode memory



    //gather mode memory

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

    void gatherer() {

    }
    void lookForSoup() {

    }
    void getSoup() {

    }

    void checkForRefinery() {

    }

    void buildRefinery() {

    }

    void processSoup() {

    }

    void goToHQ() {

    }

    void goToRefinery() {

    }
    void builder()throws GameActionException {
        //if there is no DS
        if (numOfDesignSchools == 0) {
            moveToGoodDSArea();
        }
        //if there is no FC
        if (numOfFulfillments == 0) {
            buildFC();
        }
        //otherwise become a gatherer
        gatherer();
    }

    void moveToGoodDSArea()throws GameActionException {
        while (tooFarFromHQ()) {
            waitforcooldown();
            Direction hqdir = rc.getLocation().directionTo(hqLoc);
            boolean moved = tryMove(hqdir);
            if (!moved) {
                moved = tryMove(hqdir.rotateRight());
            }
            if (!moved) {
                moved = tryMove(hqdir.rotateRight().rotateRight());
            }
            if (!moved) {
                moved = tryMove(hqdir.rotateLeft());
            }
            if (!moved) {
                moved = tryMove(hqdir.rotateLeft().rotateLeft());
            }
        }
        while (tooCloseToHQ()) {
            waitforcooldown();
            Direction away = rc.getLocation().directionTo(hqLoc).opposite();
            boolean moved = tryMove(away);
            if (!moved) {
                moved = tryMove(away.rotateRight());
            }
            if (!moved) {
                moved = tryMove(away.rotateLeft());
            }
        }
        buildDS();
    }

    boolean tooFarFromHQ() {
        final int maxdistanceHQ = 20;
        if (rc.getLocation().distanceSquaredTo(hqLoc) > maxdistanceHQ) {
            return true;
        } else {
            return false;
        }
    }

    boolean tooCloseToHQ() {
        if (rc.getLocation().isAdjacentTo(hqLoc)) {
            return true;
        } else {
            return false;
        }
    }

    boolean DSGoodspot(MapLocation target) {
        //needs to be less than 13 and more than 2 to hq
        final int min =2;
        final int max = 13;
        int distancetoHQ = target.distanceSquaredTo(hqLoc);
        if (min < distancetoHQ && distancetoHQ < max) {
            return true;
        } else {
            return false;
        }
    }

    void buildDS()throws GameActionException {
        Direction dir = rc.getLocation().directionTo(hqLoc);
        while (numOfDesignSchools == 0) {
            waitforcooldown();
            if (DSGoodspot(rc.getLocation().add(dir))) {
                if (tryBuild(RobotType.DESIGN_SCHOOL, dir)) {
                    numOfDesignSchools++;
                }
            }
            dir = dir.rotateLeft();
        }
        return; // next turn
    }

    void buildFC()throws GameActionException {
        Direction dir = directions[2]; //doesn't matter
        while (numOfFulfillments == 0) {
            if (tryBuild(RobotType.FULFILLMENT_CENTER, dir)) {
                numOfFulfillments++;
            } else {
                dir = dir.rotateLeft();
            }
        }
    }

    public void myTurn() throws GameActionException {
        if (myType == MinerType.GATHERER) {
            gatherer();
        } else {
            builder();
        }
    }
    boolean senseNearbyRefinery() throws GameActionException {
        //Return true if the robot senses refinery nearby, otherwise return false.
        for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if (bot.getType() == RobotType.REFINERY) {
                refineLoc = bot.getLocation();
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
    boolean tryMove(Direction dir)throws GameActionException {
        final int failed_attampts_limit = 3;
        //sense for important buildings every move
        for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if (bot.getType() == RobotType.DELIVERY_DRONE) {
                numOfDesignSchools++;
                //this is going to keep going up sensing the same building but keeps us from making a whole bunch of them
            }
            if (bot.getType() == RobotType.FULFILLMENT_CENTER) {
                numOfFulfillments++;
            }
        }
        if (super.tryMove(dir)) {
            failedMoves =0;
            return true;
        } else {
            failedMoves++;
            if (failedMoves > failed_attampts_limit) {
                if (rc.getLocation().isAdjacentTo(hqLoc)){
                    rc.disintegrate();
                }
            }
            return false;
        }
    }
}