package team3player;
import battlecode.common.*;
class LandscaperRobot extends RobotFramework {
    MapLocation hqLoc; //this is initialized to null, if used it will create a null reference exception
    
    LandscaperRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        //on robot creation/start up code goes here
        System.out.println("Landscaper:" + rc.getID() + " initialization");
        for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if (bot.getType() == RobotType.HQ) {
                hqLoc = bot.getLocation();
                System.out.println("Found HQ Location: " + hqLoc.x + ":" + hqLoc.y);
            }
        } //the landscaper might not sense the HQ in its first turn
        //so hqLoc might still be null after this

    }
    public void myTurn()throws GameActionException {
        //logic to be run on every turn goes here
        waitforcooldown();

        MapLocation myLoc = rc.getLocation();
        //the next line uses the .y and .x property on hqLoc, but hqLoc might still be null throwing a null reference exception
        double distHQ = Math.pow((myLoc.x - hqLoc.x),2) + Math.pow((myLoc.y - hqLoc.y),2);
        Direction dirDep = rc.getLocation().directionTo(hqLoc);
        Direction dirDig = dirDep.opposite();  // old, non-working approach - (dirDep + 4) % 8

        if (distHQ > 2 && distHQ < 9) { // makes sure it is in the appropriate range from HQ
            if (rc.canDigDirt(dirDig)) {
                rc.digDirt(dirDig);
                System.out.println("Landscaper "+ rc.getID() +"digging.");
            }
            else {
                System.out.println("Landscaper "+ rc.getID() +"cannot dig; attempting deposit.");
                if (rc.canDepositDirt(dirDep)) {
                    rc.depositDirt(dirDep);
                    System.out.println("Landscaper "+ rc.getID() +"depositing dirt.");
                }
            }
        }
        else {
            if (distHQ < 3) {
                if (tryMove(dirDig)) {
                    System.out.println("Landscaper " + rc.getID() + "moving away from HQ.");
                } else {
                    System.out.println("Landscaper " + rc.getID() + " cannot move.");
                }
            }
            if (distHQ > 13) {
                if (tryMove(dirDep)) {
                    System.out.println("Landscaper " + rc.getID() + "moving towards HQ.");
                }
                else {
                    System.out.println("Landscaper " + rc.getID() + " cannot move.");
                }
            }
        }
    }
}