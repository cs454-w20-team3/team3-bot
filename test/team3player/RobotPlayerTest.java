package team3player;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.RobotInfo;
import battlecode.common.GameActionException;
import battlecode.common.Direction;

import javax.security.auth.callback.LanguageCallback;

class Clock {
	private static int mcount=0;
	public static void yield() {
		mcount++;
		return;
	}
	public static void reset() {
		mcount=0;
	}
	public static int count() {
		return mcount;
	}
}
public class RobotPlayerTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Test
	public void testSanity() {
		assertEquals(2, 1+1);
	}
	RobotController rc;
	@Test
	public void simpleRCtest() {
		rc = mock(RobotController.class);
		when(rc.getTeam()).thenReturn(Team.A);
        when(rc.getType()).thenReturn(RobotType.HQ); // TODO?!
        when(rc.getID()).thenReturn(0);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
		RobotFramework robot = new MinerRobot(rc);
		verify(rc).senseNearbyRobots(anyInt(), any(Team.class)); //without a second arguement defualts to called once
	}

	@Test
	public void canMoveSafetest_flooded()throws GameActionException {
		//when rc.canMove = true and rc.sensflooded is true the bot should not move
		rc = mock(RobotController.class);
		when(rc.getTeam()).thenReturn(Team.A);
        when(rc.getType()).thenReturn(RobotType.MINER); 
        when(rc.getID()).thenReturn(0);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
		when(rc.senseFlooding(any(MapLocation.class))).thenReturn(true);
		when(rc.canMove(any(Direction.class))).thenReturn(true);
		RobotFramework robot = new MinerRobot(rc);
		for (Direction dir : RobotFramework.directions) {
			assertEquals(false,robot.canMoveSafe(dir));
		}
	}
	@Test
	public void canMoveSafetest_not_flooded()throws GameActionException {
		//when rc.canmove = true and flooded = false canmovesafe should allow the bot to move
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
        when(rc.getType()).thenReturn(RobotType.MINER); 
		when(rc.getID()).thenReturn(0);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
		//test case specific
		when(rc.isReady()).thenReturn(true);
		when(rc.canMove(any(Direction.class))).thenReturn(true);
		when(rc.senseFlooding(any(MapLocation.class))).thenReturn(false);
		RobotFramework robot = new MinerRobot(rc);
		for (Direction dir : RobotFramework.directions) {
			assertEquals(true,robot.canMoveSafe(dir));
		}
	}
	@Test
	public void canMoveSafetest_not_flooded_not_ready()throws GameActionException {
		//when rc.canmove = false and flooded = true canmovesafe should be false
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
        when(rc.getType()).thenReturn(RobotType.MINER); 
		when(rc.getID()).thenReturn(0);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
		//test case specific
		when(rc.isReady()).thenReturn(true);
		when(rc.canMove(any(Direction.class))).thenReturn(false);
		when(rc.senseFlooding(any(MapLocation.class))).thenReturn(false);
		RobotFramework robot = new MinerRobot(rc);
		for (Direction dir : RobotFramework.directions) {
			assertEquals(false,robot.canMoveSafe(dir));
		}
	}
	@Test
	public void checkForRefinery_should_be_true()throws GameActionException {
		//when rc.canmove = false and flooded = true canmovesafe should be false
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
        when(rc.getType()).thenReturn(RobotType.MINER); 
		when(rc.getID()).thenReturn(0);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		//test case specific
		when(rc.isReady()).thenReturn(true);
		RobotInfo abot = mock(RobotInfo.class);
		when(abot.getType()).thenReturn(RobotType.REFINERY);
		RobotInfo[] senseResults = new RobotInfo[1];
		senseResults[0]=abot;
		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(senseResults);
		MinerRobot robot = new MinerRobot(rc);
		assertEquals(true,robot.checkForRefinery());
	}
	@Test
	public void checkForRefinery_should_be_false()throws GameActionException {
		//when rc.canmove = false and flooded = true canmovesafe should be false
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
        when(rc.getType()).thenReturn(RobotType.MINER); 
		when(rc.getID()).thenReturn(0);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
		//test case specific
		when(rc.isReady()).thenReturn(true);
		MinerRobot robot = new MinerRobot(rc);
		assertEquals(false,robot.checkForRefinery());
	}

	//CHAD
	@Test
	public void landscaperContstructorTest() {
		rc = mock(RobotController.class);
		when(rc.getTeam()).thenReturn(Team.A);
		when(rc.getType()).thenReturn(RobotType.LANDSCAPER);
		when(rc.getID()).thenReturn(0);
		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
		LandscaperRobot robot = new LandscaperRobot(rc);
		robot.idNearbyBots();
		assertNull(robot.hqLoc);
		assertNull(robot.baddyHqLoc);
		assertNotNull(robot.myLoc);
//		verify(rc).senseNearbyRobots(anyInt(), any(Team.class)); //without a second arguement defualts to called once
	}
	@Test
	public void idNearbyBots_should_be_TwoHQs()throws GameActionException {
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
		when(rc.getType()).thenReturn(RobotType.LANDSCAPER);
		when(rc.getID()).thenReturn(0);
		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.isReady()).thenReturn(true);
		//test case specific
		RobotInfo abotA = mock(RobotInfo.class);
		when(abotA.getType()).thenReturn(RobotType.HQ);
		when(abotA.getTeam()).thenReturn(Team.A);
		when(abotA.getLocation()).thenReturn(new MapLocation(1,1));
		RobotInfo abotB = mock(RobotInfo.class);
		when(abotB.getType()).thenReturn(RobotType.HQ);
		when(abotB.getTeam()).thenReturn(Team.B);
		when(abotB.getLocation()).thenReturn(new MapLocation(2,2));
		RobotInfo[] senseResults = new RobotInfo[]{abotA, abotB};
		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
		LandscaperRobot robot = new LandscaperRobot(rc);
