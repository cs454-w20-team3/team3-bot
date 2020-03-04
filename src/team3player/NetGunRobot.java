
package team3player;
import battlecode.common.*;
class NetGunRobot extends RobotFramework {
    Team myTeam = null;
    Team enemyTeam = null;
    int kills;

    NetGunRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        myTeam = rc.getTeam();
        enemyTeam = rc.getTeam().opponent();
        kills = 0;
        //on robot creation/start up code goes here
    }

    public void myTurn() throws GameActionException {
        //logic to be run on every turn goes here

        RobotInfo[] enemiesInRange = rc.senseNearbyRobots(-1, enemyTeam);

        for (RobotInfo e : enemiesInRange) {
            if (rc.canShootUnit(e.ID)) {
                rc.shootUnit(e.ID);
                kills++;
                System.out.println("Enemy Drone Shot Down!");
            }
        }
    }
}