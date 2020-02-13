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
		//assertEquals(true,robot.senseNearbyRefinery());
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
		//assertEquals(false,robot.senseNearbyRefinery());
	}
}
