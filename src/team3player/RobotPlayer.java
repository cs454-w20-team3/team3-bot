package team3player;
import battlecode.common.*;


public strictfp class RobotPlayer {
    static RobotController rc;
    //this points the object that will be the logic and memory for the current robot
    static RobotFramework thisRobot = null;

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
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static int turnCount;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;
        turnCount = 0;

        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                //this will only execute on the first turn of each robot
                //we create a robot class of the correct type
                if (turnCount == 1) {                   
                    switch (rc.getType()) {
                        case HQ:                 thisRobot = new HQRobot(rc);           break;
                        case MINER:              thisRobot = new MinerRobot(rc);        break;
                        case REFINERY:           thisRobot = new RefineryRobot(rc);     break;
                        case VAPORATOR:          thisRobot = new VaporatorRobot(rc);    break;
                        case DESIGN_SCHOOL:      thisRobot = new DesignSchoolRobot(rc); break;
                        case FULFILLMENT_CENTER: thisRobot = new FCRobot(rc);           break;
                        case LANDSCAPER:         thisRobot = new LandscaperRobot(rc);   break;
                        case DELIVERY_DRONE:     thisRobot = new DroneRobot(rc);        break;
                        case NET_GUN:            thisRobot = new NetGunRobot(rc);       break;
                    }
                }
                //then instead of all the runMiner, runHQ... functions we just run thisRobot.myTurn()
                thisRobot.myTurn();

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnedByMiner() {
        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
    }

    static boolean tryMove() throws GameActionException {
        for (Direction dir : directions)
            if (tryMove(dir))
                return true;
        return false;
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMine(Direction dir) throws GameActionException {
        System.out.println("Miner ready? " + rc.isReady());
        System.out.println("Miner can mine soup? " + rc.canMineSoup(dir));
        System.out.println("Reached soup limit? " + rc.getSoupCarrying() + ":" + rc.getType().soupLimit);
        System.out.println("Remain cooldown turns? " + rc.getCooldownTurns());
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryRefine(Direction dir) throws GameActionException {
        System.out.println("tryRefine:Miner ready? " + rc.isReady());
        System.out.println("tryRefine:Miner can deposit soup? " + rc.canDepositSoup(dir));
        System.out.println("tryRefine: Remain cooldown turns? " + rc.getCooldownTurns());
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }


    static void tryBlockchain() throws GameActionException {
        if (turnCount < 3) {
            int[] message = new int[7];
            for (int i = 0; i < 7; i++) {
                message[i] = 123;
            }
            if (rc.canSubmitTransaction(message, 10))
                rc.submitTransaction(message, 10);
        }
        // System.out.println(rc.getRoundMessages(turnCount-1));
    }
}