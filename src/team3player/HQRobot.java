package team3player;
import battlecode.common.*;
class HQRobot extends RobotFramework {
    int numOfMiners = 0;
    final int maxNumOfMiners = 10;
    int enemySecretPosition = 0;
    int enemySecret = 444444444;
    int totalEnemyMessageLength = 6;
    int[] EnemyCode;

    HQRobot(RobotController rc_) {
        //super(rc_) calls the constructor of the parent class which just saves rc
        //the parent class also has the old utility functions like tryMove which need rc
        super(rc_);

    }
    public void myTurn() throws GameActionException {
        waitforcooldown();
        if(enemySecret == -1) {
            EnemyCode = getMsgFromBlockchain();
            enemySecretPosition = EnemyCode[0];
            enemySecret = EnemyCode[1];
            totalEnemyMessageLength = EnemyCode[2];

            if (enemySecretPosition == totalEnemyMessageLength)
                enemySecretPosition = 0;

            System.out.println("enemySecret: " + enemySecret);
        }
      else {
            if( rc.getRoundNum() > 40 && rc.getRoundNum() < 140){
                //sendHqMssg(enemySecret);
                for(int i = 0; i < 3; i++)
                    sendDSMssg(enemySecret, enemySecretPosition, totalEnemyMessageLength);
            }
            if( rc.getRoundNum() > 5 && rc.getRoundNum() < 20){
                //sendHqMssg(enemySecret);
                for(int i = 0; i < 7; i++)
                    sendSoupMssg(enemySecret, enemySecretPosition, totalEnemyMessageLength);
            }
        }
//        else {
//            for(int i = 0; i < 7; i++)
//                sendHqMssg(enemySecret);
//        }
        for (RobotInfo bot : rc.senseNearbyRobots(-1, rc.getTeam().opponent())) {
            if (rc.canShootUnit(bot.ID)) {
                rc.shootUnit(bot.ID);
            }
        }
        if (affordMiner()) {
            buildMiner();
        }
    }
    public boolean affordMiner() {
        //the coefficient multiplies the amout of soup the team needs to have saved up per miner to build another miner
        //so increasing the coefficient means the HQ builds fewer miners, decreasing the coefficient means more miners are built
        //I'm sure this number will get tweaked alot especial once we start getting a wall built.
        //a reasonable range for the coefficient is like [0.3 - 2]
        double coefficient = 2;
        //miners cost 70 units of soup to make
        final int minerCost = 70;
        // System.out.println("HQ bank level: " + numOfMiners * minerCost * coefficient);
        if (rc.getTeamSoup() >= numOfMiners * minerCost * coefficient) {
            return true;
        } else {
            return false;
        }
    }
    public void buildMiner()throws GameActionException {
        for (Direction dir : directions) {
            if (numOfMiners < maxNumOfMiners) {
                if (tryBuild(RobotType.MINER, dir))
                numOfMiners++;
            }
        }
    }

}