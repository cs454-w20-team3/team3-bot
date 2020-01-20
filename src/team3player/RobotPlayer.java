package team3player;
import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;
    static boolean firstTurn = true;
    static MemoryforMiner minerMemory = null;
    static MemoryforHQ HQMemory = null;
    static MemoryforRefinery RefineryMemory = null;
    static Team myTeam = null;
    static Team oppTeam = null;
    static Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST
    };
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static int turnCount;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;
        Team myTeam = rc.getTeam();
        Team oppTeam = myTeam.opponent();
        turnCount = 0;
        
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You can add the missing ones or rewrite this into your own control structure.
                switch (rc.getType()) {
                    case HQ:                 runHQ();                break;
                    case MINER:              runMiner();             break;
                    case REFINERY:           runRefinery();          break;
                    case VAPORATOR:          runVaporator();         break;
                    case DESIGN_SCHOOL:      runDesignSchool();      break;
                    case FULFILLMENT_CENTER: runFulfillmentCenter(); break;
                    case LANDSCAPER:         runLandscaper();        break;
                    case DELIVERY_DRONE:     runDeliveryDrone();     break;
                    case NET_GUN:            runNetGun();            break;
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void runHQ() throws GameActionException {
      if (firstTurn) {
        firstTurn = false;
        HQStartup();
      }
        for (Direction dir : directions) {
            //do we have less than 11 miners? Then try to build some more
            //this isn't a very good change. just showing how we can use 
            //the HQMemory object
            if (HQMemory.numOfMiners < 11 && tryBuild(RobotType.MINER, dir)) {
              HQMemory.numOfMiners++;
            }
          }
    }

    static void runMiner() throws GameActionException {
        if (firstTurn) {
          firstTurn=false;
          minerStartup();
        }
        //What does a miner/worker need to do?
        //is this a builder or a gatherer?
        switch (minerMemory.myRole) {
        case Gatherer: runMinerGather(); break;  
        case Builder: runMinerBuilder(); break;
        }
    }
    static void runMinerGather() throws GameActionException {
      switch (minerMemory.myMode) {
        case Gathering:
        //I need to go get soup
        //lets scan for the furthest soup and go that direction
        //picking up anything I can along the way
        if (minerMemory.destination == null) {
          setDestinationFSoup();
        }
        //if they are full or made it to their destination go home
        if (rc.getSoupCarrying() == 100 || rc.getLocation() == minerMemory.destination) {
          //we need to go back home 
          minerMemory.myMode = MemoryforMiner.Mode.Returning;
        }
        //boolean canMineSoup(Direction dir)
        for (Direction dir : directions) {
          if (rc.canMineSoup(dir)) {
            if (tryMine(dir))
            System.out.println("I have " + rc.getSoupCarrying() + " soup");
          }
        }
        Direction wantToGo = rc.getLocation().directionTo(minerMemory.destination);
        if (rc.canMove(wantToGo)) {
          //I really need better pathing.
          tryMove(wantToGo);
        }
        break; //end Gathering mode
        case Returning:
          //we need to go to HQ (or other refinery, but that will be for later)
          if (rc.canDepositSoup(rc.getLocation().directionTo(minerMemory.hq))) {
            while (rc.getSoupCarrying() > 0)
            rc.depositSoup(rc.getLocation().directionTo(minerMemory.hq), rc.getSoupCarrying());
            minerMemory.myMode = MemoryforMiner.Mode.Gathering;
            Clock.yield();
          }
          tryMove(rc.getLocation().directionTo(minerMemory.hq));
          
        break;
      }
      
      
    }
    static void setDestinationFSoup() {
      MapLocation[] soupLocations = rc.senseNearbySoup();
      minerMemory.destination = soupLocations[0];
      int max = rc.getLocation().distanceSquaredTo(minerMemory.destination);
      for (MapLocation soupLoc : soupLocations) {
        int distance = rc.getLocation().distanceSquaredTo(soupLoc);
        if (distance > max) {
          max = distance;
          minerMemory.destination = soupLoc;
        }
      }
    }
    static void runMinerBuilder() {
      
    }

    static void runRefinery() throws GameActionException {
        // System.out.println("Pollution: " + rc.sensePollution(rc.getLocation()));
        if (firstTurn) {
          firstTurn = false;
          RefineryStartup();
        }
    }

    static void runVaporator() throws GameActionException {

    }

    static void runDesignSchool() throws GameActionException {

    }

    static void runFulfillmentCenter() throws GameActionException {
        for (Direction dir : directions)
            tryBuild(RobotType.DELIVERY_DRONE, dir);
    }

    static void runLandscaper() throws GameActionException {

    }

    static void runDeliveryDrone() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        if (!rc.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within capturing range
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

            if (robots.length > 0) {
                // Pick up a first robot within range
                rc.pickUpUnit(robots[0].getID());
            }
        } else {
            // No close robots, so search for robots within sight radius
            tryMove(randomDirection());
        }
    }

    static void runNetGun() throws GameActionException {

    }

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnedByMiner() {
        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        if (rc.isReady()) {
          if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
          } else {
            //why can't we move?
            //if occupied by another bot
            //isLocationOccupied(MapLocation loc)
            //add(Direction direction)
            if (rc.isLocationOccupied(rc.getLocation().add(dir))) {
              //another bot is in our way. Dennis Rodman time.
              if (rc.canMove(dir.rotateRight())) {
                rc.move(dir.rotateRight());
                return true;
              } else if (rc.canMove(dir.rotateLeft())) {
                rc.move(dir.rotateLeft());
                return true;
              }
            }
          }
        }
        return false;
    }

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }


    static void tryBlockchain() throws GameActionException {
        if (turnCount < 3) {
            int[] message = new int[7];
            for (int i = 0; i < 7; i++) {
                message[i] = 123;
            }
            if (rc.canSubmitTransaction(message, 10))
                rc.submitTransaction(message, 10);
        }
        // System.out.println(rc.getRoundMessages(turnCount-1));
    }
    static void minerStartup() {
      minerMemory = new MemoryforMiner();
      for (RobotInfo bot : rc.senseNearbyRobots(-1, myTeam)) {
        //loop through every friendly bot in my range
        if (bot.getType() == RobotType.HQ) {
          //if this bot is my HQ save its location
          minerMemory.hq= bot.getLocation();
        }
      }
      //since ID is a random value 
      //although I don't know its distrobution :-(
      //I can use it to assign roles to bot probablistically
      if (rc.getID() % 10 < 2) {
        //ID is from 10000 to 32000 I think. So, mod 10 is
        //0,1,2,3,4,5,6,7,8,9. less than 2 is 0 and 1
        //this all basically means that %20 of miner get this role
        minerMemory.myRole=MemoryforMiner.Role.Builder;
      } else {
        minerMemory.myRole=MemoryforMiner.Role.Gatherer;
        minerMemory.myMode=MemoryforMiner.Mode.Gathering;
      }
    }
    static void HQStartup() {
      HQMemory = new MemoryforHQ();
    }
    static void RefineryStartup() {
      RefineryMemory = new MemoryforRefinery();
    }
}
class MemoryforMiner {
  public static MapLocation hq=null;
  enum Role { Builder, Gatherer; }
  public Role myRole;
  enum Mode { Gathering, Returning;}
  public Mode myMode = null;
  public MapLocation destination=null;
  public MemoryforMiner() {}
}
class MemoryforHQ {
  public MemoryforHQ() {
    numOfMiners=0;
  }
  public int numOfMiners;
}
class MemoryforRefinery {
  
}