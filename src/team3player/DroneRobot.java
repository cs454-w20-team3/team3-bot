package team3player;
import battlecode.common.*;
class DroneRobot extends RobotFramework {
    MapLocation hqLoc;

    int hold;

    DroneRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        //on robot creation/start up code goes here
    }
    public void myTurn()throws GameActionException {
        //logic to be run on every turn goes here
        Team enemy = rc.getTeam().opponent();
        if (!rc.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within capturing range
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

            if (robots.length > 0)
            {
                // Pick up a first robot within range
                rc.pickUpUnit(robots[0].getID());
                hold = 1;
                System.out.println("I picked up " + robots[0].getID() + "!");

                while (hold == 1)
                {
                    if ( (rc.senseFlooding(rc.getLocation().add(Direction.CENTER))) == true )
                    {
                        rc.dropUnit(Direction.CENTER);
                        System.out.println("I dropped " + robots[0].getID() + "!");
                        hold = 0;
                    }
                    else
                    {
                        tryMove(randomDirection());
                    }
                }
            }
        } else {
            // No close robots, so search for robots within sight radius
            tryMove(randomDirection());
        }
    }
}