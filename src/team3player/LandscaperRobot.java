package team3player;
import battlecode.common.*;
class LandscaperRobot extends RobotFramework {
    MapLocation hqLoc; //this is initialized to null, if used it will create a null reference exception
    MapLocation baddyHqLoc; //location of enemy HQ
    MapLocation myLoc; //stores this bots location

    LandscaperRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);
        //on robot creation/start up code goes here
        System.out.println("Landscaper:" + rc.getID() + " initialization");
        myLoc = rc.getLocation();
        idNearbyBots();
    }

    public void myTurn()throws GameActionException {
        //logic to be run on every turn goes here
        waitforcooldown();
        seekHQ();
        fortifyHQ();
    }

    public boolean seekHQ()throws GameActionException {
        myLoc = rc.getLocation();
        while (hqLoc == null) {
            System.out.println("seeking HQ, " + myLoc);
            idNearbyBots();
            tryMoveSafe(randomDirection());
        }
        return true;
    }

    public void fortifyHQ() throws GameActionException {
        waitforcooldown();
        myLoc = rc.getLocation();
        double distHQ = Math.pow((myLoc.x - hqLoc.x),2) + Math.pow((myLoc.y - hqLoc.y),2);  //this line uses the .y and .x property on hqLoc, but hqLoc might still be null throwing a null reference exception
        Direction dirHQ = rc.getLocation().directionTo(hqLoc);
        Direction dirDep = getDepositDir(dirHQ);
        Direction dirTo = getDestinationDir(dirHQ);
        Direction dirDig = rc.getLocation().directionTo(hqLoc).opposite();  // old, non-working approach - (dirDep + 4) % 8
        System.out.println("Landscaper:" + rc.getID() + ", distance to HQ:" + distHQ + ", direction to HQ:" + dirHQ);
				MapLocation[] locs = adjacentLocUnion(myLoc, hqLoc);
				MapLocation optimal = minElev(locs);
				Direction opt_dir = myLoc.directionTo(optimal);
        if (distHQ < 4) { // makes sure it is in the appropriate range from HQ
//            if (hqLoc != null) goToLowPoint(dirHQ);
						if (optimal == null)
							digDepMove(dirDig, dirDep, dirTo);
						else
							digDepMove(dirDig, opt_dir, dirTo);
        }
        //else if its 2 spaces away and elevation is too high, help build the way from the outside
        else {
            if (distHQ > 2) {
							if (optimal != null) {
								//we have a good spot to deposit dirt
								digDepMove(dirDig, opt_dir, dirTo);
							}
                System.out.println("distance over 2");
                if (tryMoveSafe(dirHQ)) {
                    System.out.println("Landscaper " + rc.getID() + "moving towards HQ.");
                }
                else {
                    trySaunterSafe(dirHQ);
                    System.out.println("Landscaper " + rc.getID() + " cannot move towards HQ, trying random.");
                }
            }
        }
    }

    public static MapLocation[] adjacentLocUnion(MapLocation loc1, MapLocation loc2) {
        if (loc1 == null || loc2 == null) {
            return new MapLocation[]{};
        }
        if (!loc1.isWithinDistanceSquared(loc2,8)){
            //if distance is larger than 8 there are no union adjacent locations
            return new MapLocation[]{};
        }
        MapLocation[] results = new MapLocation[4];
        //the maximum adjacent union is 4 locations
        int index=0;

        for (Direction dir : Direction.values()) {
            if (dir == Direction.CENTER) {
                continue;
            }
            MapLocation adj = loc1.add(dir);
            if (adj.isAdjacentTo(loc2) && !adj.equals(loc2)) {
                results[index]=adj;
                index++;
            }
        }
        return results;
    }

    public MapLocation minElev(MapLocation[] locs)throws GameActionException {
        if (locs == null || locs.length == 0) {
            return null;
        }
        MapLocation result = locs[0];
        int min = rc.senseElevation(result);
        for (int i=1; i< locs.length  ; i++) {
					if (locs[i] == null) continue;
            int cur = rc.senseElevation(locs[i]);
            if (cur < min) {
                result = locs[i];
                min = cur;
            }
        }
        return result;
    }



    public void goToLowPoint(Direction dir) throws GameActionException {
        int lowElevation = 1000000;
        Direction lowPoint = null;
        for (Direction d : directions) {
            MapLocation adjacent = hqLoc.add(d);
            int adjElevation = (rc.senseElevation(adjacent));
            lowElevation = Math.min(adjElevation, lowElevation);
            if (adjElevation == lowElevation) {
                lowPoint = rc.getLocation().directionTo(adjacent);
            }
        }
        if (tryMoveSafe(lowPoint)) {
            System.out.println("Landscaper " + rc.getID() + "moving towards HQ.");
        }
        else if (trySaunterSafe(lowPoint)) {
            System.out.println("Landscaper " + rc.getID() + " cannot move towards HQ, trying random.");
        }
    }

    public Direction getDepositDir(Direction In) {
        Direction depDir = null;

        switch(In) {
            case NORTH:     depDir = Direction.EAST;
                            break;
            case NORTHEAST: depDir = Direction.EAST;
                            break;
            case EAST:      depDir = Direction.SOUTH;
                            break;
            case SOUTHEAST: depDir = Direction.SOUTH;
                            break;
            case SOUTH:     depDir = Direction.WEST;
                            break;
            case SOUTHWEST: depDir = Direction.WEST;
                            break;
            case WEST:      depDir = Direction.NORTH;
                            break;
            case NORTHWEST: depDir = Direction.NORTH;
                            break;
        }
        return depDir;
    }

    public Direction getDestinationDir(Direction In) {
        Direction moveDir = null;
        switch(In) {
            case NORTH:       moveDir = Direction.WEST;
                break;
            case NORTHEAST:   moveDir = Direction.NORTH;
                break;
            case EAST:        moveDir = Direction.NORTH;
                break;
            case SOUTHEAST:   moveDir = Direction.EAST;
                break;
            case SOUTH:       moveDir = Direction.EAST;
                break;
            case SOUTHWEST:   moveDir = Direction.SOUTH;
                break;
            case WEST:        moveDir = Direction.SOUTH;
                break;
            case NORTHWEST:   moveDir = Direction.WEST;
                break;
        }
        return moveDir;
    }

    public void digDepMove(Direction dirDig, Direction dirDep, Direction dirTo) throws GameActionException {
        waitforcooldown();
        if (rc.canDigDirt(dirDig)) {
            rc.digDirt(dirDig);
        }
        waitforcooldown();
        if (rc.canDepositDirt(dirDep)) {
            rc.depositDirt(dirDep);
        }
        waitforcooldown();
        if (!tryMoveSafe(dirTo)) {
            trySaunterSafe(dirTo);
        }
    }

    public void idNearbyBots() {
        for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam())) {
            if (bot.getType() == RobotType.HQ && bot.getTeam() == rc.getTeam()) {
                hqLoc = bot.getLocation();
                System.out.println("Found HQ Location: " + hqLoc.x + ":" + hqLoc.y);
            }
            if (bot.getType() == RobotType.HQ && bot.getTeam() == rc.getTeam().opponent()) {
                baddyHqLoc = bot.getLocation();
                System.out.println("Found the enemy HQ Location: " + hqLoc.x + ":" + hqLoc.y);
            }
        }
    }
}
