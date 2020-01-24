package team3player;
import battlecode.common.*;
class LandscaperRobot extends RobotFramework {
    MapLocation hqLoc;
    
    LandscaperRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        //on robot creation/start up code goes here
    }
    public void myTurn()throws GameActionException {
        //logic to be run on every turn goes here
    }
}