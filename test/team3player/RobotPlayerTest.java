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
	public void senseRefinery_should_be_true()throws GameActionException {
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
		assertEquals(true,robot.senseNearbyRefinery());
	}
	@Test
	public void senseRefinery_should_be_false()throws GameActionException {
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
		assertEquals(false,robot.senseNearbyRefinery());
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


	//MI YON
	@Test
	public void lookForSoup_should_be_false()throws GameActionException {
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
		when(rc.senseNearbySoup()).thenReturn(new MapLocation[]{});
		assertEquals(false,robot.lookForSoup());
	}
	@Test
	public void lookForSoup_should_be_true()throws GameActionException {
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
		MapLocation aloc = new MapLocation(0,0);
		MapLocation[] locResults = {new MapLocation(0,0),new MapLocation(1,1)};
		locResults[0]=aloc;
		MinerRobot robot = new MinerRobot(rc);
		when(rc.senseNearbySoup()).thenReturn(locResults);
		assertEquals(true,robot.lookForSoup());
	}
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
}
