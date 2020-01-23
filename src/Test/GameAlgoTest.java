package Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import gameClient.Fruits;
import gameClient.GameAlgo;
import gameClient.Robots;


class GameAlgoTest {
	private static game_service _game = Game_Server.getServer(1);
	private static final DGraph dg1=new DGraph();
	private static final Graph_Algo ga=new Graph_Algo();
	private static final ArrayList<Fruits> f_list = new ArrayList<Fruits>();
	private static final ArrayList<Robots> r_list = new ArrayList<Robots>();
	private static final Robots r = new Robots();
	private static GameAlgo gameAlgo;

	/**
	 * this scenario has two fruits and one robot.
	 */
	@BeforeAll
	static void init(){
		dg1.init(_game.getGraph());
		ga.init(dg1);
		gameAlgo = new GameAlgo(ga);

		// add fruits to list
		Iterator<String> f_iter = _game.getFruits().iterator();
		while(f_iter.hasNext()) {
			f_list.add(new Fruits(f_iter.next()));
		}
		// add robot
		_game.addRobot(0);
		r.init(_game.getRobots().get(0));
		
		Iterator<String> r_iter = _game.getRobots().iterator();
		while(r_iter.hasNext()) {
			r_list.add(new Robots(r_iter.next()));
		}
	}



	/**
	 * checking if return the closest fruit in the list to a given robot
	 */
	@Test
	void ClosestFruitTest() {
		Fruits expected = f_list.get(0);
		Fruits actual = gameAlgo.ClosestFruitbyShortestpath(f_list, r);
		double expected_pathcost = ga.shortestPathDist(r.getSrc(), gameAlgo.nearestNode(expected)[0]);
		double actual_pathcost = ga.shortestPathDist(r.getSrc(), gameAlgo.nearestNode(actual)[0]);
		assertEquals(actual, expected, "ClosestFruitbyShortestpath test");
		assertEquals(actual_pathcost, expected_pathcost, "ClosestFruitbyShortestpath test");
	}

	/**
	 * checking if the return fruit has most value
	 */
	@Test
	void MostValueTest() {
		Fruits expected = f_list.get(1);
		Fruits actual = gameAlgo.mostValue(f_list);
		double expected_value = expected.getValue();
		double actual_value = actual.getValue();
		assertEquals(actual, expected, "MostValue test");
		if(actual_value != expected_value) {
			fail("should be same values, MostValue test");
		}
	}

	/**
	 * checking if return pair of int that represent src and dest of an edge that the fruit in on
	 */
	@Test
	void nearestNode() {
		int[] expected = {9,8};
		int[] actual = gameAlgo.nearestNode(f_list.get(0));
		assertEquals(actual[0], expected[0], "nearestNode test, fruit 1");
		assertEquals(actual[1], expected[1], "nearestNode test, fruit 1");
		int[] expected2 = {4,3};
		int[] actual2 = gameAlgo.nearestNode(f_list.get(1));
		assertEquals(actual2[0], expected2[0], "nearestNode test, fruit 2");
		assertEquals(actual2[1], expected2[1], "nearestNode test, fruit 2");

	}
}