//		when(robot.idNearbyBots()).thenCallRealMethod(robot.idNearbyBots());
//		robot.idNearbyBots();
		assertEquals(abotA.getLocation(),robot.hqLoc);
		assertEquals(abotB.getLocation(),robot.baddyHqLoc);
	}
	@Test
	public void seekHQ_found() throws GameActionException {
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
		when(rc.getType()).thenReturn(RobotType.LANDSCAPER);
		when(rc.getID()).thenReturn(0);
		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.isReady()).thenReturn(true);
		//test case specific
		RobotInfo abotA = mock(RobotInfo.class);
		when(abotA.getType()).thenReturn(RobotType.HQ);
		when(abotA.getTeam()).thenReturn(Team.A);
		when(abotA.getLocation()).thenReturn(new MapLocation(1,1));
		RobotInfo abotB = mock(RobotInfo.class);
		when(abotB.getType()).thenReturn(RobotType.HQ);
		when(abotB.getTeam()).thenReturn(Team.B);
		when(abotB.getLocation()).thenReturn(new MapLocation(2,2));
		RobotInfo[] senseResults = new RobotInfo[]{abotA, abotB};
		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
		LandscaperRobot robot = new LandscaperRobot(rc);
		assertEquals(true, robot.seekHQ());
	}
	@Test
	public void seekHQ_foundEventually() throws GameActionException {
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
		when(rc.getType()).thenReturn(RobotType.LANDSCAPER);
		when(rc.getID()).thenReturn(0);
		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.isReady()).thenReturn(true);
		when(rc.canMove(any(Direction.class))).thenReturn(true);
		when(rc.senseFlooding(any(MapLocation.class))).thenReturn(false);
		//test case specific
		RobotInfo abotA = mock(RobotInfo.class);
		when(abotA.getType()).thenReturn(RobotType.HQ);
		when(abotA.getTeam()).thenReturn(Team.A);
		when(abotA.getLocation()).thenReturn(new MapLocation(1,1));
		RobotInfo abotB = mock(RobotInfo.class);
		when(abotB.getType()).thenReturn(RobotType.HQ);
		when(abotB.getTeam()).thenReturn(Team.B);
		when(abotB.getLocation()).thenReturn(new MapLocation(2,2));
		RobotInfo[] senseResults = new RobotInfo[]{abotA, abotB};
		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(new RobotInfo[]{}, new RobotInfo[]{}, senseResults);
		LandscaperRobot robot = new LandscaperRobot(rc);
		assertEquals(true,robot.seekHQ());
		verify(rc,times(2)).move(any(Direction.class));
	}
	@Test
	public void fortifyHQ_near() throws GameActionException {
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
		when(rc.getType()).thenReturn(RobotType.LANDSCAPER);
		when(rc.getID()).thenReturn(0);
		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.isReady()).thenReturn(true);
		when(rc.canMove(any(Direction.class))).thenReturn(true);
		when(rc.senseFlooding(any(MapLocation.class))).thenReturn(false);
		//test case specific
		RobotInfo abotA = mock(RobotInfo.class);
		when(abotA.getType()).thenReturn(RobotType.HQ);
		when(abotA.getTeam()).thenReturn(Team.A);
		when(abotA.getLocation()).thenReturn(new MapLocation(1, 1));
		RobotInfo abotB = mock(RobotInfo.class);
		when(abotB.getType()).thenReturn(RobotType.HQ);
		when(abotB.getTeam()).thenReturn(Team.B);
		when(abotB.getLocation()).thenReturn(new MapLocation(2, 2));
		RobotInfo[] senseResults = new RobotInfo[]{abotA, abotB};
		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
		LandscaperRobot robot = new LandscaperRobot(rc);
		robot.fortifyHQ();
	}
	@Test
	public void fortifyHQ_far() throws GameActionException {
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
		when(rc.getType()).thenReturn(RobotType.LANDSCAPER);
		when(rc.getID()).thenReturn(0);
		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.isReady()).thenReturn(true);
		when(rc.canMove(any(Direction.class))).thenReturn(true);
		when(rc.senseFlooding(any(MapLocation.class))).thenReturn(false);
		//test case specific
		RobotInfo abotA = mock(RobotInfo.class);
		when(abotA.getType()).thenReturn(RobotType.HQ);
		when(abotA.getTeam()).thenReturn(Team.A);
		when(abotA.getLocation()).thenReturn(new MapLocation(51, 51));
		RobotInfo abotB = mock(RobotInfo.class);
		when(abotB.getType()).thenReturn(RobotType.HQ);
		when(abotB.getTeam()).thenReturn(Team.B);
		when(abotB.getLocation()).thenReturn(new MapLocation(52, 52));
		RobotInfo[] senseResults = new RobotInfo[]{abotA, abotB};
		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
		LandscaperRobot robot = new LandscaperRobot(rc);
		robot.fortifyHQ();
	}
	@Test
	public void fortifyHQ_mustSaunter() throws GameActionException {
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
		when(rc.getType()).thenReturn(RobotType.LANDSCAPER);
		when(rc.getID()).thenReturn(0);
		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.isReady()).thenReturn(true);
		when(rc.canMove(any(Direction.class))).thenReturn(false, true);
		when(rc.senseFlooding(any(MapLocation.class))).thenReturn(false);
		//test case specific
		RobotInfo abotA = mock(RobotInfo.class);
		when(abotA.getType()).thenReturn(RobotType.HQ);
		when(abotA.getTeam()).thenReturn(Team.A);
		when(abotA.getLocation()).thenReturn(new MapLocation(51, 51));
		RobotInfo abotB = mock(RobotInfo.class);
		when(abotB.getType()).thenReturn(RobotType.HQ);
		when(abotB.getTeam()).thenReturn(Team.B);
		when(abotB.getLocation()).thenReturn(new MapLocation(52, 52));
		RobotInfo[] senseResults = new RobotInfo[]{abotA, abotB};
		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
		LandscaperRobot robot = new LandscaperRobot(rc);
		robot.fortifyHQ();
