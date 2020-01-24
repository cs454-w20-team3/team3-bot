package team3player;
import battlecode.common.*;
class RefineryRobot extends RobotFramework {
    MapLocation hqLoc;
    
    RefineryRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        //on robot creation/start up code goes here
    }
    public void myTurn()throws GameActionException {
        //refinery doesn't really need to do anything
        //it does have a bytecode budget
        //so offense blockchain stuff could go here
        //but I don't think we are going to implement that
    }
}