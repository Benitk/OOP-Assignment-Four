package gameClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import gameClient.Fruits;

import dataStructure.node_data;

public class AutomaticPlayer {

	public AutomaticPlayer(MyGameGUI gui) {
		setGUI(gui);
		setGameAlgo(new GameAlgo(getGUI().getGraphAlgo()));
		init();
	}

	private void init() {
		RobotsAutoPosition();
	}

	/**
	 * reading data from the server game, init the fruits and robots to the new
	 * values each call of this function in case the robot has no dest - aka (-1)
	 * send it to ClosestNodeAuto/nextValueAuto to create a new path to the robot at
	 * the same time send placemark data to the kml_logger class
	 */
	public void moveRobotsAuto() {
		// update fruit

		int[] fruitLocation = new int[2];
		int robNodeList_size = 0;
		List<String> log = getGUI().getGame().move();

		// init fruits
		List<String> fruits = getGUI().getGame().getFruits();
		for (int i = 0; i < fruits.size(); i++) {
			getGUI().getFruitList().get(i).init(fruits.get(i));
			Fruits f = getGUI().getFruitList().get(i);
			int type = getGUI().getFruitList().get(i).getType();

			// kml fruit placemark
			if (type == -1) {
				getGUI().getKml().Placemark(5, f.getPosX(), f.getPosY(), getGUI().getKml().currentTime());
			} else {
				getGUI().getKml().Placemark(6, f.getPosX(), f.getPosY(), getGUI().getKml().currentTime());
			}
		}
		if (log != null) {
			//init robots
			for (int i = 0; i < log.size(); i++) {
				Robots r = getGUI().getRobList().get(i);
				r.init(log.get(i));

				// kml robot placemark
				getGUI().getKml().Placemark(r.getId(), r.getPosX(), r.getPosY(), getGUI().getKml().currentTime());
				if (r.getDest() == -1 && r.getNextDest().isEmpty()) {
					if(r.getTarget() != null) {
						r.getTarget().setIsTarget(false);
						r.setTarget(null);

					}
					if(r.getSpeed() < 3 && getGUI().getRobList().size() < 2) {
						getGUI().getGame().chooseNextEdge(i, nextValueAuto(r.getSrc(), i));
					}
					else {
						getGUI().getGame().chooseNextEdge(i, ClosestNodeAuto(r.getSrc(), i));
					}
				}
				// r next list isnt empty
				else if(r.getDest() == -1 && r.getTarget() != null) {
					fruitLocation = getGameAlgo().nearestNode(r.getTarget());
					robNodeList_size = r.getNextDest().size();

					// fruit change is place or other robot took it clear nextDest list
					if(r.getNextDest().get(robNodeList_size - 1).getKey() != fruitLocation[1] || r.getNextDest().get(robNodeList_size - 1).getKey() != fruitLocation[0]) {
						r.getTarget().setIsTarget(false);
						r.setTarget(null);
						r.getNextDest().clear();
					}
					else {
						getGUI().getGame().chooseNextEdge(i, r.getNextDest().get(0).getKey());
						r.getNextDest().remove(0);
					}
				}
			}
		}
	}

	/**
	 * using ClosestFruitbyShortestpath function to get the closest fruit then using
	 * shortpath to get the list of nodes in the route to the fruit and insert it to
	 * the robot nextDest list
	 * 
	 * @param rob_id - robot id
	 * @param src    - robot current src node
	 * @return next destnation to the robot
	 */
	private int ClosestNodeAuto(int src, int rob_id) {
		int ans[] = new int[2];
		List<node_data> nextdest = new ArrayList<node_data>();
		Fruits f = getGameAlgo().ClosestFruitbyShortestpath(getGUI().getFruitList(), getGUI().getRobList().get(rob_id));
		if (f != null){
			ans = getGameAlgo().nearestNode(f);
			if (!(src == ans[0])) {
				nextdest = getGUI().getGraphAlgo().shortestPath(src, ans[0]);
				// remove src
				nextdest.remove(0);

				getGUI().getRobList().get(rob_id).getNextDest().addAll(nextdest);
				getGUI().getRobList().get(rob_id).getNextDest().add(getGUI().getGraphAlgo().get_Dgraph().getNode(ans[1]));
				getGUI().getRobList().get(rob_id).setTarget(f);
				f.setIsTarget(true);
				return getGUI().getRobList().get(rob_id).getNextDest().get(0).getKey();

				// found and diffrent from src
			} else if(ans[0] != -1){
				getGUI().getRobList().get(rob_id).setTarget(f);
				f.setIsTarget(true);
				return ans[1];
			}
		}
		return -1;
	}