//		assertEquals(true, robot.trySaunterSafe(any(Direction.class)));
//		verify(rc,times(3)).move(any(Direction.class));
	}
	public void goToLowSpot_test() throws GameActionException {
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
		when(rc.getType()).thenReturn(RobotType.LANDSCAPER);
		when(rc.getID()).thenReturn(0);
		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.isReady()).thenReturn(true);
		when(rc.canMove(any(Direction.class))).thenReturn(true);
		when(rc.senseFlooding(any(MapLocation.class))).thenReturn(false);
		//test case specific
		RobotInfo abotA = mock(RobotInfo.class);
		when(abotA.getType()).thenReturn(RobotType.HQ);
		when(abotA.getTeam()).thenReturn(Team.A);
		when(abotA.getLocation()).thenReturn(new MapLocation(20, 20));
		RobotInfo[] senseResults = new RobotInfo[]{abotA};
		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
		when(rc.senseElevation(any(MapLocation.class))).thenReturn(10);
		LandscaperRobot robot = new LandscaperRobot(rc);
		robot.goToLowPoint(any(Direction.class));
		verify(rc,times(3)).move(any(Direction.class));
	}
	@Test
	public void digDepMove_test() throws GameActionException {
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
		when(rc.getType()).thenReturn(RobotType.LANDSCAPER);
		when(rc.getID()).thenReturn(0);
		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.isReady()).thenReturn(true);
		when(rc.canMove(any(Direction.class))).thenReturn(true);
		when(rc.senseFlooding(any(MapLocation.class))).thenReturn(false);
		//test case specific
		RobotInfo abotA = mock(RobotInfo.class);
		when(abotA.getType()).thenReturn(RobotType.HQ);
		when(abotA.getTeam()).thenReturn(Team.A);
		when(abotA.getLocation()).thenReturn(new MapLocation(20, 20));
		RobotInfo[] senseResults = new RobotInfo[]{abotA};
		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
		when(rc.canDigDirt(any(Direction.class))).thenReturn(true);
		when(rc.canDepositDirt(any(Direction.class))).thenReturn(true);
		LandscaperRobot robot = new LandscaperRobot(rc);
