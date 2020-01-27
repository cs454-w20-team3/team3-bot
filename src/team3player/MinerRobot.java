package team3player;
import battlecode.common.*;
class MinerRobot extends RobotFramework {
    MapLocation hqLoc;

    int OurRobot[];
    int BuildTarget = 5;
    int GatherTarget = 1;
    int RobotPoint = 0;

    MinerRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        //on robot creation/start up code goes here

        // Avi Code Starts

        RobotPoint += 1 ;

        System.out.println("Miner:" + rc.getID() + " initialization");

        OurRobot[RobotPoint] = rc.getID();

        if ( RobotPoint % BuildTarget == 0 ) {
            buildMode(); }

        else if ( RobotPoint % GatherTarget == 0 ) {
            gatherMode(); }


        if ( RobotPoint % 6 == 0 && BuildTarget > 1) {
            BuildTarget -= 1;
            GatherTarget += 1;
        }

        // Avi Code Ends

        for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if (bot.getType() == RobotType.HQ) {
                hqLoc = bot.getLocation();
                System.out.println("Found HQ Location: " + hqLoc.x + ":" + hqLoc.y);
            }
        }
    }

    public void gatherMode() {
        System.out.println("I am Gatherer");

    }

    public void buildMode() {
        System.out.println("I am Builder");

    }

    public void myTurn() throws GameActionException {
        //logic to be run on every turn goes here
        MapLocation[] soupLocs = rc.senseNearbySoup();
        if (soupLocs.length != 0) {
            System.out.println("Miner found the soup location");
            int soupLocIdx = (int)(soupLocs.length * Math.random());
            System.out.println("soupLoc's index: " + soupLocIdx);
            Direction dirToSoup = rc.getLocation().directionTo(soupLocs[soupLocIdx]);
            tryMove(dirToSoup);
            if (tryMine(Direction.CENTER))
                System.out.println("I mined soup! " + rc.getSoupCarrying());
        } else {
            System.out.println("Minder couldn't find the soup location");
            tryMove(randomDirection());
        }

        if (rc.getSoupCarrying() == RobotType.MINER.soupLimit){
            Direction dirToHQ = rc.getLocation().directionTo(hqLoc);
            if(tryMove(dirToHQ))
                System.out.println("Move to HQ");
            else
                System.out.println("Can't move to HQ");
            if (tryRefine(Direction.CENTER))
                System.out.println("I refined soup! " + rc.getTeamSoup());
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