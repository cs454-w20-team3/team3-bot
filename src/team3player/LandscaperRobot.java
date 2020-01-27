package team3player;
import battlecode.common.*;
class LandscaperRobot extends RobotFramework {
    MapLocation hqLoc = null;
    MapLocation dsLoc = null;
    LandscaperRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        //on robot creation/start up code goes here
        //sense near by robots is expense, so lets do it once and pass results
        RobotInfo[] bots = rc.senseNearbyRobots();
        detectHQ(bots);
        detectDS(bots);
    }
    public void myTurn()throws GameActionException {
        //do we know where the hq is?
        if (hqLoc == null) {
            //we need to find the HQ
            //lets circle the design school
            searchForHQ();
        } else {
            //we know were it is
            buildWall();
        }
    }
    public void buildWall() {

    }
    public void searchForHQ()throws GameActionException {
        boolean clockwise = true;
        int turnsBeforeSwitchingDirection = 4;
        while (hqLoc == null) {
            waitforcooldown();
            Direction currentRelativeDir = dsLoc.directionTo(rc.getLocation());
            Direction moveTo = null;
            if (clockwise) {
                moveTo = currentRelativeDir.rotateRight();
            } else {
                moveTo = currentRelativeDir.rotateLeft();
            }
            MapLocation target = dsLoc.add(moveTo).add(moveTo);
            if (tryToGoTo(target, turnsBeforeSwitchingDirection)) {
                detectHQ(rc.senseNearbyRobots());
            } else {
                clockwise = !clockwise;
            }
        }
    }
    public void detectHQ(RobotInfo[] robots) {
        for (RobotInfo robot : robots) {
            if (robot.type == RobotType.HQ && robot.getTeam() == rc.getTeam()) {
                hqLoc = robot.getLocation();
            }
        }
    }
    public void detectDS(RobotInfo[] robots) {
        for (RobotInfo robot : robots) {
            if (robot.type == RobotType.DESIGN_SCHOOL && robot.getTeam() == rc.getTeam()) {
                dsLoc = robot.getLocation();
            }
        }
    }
}