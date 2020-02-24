package team3player;
import battlecode.common.*;
class DesignSchoolRobot extends RobotFramework {
    MapLocation dsLoc;
    MapLocation hqLoc = null;
    Direction hqDir = null;
    int numOfLandscapers = 0;
    final int maxNumOfLandscapers = 5;

    DesignSchoolRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        //on robot creation/start up code goes here
        System.out.println("Design School initialization");
        dsLoc = rc.getLocation();
        idNearbyBots();
        System.out.println("I'm located at " +dsLoc.x + ":" + dsLoc.y);
    }
    public void myTurn()throws GameActionException {

        if (numOfLandscapers < maxNumOfLandscapers) {
            if (hqLoc != null) {
                //we know where the hq is
                hqDir = rc.getLocation().directionTo(hqLoc);
                boolean built = tryBuild(RobotType.LANDSCAPER, hqDir);
                while (!built) {
                    waitforcooldown();
                    built = tryBuild(RobotType.LANDSCAPER, hqDir.rotateLeft());
                    if (!built) {
                        built = tryBuild(RobotType.LANDSCAPER, hqDir.rotateRight());
                    }
                    if (!built) {
                        built = tryBuild(RobotType.LANDSCAPER, hqDir.rotateRight().rotateRight());
                    }
                    if (!built) {
                        built = tryBuild(RobotType.LANDSCAPER, hqDir.rotateLeft().rotateLeft());
                    }
                    if (!built) {
                        built = tryBuild(RobotType.LANDSCAPER, hqDir);
                    }
                }
            } else {
                //we don't know where the hq is
                    for (Direction dir : directions) {
                        if (numOfLandscapers < maxNumOfLandscapers) {
                            if (tryBuild(RobotType.LANDSCAPER, dir))
                                numOfLandscapers++;
                            else
                                System.out.println("DS could not build landscaper");
                    }
                }
            }
        }
    }

    public void idNearbyBots() {
        for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if (bot.getType() == RobotType.HQ && bot.getTeam() == rc.getTeam()) {
                hqLoc = bot.getLocation();
                hqDir = dsLoc.directionTo(hqLoc);
                System.out.println("Found HQ Location: " + hqLoc.x + ":" + hqLoc.y);
            }
        }
    }
}