//		robot.digDepMove(any(Direction.class),any(Direction.class),any(Direction.class));
		robot.digDepMove(Direction.NORTH,Direction.NORTH,Direction.NORTH);
	}
	@Test
	public void digDepMove_Saunter() throws GameActionException {
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
		when(rc.getType()).thenReturn(RobotType.LANDSCAPER);
		when(rc.getID()).thenReturn(0);
		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.isReady()).thenReturn(true);
		when(rc.canMove(any(Direction.class))).thenReturn(false,true);
		when(rc.senseFlooding(any(MapLocation.class))).thenReturn(false);
		//test case specific
		RobotInfo abotA = mock(RobotInfo.class);
		when(abotA.getType()).thenReturn(RobotType.HQ);
		when(abotA.getTeam()).thenReturn(Team.A);
		when(abotA.getLocation()).thenReturn(new MapLocation(20, 20));
		RobotInfo[] senseResults = new RobotInfo[]{abotA};
		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
		when(rc.canDigDirt(any(Direction.class))).thenReturn(true);
		when(rc.canDepositDirt(any(Direction.class))).thenReturn(true);
		LandscaperRobot robot = new LandscaperRobot(rc);
//		robot.digDepMove(any(Direction.class),any(Direction.class),any(Direction.class));
		robot.digDepMove(Direction.NORTH,Direction.NORTH,Direction.NORTH);
	}
	@Test
	public void getDepsitDir_test() throws GameActionException {
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
		when(rc.getType()).thenReturn(RobotType.LANDSCAPER);
		when(rc.getID()).thenReturn(0);
		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.isReady()).thenReturn(true);
//		when(rc.canMove(any(Direction.class))).thenReturn(false,true);
//		when(rc.senseFlooding(any(MapLocation.class))).thenReturn(false);
		//test case specific
		RobotInfo abotA = mock(RobotInfo.class);
		when(abotA.getType()).thenReturn(RobotType.HQ);
		when(abotA.getTeam()).thenReturn(Team.A);
		when(abotA.getLocation()).thenReturn(new MapLocation(20, 20));
		RobotInfo[] senseResults = new RobotInfo[]{abotA};
		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
		LandscaperRobot robot = new LandscaperRobot(rc);
		assertEquals(Direction.EAST, robot.getDepositDir(Direction.NORTH));
		assertEquals(Direction.EAST, robot.getDepositDir(Direction.NORTHEAST));
		assertEquals(Direction.SOUTH, robot.getDepositDir(Direction.EAST));
		assertEquals(Direction.SOUTH, robot.getDepositDir(Direction.SOUTHEAST));
		assertEquals(Direction.WEST, robot.getDepositDir(Direction.SOUTH));
		assertEquals(Direction.WEST, robot.getDepositDir(Direction.SOUTHWEST));
		assertEquals(Direction.NORTH, robot.getDepositDir(Direction.WEST));
		assertEquals(Direction.NORTH, robot.getDepositDir(Direction.NORTHWEST));
	}
	@Test
	public void getDestinationDir_test() throws GameActionException {
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
		when(rc.getType()).thenReturn(RobotType.LANDSCAPER);
		when(rc.getID()).thenReturn(0);
		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.isReady()).thenReturn(true);
