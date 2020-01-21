package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Server.Game_Server;
import Server.game_service;

import gameClient.Robots;

class RobotsTest {
	private game_service _game = Game_Server.getServer(0);
	private Robots _r = new Robots();


	@Test
	void initTest() {
		_game.addRobot(0);
		_r.init(_game.getRobots().get(0));
		String expected[] = {"0","0.0","0","-1","1.0", "35.18753053591606,32.10378225882353,0.0"};
		String actual_id = _r.getId()+"";
		String actual_value = _r.getValue()+"";
		String actual_src = _r.getSrc()+"";
		String actual_dest = _r.getDest()+"";
		String actual_speed = _r.getSpeed()+"";
		String actual_pos = _r.getPosX()+","+_r.getPosY()+",0.0";
		assertEquals(actual_id, expected[0], "id test");
		assertEquals(actual_value, expected[1], "value test");
		assertEquals(actual_src, expected[2], "src test");
		assertEquals(actual_dest, expected[3], "dest test");
		assertEquals(actual_speed, expected[4], "speed test");
		assertEquals(actual_pos, expected[5], "pos test");
		
	}

}