	/**
	 * using mostValue function to get the most valuable fruit then using shortpath
	 * to get the list of nodes in the route to the fruit and insert it to the robot
	 * nextDest list
	 * 
	 * @param rob_id - robot id
	 * @param src    - robot current src node
	 * @return next destnation to the robot
	 */
	private int nextValueAuto(int src, int rob_id) {
		int ans[] = new int[2];

		List<node_data> nextdest = new ArrayList<node_data>();
		Fruits f = getGameAlgo().mostValue(getGUI().getFruitList());
		if (f != null){

			ans = getGameAlgo().nearestNode(f);
			if (!(src == ans[0])) {
				nextdest = getGUI().getGraphAlgo().shortestPath(src, ans[0]);
				// remove src
				nextdest.remove(0);

				getGUI().getRobList().get(rob_id).getNextDest().addAll(nextdest);
				getGUI().getRobList().get(rob_id).getNextDest().add(getGUI().getGraphAlgo().get_Dgraph().getNode(ans[1]));
				getGUI().getRobList().get(rob_id).setTarget(f);
				f.setIsTarget(true);
				return getGUI().getRobList().get(rob_id).getNextDest().get(0).getKey();

				// found and diffrent from src
			} else if(ans[0] != -1){
				getGUI().getRobList().get(rob_id).setTarget(f);
				f.setIsTarget(true);
				return ans[1];
			}

		}
		return -1;
	}


	/**
	 * init the robot list in the gui class to the number of given robots from the
	 * server placing the robots before the game start each robot place near the
	 * most value fruit if there is few robots then they are placed in other fruits
	 * by most value order
	 */
	private void RobotsAutoPosition() {
		JSONObject GameJson;
		try {
			GameJson = new JSONObject(getGUI().getGame().toString()).getJSONObject("GameServer");
			int Robot_num = GameJson.getInt("robots");
			int[] nextNode = new int[2];

			getGUI().setRobList(new ArrayList<Robots>(Robot_num));
			if (Robot_num == 1) { 
				Fruits f = getGameAlgo().mostValue(getGUI().getFruitList());
				nextNode = getGameAlgo().nearestNode(f);
				getGUI().getGame().addRobot(nextNode[0]); 
			} else { 
				boolean found = false;
				for(int i = 0; i < Robot_num; i++) {
					Fruits f = getGameAlgo().mostValue(getGUI().getFruitList());
					if (f != null) { 
						nextNode = getGameAlgo().nearestNode(f);
						if(nextNode[0] != -1) { 
							getGUI().getGame().addRobot(nextNode[0]);
							f.setIsTarget(true);
							found = true;
						} 
					} // didnt found fruit in current robot area 
					if(!found) { 
						int bound = (getGUI().getGraphAlgo().get_Dgraph().nodeSize() + 1) / Robot_num; 
						// random middle starting point 
						getGUI().getGame().addRobot(bound / 2); 
					}
				}
			}


		} catch (JSONException e) {
			e.printStackTrace();
		}
		// set fruits targets to false
		Iterator<Fruits> f_iter = getGUI().getFruitList().iterator();
		while(f_iter.hasNext()) {
			f_iter.next().setIsTarget(false);
		}

		// adding robots
		Iterator<String> r_iter = getGUI().getGame().getRobots().iterator();
		while (r_iter.hasNext()) {
			String s = r_iter.next();
			Robots r = new Robots(s);
			getGUI().getRobList().add(r);
			r.setNextDest(new ArrayList<node_data>());
		}
	}

	/*** private data ****/

	private MyGameGUI _gui;
	private GameAlgo _gameAlgo;

	/*** getters/setters ***/
	public MyGameGUI getGUI() {
		return _gui;
	}

	private void setGUI(MyGameGUI _gui) {
		this._gui = _gui;
	}

	public GameAlgo getGameAlgo() {
		return _gameAlgo;
	}

	private void setGameAlgo(GameAlgo _gameAlgo) {
		this._gameAlgo = _gameAlgo;
	}
}