//		when(rc.canMove(any(Direction.class))).thenReturn(false,true);
//		when(rc.senseFlooding(any(MapLocation.class))).thenReturn(false);
		//test case specific
		RobotInfo abotA = mock(RobotInfo.class);
		when(abotA.getType()).thenReturn(RobotType.HQ);
		when(abotA.getTeam()).thenReturn(Team.A);
		when(abotA.getLocation()).thenReturn(new MapLocation(20, 20));
		RobotInfo[] senseResults = new RobotInfo[]{abotA};
		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
		LandscaperRobot robot = new LandscaperRobot(rc);
		assertEquals(Direction.WEST, robot.getDestinationDir(Direction.NORTH));
		assertEquals(Direction.NORTH, robot.getDestinationDir(Direction.NORTHEAST));
		assertEquals(Direction.NORTH, robot.getDestinationDir(Direction.EAST));
		assertEquals(Direction.EAST, robot.getDestinationDir(Direction.SOUTHEAST));
		assertEquals(Direction.EAST, robot.getDestinationDir(Direction.SOUTH));
		assertEquals(Direction.SOUTH, robot.getDestinationDir(Direction.SOUTHWEST));
		assertEquals(Direction.SOUTH, robot.getDestinationDir(Direction.WEST));
		assertEquals(Direction.WEST, robot.getDestinationDir(Direction.NORTHWEST));
	}
	@Test
	public void test_DS_constructor() throws GameActionException {
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
		when(rc.getType()).thenReturn(RobotType.DESIGN_SCHOOL);
		when(rc.getID()).thenReturn(0);
		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.isReady()).thenReturn(true);
		//test case specific
		RobotInfo abotA = mock(RobotInfo.class);
		when(abotA.getType()).thenReturn(RobotType.HQ);
		when(abotA.getTeam()).thenReturn(Team.A);
		when(abotA.getLocation()).thenReturn(new MapLocation(1,1));
		RobotInfo[] senseResults = new RobotInfo[]{abotA};
		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
		DesignSchoolRobot robot = new DesignSchoolRobot(rc);
		assertEquals(abotA.getLocation(),robot.hqLoc);
	}
	@Test
	public void test_DS_myTurn_HQ() throws GameActionException {
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
		when(rc.getType()).thenReturn(RobotType.DESIGN_SCHOOL);
		when(rc.getID()).thenReturn(0);
		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.isReady()).thenReturn(true);
		//test case specific
		RobotInfo abotA = mock(RobotInfo.class);
		when(abotA.getType()).thenReturn(RobotType.HQ);
		when(abotA.getTeam()).thenReturn(Team.A);
		when(abotA.getLocation()).thenReturn(new MapLocation(20,20));
		RobotInfo[] senseResults = new RobotInfo[]{abotA};
		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
		when(rc.canBuildRobot(any(RobotType.class),any(Direction.class))).thenReturn(false,false, false, false, false, true);
		DesignSchoolRobot robot = new DesignSchoolRobot(rc);
		robot.myTurn();
		verify(rc, atMost(5)).buildRobot(any(RobotType.class), any(Direction.class));
	}
	@Test
	public void test_DS_myTurn_noHQ() throws GameActionException {
		rc = mock(RobotController.class);
		//basic mock setup
		when(rc.getTeam()).thenReturn(Team.A);
		when(rc.getType()).thenReturn(RobotType.DESIGN_SCHOOL);
		when(rc.getID()).thenReturn(0);
		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.isReady()).thenReturn(true);
		//test case specific
		RobotInfo abotA = mock(RobotInfo.class);
		when(abotA.getType()).thenReturn(RobotType.HQ);
		when(abotA.getTeam()).thenReturn(Team.A);
		when(abotA.getLocation()).thenReturn(new MapLocation(20,20));
		RobotInfo[] senseResults = new RobotInfo[]{abotA};
		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(new RobotInfo[]{});
		when(rc.canBuildRobot(any(RobotType.class),any(Direction.class))).thenReturn(false,false, false, false, false, true);
		DesignSchoolRobot robot = new DesignSchoolRobot(rc);
		robot.myTurn();
		verify(rc, atMost(5)).buildRobot(any(RobotType.class), any(Direction.class));
	}














	//MI YON
	//@Test
	// public void Miner_myTurn()throws GameActionException  {
		//this test checks that the Fullfillment center doesn't do anything during construction
		//rc = mock(RobotController.class);
		//when(rc.getTeam()).thenReturn(Team.A);
