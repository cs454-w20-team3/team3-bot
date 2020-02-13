
package team3player;
import battlecode.common.*;
class NetGunRobot extends RobotFramework {
    MapLocation hqLoc;

    NetGunRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        //on robot creation/start up code goes here
    }

    public void myTurn() throws GameActionException {
        //logic to be run on every turn goes here

        Team enemy = rc.getTeam().opponent();
        RobotInfo[] enemiesInRange = rc.senseNearbyRobots(GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED, enemy);

        for (RobotInfo e : enemiesInRange) {
            if (e.type == RobotType.DELIVERY_DRONE) {
                if (rc.canShootUnit(e.ID)) {
                    rc.shootUnit(e.ID);
                    System.out.println("Enemy Drone Shot Down!");
                    //break;
                }
            }
        }
    }
}