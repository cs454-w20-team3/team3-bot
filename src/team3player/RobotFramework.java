package team3player;
import battlecode.common.*;

public abstract class RobotFramework {
    public RobotController rc;
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
    static final int teamSecret = 123456;
    static final String[] messageType = {
            "HQ loc",
            "design school created",
            "soup location",
    };

    RobotFramework(RobotController rc_) {
        rc=rc_;
    }

    abstract public void myTurn() throws GameActionException;

    boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

    Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    boolean tryMove() throws GameActionException {
        for (Direction dir : directions)
            if (tryMove(dir))
                return true;
        return false;
    }
    void waitforcooldown() {
        while (!rc.isReady()) {
            Clock.yield();
        }
    }

    boolean canMoveSafe(Direction dir) throws GameActionException {
        if (rc.canMove(dir) && rc.senseFlooding(rc.getLocation().add(dir)) == false) {
            return true;
        }
        return false;
    }

    boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }

    boolean tryMoveSafe(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && canMoveSafe(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }

    public boolean trySaunterSafe(Direction dir) throws GameActionException {
        boolean moved = false;
        Direction tryThis = dir;
        while (!moved) {
            tryThis = tryThis.rotateLeft();
            if (rc.isReady() && canMoveSafe(tryThis)) {
                rc.move(tryThis);
                moved = true;
            }
        }
        return true;
    }

    public int[] whatMessage(int BuildingCode, int enemySecret, int enemySecretPosition, int totalEnemyMessageLength) throws GameActionException {
        int count;
        int fake_x = rc.getMapWidth() / 2;// x coord of fake coords
        int fake_y = rc.getMapHeight() / 2;// y coord of fake coords
        int[] message = new int[totalEnemyMessageLength];

        message[enemySecretPosition] = enemySecret;
        message[enemySecretPosition++] = 2;

        for(count=0; count<totalEnemyMessageLength-1; count++)
        {
            if (count != enemySecretPosition && count != enemySecretPosition++) {
                if (count++ != enemySecretPosition && count++ != enemySecretPosition++) {
                    message[count] = (int) (fake_x + (Math.random() * 10));
                    message[count++] = (int) (fake_y + (Math.random() * 10));
                    count++;
                }
                else {
                    message[count] = teamSecret;
                }
            }
        }
        return message;
    }

    public int sendHqMssg() throws GameActionException {
        int cost = (rc.getRoundNum() / 100);
        int fake_x = rc.getMapWidth() / 2;// x coord of fake coords
        int fake_y = rc.getMapHeight() / 2;// y coord of fake coords
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 0;
        message[2] = (int) (fake_x + (Math.random()*10));
        message[3] = (int) (fake_y + (Math.random()*10));
        message[4] = (int) (fake_y - (Math.random()*10));
        message[5] = (int) (fake_x - (Math.random()*10));
        message[6] = teamSecret;
        if (rc.canSubmitTransaction(message, cost))
            rc.submitTransaction(message, cost);
        return 1;
    }

    public int sendHqMssg(int enemySecret, int enemySecretPosition, int totalEnemyMessageLength) throws GameActionException {
        int cost = 5;

        int[] message = whatMessage(0, enemySecret, enemySecretPosition, totalEnemyMessageLength);

        if (rc.canSubmitTransaction(message, cost))
            rc.submitTransaction(message, cost);
        return 1;
    }

    public int sendDSMssg(int enemySecret, int enemySecretPosition, int totalEnemyMessageLength) throws GameActionException {
        int cost = 5;

        int[] message = whatMessage(1, enemySecret, enemySecretPosition, totalEnemyMessageLength);

        if (rc.canSubmitTransaction(message, cost))
            rc.submitTransaction(message, cost);
        return 1;
    }

    public int sendSoupMssg(int enemySecret, int enemySecretPosition, int totalEnemyMessageLength) throws GameActionException {
        int cost = 5;

        int[] message = whatMessage(2, enemySecret, enemySecretPosition, totalEnemyMessageLength);

        if (rc.canSubmitTransaction(message, cost))
            rc.submitTransaction(message, cost);
        return 1;
    }

    public int[] getMsgFromBlockchain() throws GameActionException {

        int[] EnemyCode = new int[3];
        EnemyCode[0] = 0;
        EnemyCode[1] = 444444444;

        for (int i = 1; i < rc.getRoundNum(); i++){
            for(Transaction tx : rc.getBlock(i)) {
                int[] mess = tx.getMessage();
                for(int j = 0; j < mess.length; j++) {
                    if(mess[j] > 63)
                    {
                        EnemyCode[0] = j;
                        EnemyCode[1] = mess[j];
                        EnemyCode[2] = mess.length;

                        return EnemyCode;
                    }
                }
            }
        }
        return EnemyCode;
    }

}