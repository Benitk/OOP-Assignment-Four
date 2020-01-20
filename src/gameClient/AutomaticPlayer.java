package gameClient;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import gameClient.Fruits;
import utils.Point3D;
import dataStructure.edgeData;
import dataStructure.edge_data;
import dataStructure.nodeData;
import dataStructure.node_data;

public class AutomaticPlayer {


	public AutomaticPlayer(MyGameGUI gui) {
		setGUI(gui);
		init();
	}

	private void init() {
		RobotsAutoPosition();
	}

	
	
	/**
	 * using shortPathdisc function to determine what is the closest fruit in the list
	 * to the given robot using nearestNode() to convert fruit position to node keys
	 * @param list - fruit list
	 * @param r - robots 
	 * @return fruit - the closest to the current robot
	 */
	public Fruits ClosestFruitbyShortestpath(ArrayList<Fruits> list, Robots r) {
		if(list.isEmpty()) {
			return null;
		}
		int[] closestNode = new int[2];
		int[] FruitsNode = new int[2];
		Fruits closest = list.get(0);
		for(int i = 1 ; i < list.size(); i++) {
			if(!(list.get(i).isTarget())) {
				closestNode = nearestNode(closest);
				FruitsNode = nearestNode(list.get(i));
				if(closestNode[0] == r.getSrc()) {
					closestNode[0] = closestNode[1];
				}
				if(FruitsNode[0] == r.getSrc()) {
					FruitsNode[0] = FruitsNode[1];
				}
				if(getGUI().getGraphAlgo().shortestPathDist(r.getSrc(), closestNode[0]) >
				getGUI().getGraphAlgo().shortestPathDist(r.getSrc(), FruitsNode[0])) {
					closest = list.get(i);
				}
			}
		}
		return closest;
	}

	
	/**
	 * checking values of each fruit in the list and return the most valuable fruit
	 * @param list - fruit list
	 * @return fruit - the most worth fruit
	 */
	public Fruits mostValue(ArrayList<Fruits> list) {
		if(list.isEmpty()) {
			return null;
		}
		Fruits f_highest = list.get(0);
		for(int i = 1; i < list.size(); i++) {
			if(list.get(i).isTarget() == false && f_highest.getValue() < list.get(i).getValue()) {
				f_highest = list.get(i);
			}
		}
		//f_highest.setIsTarget(true);
		return f_highest;
	}


	/**
	 * to a given fruit iterate on all the nodes and edges find the right edge
	 * that the fruit is on return pair of nodes key, src and dest
	 * order is matter because fruit has 2 types
	 * @param f
	 * @return pair of nodes key - src and dest of the edge
	 */
	public int[] nearestNode(Fruits f) {
		if(f == null) {
			return null;
		}

		Boolean flag = false;
		ArrayList<node_data> nodes = new ArrayList<>();
		ArrayList<edge_data> edges = new ArrayList<>();
		nodes.addAll(getGUI().getGraphAlgo().get_Dgraph().getV());
		nodeData src, dest;
		edgeData edge;
		int[] ans = new int[2];
		for (int i = 0; i < nodes.size(); i++) {
			src = (nodeData) nodes.get(i);
			edges.addAll(getGUI().getGraphAlgo().get_Dgraph().getE(i));

			for (int j = 0; j < edges.size(); j++) {
				edge = (edgeData) ((edges).get(j));
				dest = edge.getNodeDest();

				/*if (dest.getKey() > src.getKey()) {*/
				flag = distance(src.getLocation(), dest.getLocation(), new Point3D(f.getPosX(), f.getPosY()));
				if (flag) {
					// check higher node key
					int higher = src.getKey() - dest.getKey();
					if (higher > 0 && f.getType() == -1) {
						ans[0] = src.getKey();
						ans[1] = dest.getKey();
						return ans;
					}

					else if (higher > 0 && f.getType() == 1) {
						ans[0] = dest.getKey();
						ans[1] = src.getKey();
						return ans;
					}

					else if (f.getType() == -1) {
						ans[0] = dest.getKey();
						ans[1] = src.getKey();
						return ans;
					}

					else {
						ans[0] = src.getKey();
						ans[1] = dest.getKey();
						return ans;
					}
				}
				/* } */
			}
			edges.clear();
		}
		// not found


		ans[0] = -1;
		ans[1] = -1;
		return ans;
	}

	/**
	 * check if fruit between two nodes(on the edge) by checking distance.
	 * 
	 * @param n1 - node1 point 
	 * @param n2 - node2 point 
	 * @param f  - fruit point 
	 * @return
	 */
	private boolean distance(Point3D n1, Point3D n2, Point3D f) {
		if (n1.distance2D(f) + f.distance2D(n2) - eps <= n1.distance2D(n2))
			return true;
		return false;
	}


