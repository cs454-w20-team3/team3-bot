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
    int numOfVaporizers = 0;
    int startRound;

    MinerRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMoveSafe which need rc
        super(rc_);
        //on robot creation/start up code goes here
        myType = determineMyType(10, 5, 1000);
        numOfDesignSchools = 0;
        numOfRefineries = 0;
        numOfFulfillments = 0;
        startRound= rc.getRoundNum();
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
            boolean found = lookForSoup();
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
            moveNextTo(soupLocs[soupLocIdx]);
        }
        return isSoupNextToMe();
    }
    boolean isSoupNextToMe()throws GameActionException {
        int soupAmount=0;
        for (Direction dir : directions) {
            soupAmount+= rc.senseSoup(rc.getLocation().add(dir));
        }
        soupAmount += rc.senseSoup(rc.getLocation());
        if (soupAmount > 0) {
            return true;
        } else {
            return false;
        }
    }
    void getSoup() throws GameActionException {
        final int refCost = 200;
        if (!isSoupNextToMe()) {
            return;
        }
        int soup = rc.getTeamSoup();
        if ( soup >= refCost ) {
            for (int i=0;i<5 && !checkForRefinery(); i++)
                buildRefinery();
        }
        int mysoup = rc.getSoupCarrying();
        while (mysoup < RobotType.MINER.soupLimit && isSoupNextToMe()) {
            waitforcooldown();
            mineAdjacentSoup();
            mysoup = rc.getSoupCarrying();
        }
        processSoup();
    }

    void mineAdjacentSoup() throws GameActionException {
        MapLocation loc = rc.getLocation();
        for (Direction dir : Direction.values()) {
            while (rc.senseSoup(loc.add(dir)) > 0) {
                tryMine(dir);
                if (rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
                   return; 
                }
            }
        }
    }


    boolean checkForRefinery() throws GameActionException {
        for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if (bot.getType() == RobotType.REFINERY) {
                numOfRefineries++;
                refineLoc = bot.getLocation();
                return true;
            }
        }
        return false;
    }

    void buildRefinery() throws GameActionException {
        for (Direction dir : directions) {
            waitforcooldown();
            if (!hqAdjacent(rc.getLocation().add(dir)))
            if (tryBuild(RobotType.REFINERY, dir)) {
                numOfRefineries++;
                refineLoc = rc.getLocation().add(dir);
                return;
            }
        }
    }
    boolean hqAdjacent(MapLocation target) {
        if (hqLoc.isAdjacentTo(target)){
            return true;
        } else {
            return false;
        }
    }
    void processSoup() throws GameActionException {
        if (refineLoc == null)
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
    void moveNextTo(MapLocation target) throws GameActionException{
        //while I am not next to my target location
        while (!rc.getLocation().isAdjacentTo(target)) {
            //try to move towards it
            waitforcooldown();
            MapLocation curLoc = rc.getLocation();
            Direction dir = curLoc.directionTo(target);
            boolean moved = tryMoveSafe(dir);
            while (!moved) {
                if (!moved) moved = tryMoveSafe(dir.rotateRight());
                if (!moved) moved = tryMoveSafe(dir.rotateRight().rotateRight());
                if (!moved) moved = tryMoveSafe(dir.rotateLeft());
                if (!moved) moved = tryMoveSafe(dir.rotateLeft().rotateLeft());
            }
        }
    }


    void goToHQ() throws GameActionException {
        moveNextTo(hqLoc);
        while (rc.getSoupCarrying() > 0)
        tryRefine(rc.getLocation().directionTo(hqLoc));
        return;
    }

    void goToRefinery(MapLocation refineLoc) throws GameActionException {
        moveNextTo(refineLoc);
        while (rc.getSoupCarrying() > 0)
        tryRefine(rc.getLocation().directionTo(refineLoc));
        return;
    }
    void builder()throws GameActionException {
        if (numOfRefineries == 0) {
            //if we don't have a refinery, be a gatherer
            gatherer();
            return;
        }
        //if there is no DS
        if (numOfDesignSchools == 0) {
            moveToGoodDSArea();
        }
        //if there is no FC
        if (numOfFulfillments == 0) {
            buildFC();
        }
        // build appropriate vaporizers
        if (numOfVaporizers == 0) {
            tryBuildVape();
        }
        //otherwise become a gatherer
        myType = MinerType.GATHERER;
        return;
    }

    void moveToGoodDSArea()throws GameActionException {
        while (tooFarFromHQ()) {
            waitforcooldown();
            Direction hqdir = rc.getLocation().directionTo(hqLoc);
            boolean moved = tryMoveSafe(hqdir);
            if (!moved) {
                moved = tryMoveSafe(hqdir.rotateRight());
            }
            if (!moved) {
                moved = tryMoveSafe(hqdir.rotateRight().rotateRight());
            }
            if (!moved) {
                moved = tryMoveSafe(hqdir.rotateLeft());
            }
            if (!moved) {
                moved = tryMoveSafe(hqdir.rotateLeft().rotateLeft());
            }
        }
        while (tooCloseToHQ()) {
            waitforcooldown();
            Direction away = rc.getLocation().directionTo(hqLoc).opposite();
            boolean moved = tryMoveSafe(away);
            if (!moved) {
                moved = tryMoveSafe(away.rotateRight());
            }
            if (!moved) {
                moved = tryMoveSafe(away.rotateLeft());
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

    int tryBuildVape() throws GameActionException {
        if (rc.getRoundNum() > 9999999) {
            Direction dir = rc.getLocation().directionTo(hqLoc);
            if (tryBuild(RobotType.VAPORATOR, dir)) {
                numOfVaporizers++;
            }
            return 1;
        } else {
            return 0;
        }
    }

    void buildFC()throws GameActionException {
        Direction dir = directions[2]; //doesn't matter
        while (numOfFulfillments == 0) {
            if (tryBuild(RobotType.FULFILLMENT_CENTER, dir)) {
                numOfFulfillments++;
            } else {
                dir = dir.rotateLeft();
            }
            dir = dir.rotateLeft();
        }
        return; // next turn
    }


    public void myTurn() throws GameActionException {
        if (myType == MinerType.GATHERER) {
            gatherer();
        } else {
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
        if (currentRound < 3) {
            return MinerType.BUILDER;
        }
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
    boolean tryMoveSafe(Direction dir)throws GameActionException {
        final int failed_attampts_limit = 3;
        //sense for important buildings every move
        for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if (bot.getType() == RobotType.DESIGN_SCHOOL) {
                numOfDesignSchools++;
                //this is going to keep going up sensing the same building but keeps us from making a whole bunch of them
            }
            if (bot.getType() == RobotType.FULFILLMENT_CENTER) {
                numOfFulfillments++;
            }
        }
        if (super.tryMoveSafe(dir)) {
            failedMoves =0;
            return true;
        } else {
            if (rc.getRoundNum() - startRound > 10)
            {
                failedMoves++;
            }
            if (failedMoves > failed_attampts_limit) {
                if (rc.getLocation().isAdjacentTo(hqLoc)){
                    rc.disintegrate();
                }
            }
            return false;
        }
    }
}