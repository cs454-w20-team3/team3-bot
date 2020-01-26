package team3player;
import battlecode.common.*;


public strictfp class RobotPlayer {
    static RobotController rc;
    //this points the object that will be the logic and memory for the current robot
    static RobotFramework thisRobot = null;

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
                        case COW:                /* do nothing */                       break;
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
}