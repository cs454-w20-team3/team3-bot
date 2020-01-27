package team3player;
import battlecode.common.*;
class DesignSchoolRobot extends RobotFramework {
    MapLocation dsLoc;
    int numOfLandscapers = 0;
    
    DesignSchoolRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        //on robot creation/start up code goes here
        System.out.println("Design School initialization");
        dsLoc = rc.getLocation();
        System.out.println("I'm located at ",dsLoc);
    }
    public void myTurn()throws GameActionException {
        //logic to be run on every turn goes here
        System.out.println("Design School doing stuff");
        waitforcooldown();
        for (Direction dir : directions) {
            if (numOfLandscapers < 5) {
                if (tryBuild(RobotType.LANDSCAPER, dir))
                    numOfLandscapers++;
                else
                    System.out.println("DS could not build landscaper");
            }
        }
    }
}