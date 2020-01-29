package team3player;
import battlecode.common.*;
class MinerRobot extends RobotFramework {
    MapLocation hqLoc;
    MapLocation refineLoc;
    boolean builtDesignSchool;
    boolean builtRefinery;

    MinerRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        //on robot creation/start up code goes here
        System.out.println("Miner:" + rc.getID() + " initialization");

        builtDesignSchool = false;

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
        if( !builtDesignSchool && rc.getID() % 5 == 0 ){
            //move to design school location
            MapLocation dsLoc = new MapLocation(hqLoc.x + 3, hqLoc.y + 3);
            Direction dirToDS = rc.getLocation().directionTo(dsLoc);
            tryMove(dirToDS);
            //try to build design school
            for (Direction dir : directions) {
                if (tryBuild(RobotType.DESIGN_SCHOOL, dir)) {
                    System.out.println("Miner build Design School");
                    builtDesignSchool = true;
                    Direction dirToHQ = rc.getLocation().directionTo(hqLoc);
                    tryMove(dirToHQ);
                }
            }
        } else if( !builtRefinery && rc.getID() % 5 == 1 ){
            //move to refinery location
            MapLocation rLoc = new MapLocation(hqLoc.x + 3, hqLoc.y - 3);
            Direction dirToRefinery = rc.getLocation().directionTo(rLoc);
            tryMove(dirToRefinery);
            //try to build refinery
            for (Direction dir : directions) {
                if (tryBuild(RobotType.REFINERY, dir)) {
                    refineLoc = rc.getLocation();
                    System.out.println("Miner build REFINERY! ");
                    builtRefinery = true;
                    Direction dirToHQ = rc.getLocation().directionTo(hqLoc);
                    if(!tryMove(dirToHQ))
                        tryMove(randomDirection());
                }
            }
        } else {
            //logic to be run on every turn goes here
            MapLocation[] soupLocs = rc.senseNearbySoup();
            if (soupLocs.length != 0) {
                System.out.println("Miner found the soup location");
                int soupLocIdx = (int) (soupLocs.length * Math.random());
                System.out.println("soupLoc's index: " + soupLocIdx);
                Direction dirToSoup = rc.getLocation().directionTo(soupLocs[soupLocIdx]);
                tryMove(dirToSoup);
                if (tryMine(Direction.CENTER))
                    System.out.println("I mined soup! " + rc.getSoupCarrying());
            } else {
                System.out.println("Miner couldn't find the soup location");
                tryMove(randomDirection());
            }

            if (rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
                for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam())) {
                    if (bot.getType() == RobotType.REFINERY) {
                        refineLoc = bot.getLocation();
                        System.out.println("Found Refinery Location: " + refineLoc.x + ":" + refineLoc.y);
                    }
                }
                Direction dirToRefine = null;
                if(refineLoc != null) {
                    System.out.println("Go Refinery to refine");
                    dirToRefine = rc.getLocation().directionTo(refineLoc);
                } else {
                    System.out.println("Go HQ to refine");
                    dirToRefine = rc.getLocation().directionTo(hqLoc);
                }
                if (tryMove(dirToRefine))
                    System.out.println("Move HQ or Refinery");
                else
                    System.out.println("Can't move to HQ or Refinery");

                if (tryRefine(Direction.CENTER))
                    System.out.println("I refined soup! " + rc.getTeamSoup());
            }
        }
    }
    boolean tryRefine(Direction dir) throws GameActionException {
        System.out.println("tryRefine:Miner ready? " + rc.isReady());
        System.out.println("tryRefine:Miner can deposit soup? " + rc.canDepositSoup(dir));
        System.out.println("tryRefine: Remain cooldown turns? " + rc.getCooldownTurns());
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
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