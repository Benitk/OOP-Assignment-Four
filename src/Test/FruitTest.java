package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Server.Game_Server;
import Server.game_service;
import gameClient.Fruits;

class FruitTest {
	private game_service _game = Game_Server.getServer(0);
	private Fruits _f = new Fruits();

	
	@Test
	void initTest() {
		_f.init(_game.getFruits().get(0));
		String expected[] = {"5.0","-1", "35.197656770719604,32.10191878639921,0.0"};
		String actual_value = _f.getValue()+"";
		String actual_type = _f.getType()+"";
		String actual_pos = _f.getPosX()+","+_f.getPosY()+",0.0";
		assertEquals(actual_value, expected[0], "value test");
		assertEquals(actual_type, expected[1], "type test");
		assertEquals(actual_pos, expected[2], "pos test");
		
	}

}