//		when(rc.getType()).thenReturn(RobotType.HQ);
//		when(rc.getID()).thenReturn(0);
//		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
//		when(rc.getRoundNum()).thenReturn(0);
//		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
//		when(rc.isReady()).thenReturn(true);
//		MinerRobot robot = new MinerRobot(rc);
//		robot.myType = MinerRobot.MinerType.GATHERER;
//		MapLocation[] locResults = {new MapLocation(0,0),new MapLocation(1,1)};
//		when(rc.senseNearbySoup()).thenReturn(locResults);
//		when(rc.getSoupCarrying()).thenReturn(RobotType.MINER.soupLimit);
//		robot.myTurn();
//		verify(rc, atMost(8)).buildRobot(any(RobotType.class), any(Direction.class));
//	}
//	@Test
//	public void tryMineTest_should_be_false()throws GameActionException {
//		rc = mock(RobotController.class);
//		//basic mock setup
//		when(rc.getTeam()).thenReturn(Team.A);
//		when(rc.getType()).thenReturn(RobotType.MINER);
//		when(rc.getID()).thenReturn(0);
//		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
//		when(rc.getRoundNum()).thenReturn(0);
//		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
//		//test case specific
//		when(rc.isReady()).thenReturn(true);
//		when(rc.canMineSoup(Direction.CENTER)).thenReturn(false);
//		MinerRobot robot = new MinerRobot(rc);
//		assertEquals(false,robot.tryMine(Direction.CENTER));
//	}
//
//	@Test
//	public void tryMineTest_should_be_true()throws GameActionException {
//		rc = mock(RobotController.class);
//		//basic mock setup
//		when(rc.getTeam()).thenReturn(Team.A);
//		when(rc.getType()).thenReturn(RobotType.MINER);
//		when(rc.getID()).thenReturn(0);
//		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
//		when(rc.getRoundNum()).thenReturn(0);
//		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
//		//test case specific
//		when(rc.isReady()).thenReturn(true);
//		when(rc.canMineSoup(Direction.CENTER)).thenReturn(true);
//		MinerRobot robot = new MinerRobot(rc);
//		assertEquals(true,robot.tryMine(Direction.CENTER));
//	}
//
//	@Test
//	public void getSoupTest_mine()throws GameActionException {
//		rc = mock(RobotController.class);
//		//basic mock setup
//		when(rc.getTeam()).thenReturn(Team.A);
//		when(rc.getType()).thenReturn(RobotType.MINER);
//		when(rc.getID()).thenReturn(0);
//		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
//		when(rc.getRoundNum()).thenReturn(0);
//		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
//		//test case specific
//		when(rc.getSoupCarrying()).thenReturn(RobotType.MINER.soupLimit-1);
//		when(rc.isReady()).thenReturn(true);
//		when(rc.getTeamSoup()).thenReturn(200);
//		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
//		when(rc.canMineSoup(Direction.CENTER)).thenReturn(true);
//		when(rc.getSoupCarrying()).thenReturn(RobotType.MINER.soupLimit);
//		MinerRobot robot = new MinerRobot(rc);
//		robot.getSoup();
//	}
//
//	@Test
//	public void getSoupTest_notMine()throws GameActionException {
//		rc = mock(RobotController.class);
//		//basic mock setup
//		when(rc.getTeam()).thenReturn(Team.A);
//		when(rc.getType()).thenReturn(RobotType.MINER);
//		when(rc.getID()).thenReturn(0);
//		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
//		when(rc.getRoundNum()).thenReturn(0);
//		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
//		//test case specific
//		when(rc.isReady()).thenReturn(true);
//		when(rc.getSoupCarrying()).thenReturn(RobotType.MINER.soupLimit);
//
//		MinerRobot robot = new MinerRobot(rc);
//		robot.getSoup();
//	}
//	@Test
//	public void goToHQTest()throws GameActionException {
//		rc = mock(RobotController.class);
//		//basic mock setup
//		when(rc.getTeam()).thenReturn(Team.A);
//		when(rc.getType()).thenReturn(RobotType.MINER);
//		when(rc.getID()).thenReturn(0);
//		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
//		when(rc.getRoundNum()).thenReturn(0);
//		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
//		//test case specific
//		when(rc.isReady()).thenReturn(true);
//		when(rc.canMove(any(Direction.class))).thenReturn(true);
//		when(rc.senseFlooding(any(MapLocation.class))).thenReturn(false);
//		when(rc.canDepositSoup(Direction.CENTER)).thenReturn(true);
//		RobotInfo abotA = mock(RobotInfo.class);
//		when(abotA.getType()).thenReturn(RobotType.HQ);
//		when(abotA.getTeam()).thenReturn(Team.A);
//		when(abotA.getLocation()).thenReturn(new MapLocation(51, 51));
//		RobotInfo[] senseResults = new RobotInfo[]{abotA};
//		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
//		MinerRobot robot = new MinerRobot(rc);
//		robot.goToHQ();
//	}
//	@Test
//	public void goToRefineryTest()throws GameActionException {
//		rc = mock(RobotController.class);
//		//basic mock setup
//		when(rc.getTeam()).thenReturn(Team.A);
//		when(rc.getType()).thenReturn(RobotType.MINER);
//		when(rc.getID()).thenReturn(0);
//		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
//		when(rc.getRoundNum()).thenReturn(0);
//		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
//		//test case specific
//		when(rc.isReady()).thenReturn(true);
//		when(rc.canMove(any(Direction.class))).thenReturn(true);
//		when(rc.senseFlooding(any(MapLocation.class))).thenReturn(false);
//		when(rc.canDepositSoup(Direction.CENTER)).thenReturn(false);
//		RobotInfo abotA = mock(RobotInfo.class);
//		when(abotA.getType()).thenReturn(RobotType.REFINERY);
//		when(abotA.getTeam()).thenReturn(Team.A);
//		when(abotA.getLocation()).thenReturn(new MapLocation(51, 51));
//		RobotInfo[] senseResults = new RobotInfo[]{abotA};
//		when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
//		MinerRobot robot = new MinerRobot(rc);
//		robot.goToRefinery(new MapLocation(51, 51));
//	}
//	@Test
//	public void lookForSoup_should_be_false()throws GameActionException {
//		rc = mock(RobotController.class);
//		//basic mock setup
//		when(rc.getTeam()).thenReturn(Team.A);
//		when(rc.getType()).thenReturn(RobotType.MINER);
//		when(rc.getID()).thenReturn(0);
//		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
//		when(rc.getRoundNum()).thenReturn(0);
//		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
//		//test case specific
//		when(rc.isReady()).thenReturn(true);
//		MinerRobot robot = new MinerRobot(rc);
//		when(rc.senseNearbySoup()).thenReturn(new MapLocation[]{});
//		assertEquals(false,robot.lookForSoup());
//	}
//	@Test
//	public void lookForSoup_should_be_true()throws GameActionException {
//		rc = mock(RobotController.class);
//		//basic mock setup
//		when(rc.getTeam()).thenReturn(Team.A);
//		when(rc.getType()).thenReturn(RobotType.MINER);
//		when(rc.getID()).thenReturn(0);
//		when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
//		when(rc.getRoundNum()).thenReturn(0);
//		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
//		//test case specific
//		when(rc.isReady()).thenReturn(true);
//		MapLocation[] locResults = {new MapLocation(0,0),new MapLocation(1,1)};
//		MinerRobot robot = new MinerRobot(rc);
//		when(rc.senseNearbySoup()).thenReturn(locResults);
//		assertEquals(true,robot.lookForSoup());
//	}
	//AVIN

	//KYLE
	@Test
	public void FCconstructorTest() {
		//this test checks that the Fullfillment center doesn't do anything during construction
		rc = mock(RobotController.class);
		when(rc.getTeam()).thenReturn(Team.A);
        when(rc.getType()).thenReturn(RobotType.HQ); // TODO?!
        when(rc.getID()).thenReturn(0);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
		RobotFramework robot = new FCRobot(rc);
		verify(rc, never()).resign(); //without a second arguement defualts to called once
	}
	@Test
	public void FCmyTurn()throws GameActionException  {
		//this test checks that the Fullfillment center doesn't do anything during construction
		rc = mock(RobotController.class);
		when(rc.getTeam()).thenReturn(Team.A);
        when(rc.getType()).thenReturn(RobotType.HQ);
        when(rc.getID()).thenReturn(0);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
		RobotFramework robot = new FCRobot(rc);
		robot.myTurn();
		verify(rc, atMost(8)).buildRobot(any(RobotType.class), any(Direction.class));
	}
	@Test
	public void isSoupNextToMe_true()throws GameActionException   {
			MapLocation myLoc = new MapLocation(0,0);
			MapLocation soupLoc = new MapLocation(0,1);
			assert(myLoc.isAdjacentTo(soupLoc));
			rc = mock(RobotController.class);
			when(rc.getTeam()).thenReturn(Team.A);
			when(rc.getType()).thenReturn(RobotType.HQ);
			when(rc.getID()).thenReturn(0);
			when(rc.getLocation()).thenReturn(myLoc);
			when(rc.getRoundNum()).thenReturn(0);
			when(rc.senseSoup(any(MapLocation.class))).thenReturn(1);
			//lets simulate soup being next to the robot
			// RobotInfo[] senseResults = new RobotInfo[]{abotA};
			RobotInfo abotA = mock(RobotInfo.class);
			when(abotA.getType()).thenReturn(RobotType.HQ);
			when(abotA.getTeam()).thenReturn(Team.A);
			when(abotA.getLocation()).thenReturn(new MapLocation(20, 20));
			RobotInfo[] senseResults = new RobotInfo[]{abotA};
			MapLocation[] results  = new MapLocation[1];
			results[0] = soupLoc;
			when(rc.senseNearbySoup(anyInt())).thenReturn(results);
			when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
			MinerRobot robot = new MinerRobot(rc);
			assertEquals(true,robot.isSoupNextToMe());
	}

	@Test
	public void isSoupNextToMe_false()throws GameActionException   {
			MapLocation myLoc = new MapLocation(0,0);
			rc = mock(RobotController.class);
			when(rc.getTeam()).thenReturn(Team.A);
			when(rc.getType()).thenReturn(RobotType.HQ);
			when(rc.getID()).thenReturn(0);
			when(rc.getLocation()).thenReturn(myLoc);
			when(rc.getRoundNum()).thenReturn(0);
			when(rc.senseSoup(any(MapLocation.class))).thenReturn(0);
			//lets simulate soup being next to the robot
			// RobotInfo[] senseResults = new RobotInfo[]{abotA};
			RobotInfo abotA = mock(RobotInfo.class);
			when(abotA.getType()).thenReturn(RobotType.HQ);
			when(abotA.getTeam()).thenReturn(Team.A);
			when(abotA.getLocation()).thenReturn(new MapLocation(20, 20));
			RobotInfo[] senseResults = new RobotInfo[]{abotA};
			when(rc.senseNearbySoup(anyInt())).thenReturn(new MapLocation[]{});
			when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
			MinerRobot robot = new MinerRobot(rc);
			assertEquals(false,robot.isSoupNextToMe());
	}
	@Test
	public void mineAdjacentSoup_test()throws GameActionException   {
			MapLocation myLoc = new MapLocation(0,0);
			rc = mock(RobotController.class);
			when(rc.getTeam()).thenReturn(Team.A);
			when(rc.getType()).thenReturn(RobotType.HQ);
			when(rc.getID()).thenReturn(0);
			when(rc.getLocation()).thenReturn(myLoc);
			when(rc.getRoundNum()).thenReturn(0);
			when(rc.isReady()).thenReturn(true);
			when(rc.canMineSoup(any(Direction.class))).thenReturn(true);
			when(rc.senseSoup(any(MapLocation.class))).thenReturn(1,1,0);
			//lets simulate soup being next to the robot
			// RobotInfo[] senseResults = new RobotInfo[]{abotA};
			RobotInfo abotA = mock(RobotInfo.class);
			when(abotA.getType()).thenReturn(RobotType.HQ);
			when(abotA.getTeam()).thenReturn(Team.A);
			when(abotA.getLocation()).thenReturn(new MapLocation(20, 20));
			RobotInfo[] senseResults = new RobotInfo[]{abotA};
			when(rc.senseNearbySoup(anyInt())).thenReturn(new MapLocation[]{});
			when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
			MinerRobot robot = new MinerRobot(rc);
			robot.mineAdjacentSoup();
			// verify(rc).senseNearbyRobots(anyInt(), any(Team.class));
			verify(rc, times(2)).mineSoup(any(Direction.class));
	}
	@Test
	public void minerCanMoveSafe_selfdestruct()throws GameActionException   {
			System.out.println("start of destruct test");
			MapLocation myLoc = new MapLocation(0,0);
			MapLocation hqLoc = new MapLocation(1,0);
			assert(myLoc.isAdjacentTo(hqLoc));
			rc = mock(RobotController.class);
			when(rc.getTeam()).thenReturn(Team.A);
			when(rc.getType()).thenReturn(RobotType.HQ);
			when(rc.getID()).thenReturn(0);
			when(rc.getLocation()).thenReturn(myLoc);
			when(rc.getRoundNum()).thenReturn(0,0,12);
			when(rc.isReady()).thenReturn(true);
			RobotInfo abotA = mock(RobotInfo.class);
			when(abotA.getType()).thenReturn(RobotType.HQ);
			when(abotA.getTeam()).thenReturn(Team.A);
			when(abotA.getLocation()).thenReturn(hqLoc);
			RobotInfo abotB = mock(RobotInfo.class);
			when(abotB.getType()).thenReturn(RobotType.REFINERY);
			when(abotB.getTeam()).thenReturn(Team.A);
			when(abotB.getLocation()).thenReturn(hqLoc);
			RobotInfo abotC = mock(RobotInfo.class);
			when(abotC.getType()).thenReturn(RobotType.FULFILLMENT_CENTER);
			when(abotC.getTeam()).thenReturn(Team.A);
			when(abotC.getLocation()).thenReturn(hqLoc);
			RobotInfo[] senseResults = new RobotInfo[]{abotA};
			when(rc.senseNearbyRobots(-1, Team.A)).thenReturn(senseResults);
			MinerRobot robot = new MinerRobot(rc);
			assert(!robot.tryMoveSafe(Direction.NORTH));
			assert(!robot.tryMoveSafe(Direction.NORTH));
			assert(!robot.tryMoveSafe(Direction.NORTH));
			assert(!robot.tryMoveSafe(Direction.NORTH));
			verify(rc).disintegrate();
	}
	@Test
	public void HQconstructorTest() {
		//this test checks that the Fullfillment center doesn't do anything during construction
		rc = mock(RobotController.class);
		when(rc.isReady()).thenReturn(true);
		when(rc.getTeam()).thenReturn(Team.A);
        when(rc.getType()).thenReturn(RobotType.HQ); // TODO?!
        when(rc.getID()).thenReturn(0);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
		RobotFramework robot = new HQRobot(rc);
		verify(rc, never()).resign(); //without a second arguement defualts to called once
	}
	@Test
	public void HQmyTurn()throws GameActionException  {
		//this test checks that the Fullfillment center doesn't do anything during construction
		rc = mock(RobotController.class);
		when(rc.isReady()).thenReturn(true);
		when(rc.getTeam()).thenReturn(Team.A);
        when(rc.getType()).thenReturn(RobotType.HQ);
        when(rc.getID()).thenReturn(0);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
		when(rc.getRoundNum()).thenReturn(0);
		when(rc.senseNearbyRobots(anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
		RobotFramework robot = new HQRobot(rc);
		robot.myTurn();
		verify(rc, atMost(8)).buildRobot(any(RobotType.class), any(Direction.class));
	}
}