	/**
	 * reading data from the server game, init the fruits and robots to 
	 * the new values each call of this function 
	 * in case the robot has no dest - aka (-1) 
	 * send it to ClosestNodeAuto/nextValueAuto to create a new path to the robot
	 * at the same time send placemark data to the kml_logger class
	 */
	public void moveRobotsAuto() {
		// update fruit

		int[] fruitLocation = new int[2];
		int robNodeList_size = 0;

		List<String> fruits = getGUI().getGame().getFruits();
		for (int i = 0; i < fruits.size(); i++) {
			getGUI().getFruitList().get(i).init(fruits.get(i));
			Fruits f = getGUI().getFruitList().get(i);
			int type = getGUI().getFruitList().get(i).getType();
			
			// kml fruit placemark 
			if(type == -1) {
				getGUI().getKml().Placemark(5, f.getPosX(), f.getPosY(), getGUI().getKml().currentTime());
			}
			else {
				getGUI().getKml().Placemark(6, f.getPosX(), f.getPosY(), getGUI().getKml().currentTime());
			}
		}

		List<String> log = getGUI().getGame().move();
		if (log != null) {
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
					fruitLocation = nearestNode(r.getTarget());
					robNodeList_size = r.getNextDest().size();
					
					// fruit changes is place or other robot took it clear nextDest list
					if(r.getNextDest().get(robNodeList_size - 1).getKey() != fruitLocation[1]) {
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
	 * using ClosestFruitbyShortestpath function to get the closest fruit
	 * then using shortpath to get the list of nodes in the route to the fruit
	 * and insert it to the robot nextDest list
	 * @param rob_id - robot id
	 * @param src - robot current src node
	 * @return next destnation to the robot
	 */
	private int ClosestNodeAuto(int src, int rob_id) {
		int ans[] = new int[2];
		List<node_data> nextdest = new ArrayList<node_data>();
		Fruits f = ClosestFruitbyShortestpath(getGUI().getFruitList(), getGUI().getRobList().get(rob_id));
		if (f != null){
			ans = nearestNode(f);
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
	 * using mostValue function to get the most valuable fruit
	 * then using shortpath to get the list of nodes in the route to the fruit
	 * and insert it to the robot nextDest list
	 * @param rob_id - robot id
	 * @param src - robot current src node
	 * @return next destnation to the robot
	 */
	private int nextValueAuto(int src, int rob_id) {
		int ans[] = new int[2];

		List<node_data> nextdest = new ArrayList<node_data>();
		Fruits f = mostValue(getGUI().getFruitList());
		if (f != null){

			ans = nearestNode(f);
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
	 * init the robot list in the gui class to the number of given robots from the server
	 * placing the robots before the game start
	 * each robot place near the most value fruit if there is few robots
	 * then they are placed in other fruits by most value order
	 */
	private void RobotsAutoPosition() {
		JSONObject GameJson;
		try {
			GameJson = new JSONObject(getGUI().getGame().toString()).getJSONObject("GameServer");
			int Robot_num = GameJson.getInt("robots");
			int[] nextNode = new int[2];
		
			getGUI().setRobList(new ArrayList<Robots>(Robot_num));
			// if less fruit then robots
			if(getGUI().getFruitList().size() < Robot_num) {
				for (int i = 0; i < getGUI().getFruitList().size(); i++) {
					Fruits f = getGUI().getAutoPlayer().mostValue(getGUI().getFruitList());
					nextNode = getGUI().getAutoPlayer().nearestNode(f);

					if (nextNode[0] == -1) {
						getGUI().getGame().addRobot((i + 6) % getGUI().getGraphAlgo().get_Dgraph().nodeSize());
					} else {
						getGUI().getGame().addRobot(nextNode[0]);

					}
				}
			}
			else {

				for (int i = 0; i < Robot_num; i++) {
					Fruits f = this.mostValue(getGUI().getFruitList());
					nextNode = this.nearestNode(f);

					if (nextNode[0] == -1) {
						getGUI().getGame().addRobot((i + 6) % getGUI().getGraphAlgo().get_Dgraph().nodeSize());
					} else {
						getGUI().getGame().addRobot(nextNode[0]);
						f.setIsTarget(true);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Iterator<Fruits> f_iter = getGUI().getFruitList().iterator();
		while(f_iter.hasNext()) {
			f_iter.next().setIsTarget(false);
		}

		// adding robots
		Iterator<String> iter = getGUI().getGame().getRobots().iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			Robots r = new Robots(s);
			getGUI().getRobList().add(r);
			r.setNextDest(new ArrayList<node_data>());
		}
	}



	/*** private data ****/

	private MyGameGUI _gui;
	private final double eps = 0.000001;
	
	/*** getters/setters ***/
	public MyGameGUI getGUI() {
		return _gui;
	}
	
	private void setGUI(MyGameGUI _gui) {
		this._gui = _gui;
	}

}
