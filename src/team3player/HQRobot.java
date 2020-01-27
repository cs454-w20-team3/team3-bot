package team3player;
import battlecode.common.*;
class HQRobot extends RobotFramework {
    int numOfMiners = 0;

    HQRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        System.out.println("HQ initialization");
    }
    public void myTurn() throws GameActionException {
        System.out.println("HQ: my turn!");
        waitforcooldown();
        for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam().opponent())) {
            if (rc.canShootUnit(bot.ID)) {
                rc.shootUnit(bot.ID);
            }
            else
                System.out.println("cannot shoot bot");
        }
        for (Direction dir : directions) {
            if (numOfMiners < 5) {
                if (tryBuild(RobotType.MINER, dir))
                    numOfMiners++;
                else
                    System.out.println("HQ could not build miner");
            }
        }
    }

}