package gameClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import dataStructure.edgeData;
import dataStructure.edge_data;
import utils.Point3D;
import utils.StdDraw;

public class ManualPlayer {

	
	public ManualPlayer(MyGameGUI gui) {
		setGUI(gui);
		init();
	}
	
	private void init() {
		RobotsStartPosition();
	}
	

	/**
	 * reading data from the server game, init the fruits and robots to 
	 * the new values each call of this function 
	 * in case the robot has no dest - aka (-1) 
	 * send it to nextNodeGUI that get new dest to the robot
	 * if there is more then one robot you can control each separately by clicking
	 * the number (robot_id) on the keyboard
	 * at the same time send placemark data to the kml_logger class
	 */
	public void moveRobotsGUI() {
		char c='0';
		//update fruit

		List<String> fruits = getGUI().getGame().getFruits();
		for (int i = 0; i < fruits.size(); i++) {
			getGUI().getFruitList().get(i).init(fruits.get(i));
			Fruits f = getGUI().getFruitList().get(i);
			int type = getGUI().getFruitList().get(i).getType();
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

				
				getGUI().getKml().Placemark(r.getId(), r.getPosX(), r.getPosY(), getGUI().getKml().currentTime());

				for(int j=0;j < getGUI().getRobList().size();j++) {
					c=(char) (j+'0');
					if(StdDraw.isKeyPressed(c))
						StdDraw.setPlayer(j);
				}
				
				if (r.getDest() == -1) {
					getGUI().getGame().chooseNextEdge(StdDraw.getPlayer(), nextNodeGUI(r.getSrc()));


				}
			}
		}
	}

	/**
	 * listen to mouse pressed on the nodes if there is an edge from the robot current src
	 * to the user clicked node it will return that as dest
	 * @param int - next dest to the robot
	 */
	private int nextNodeGUI(int src) {
		int nextDest = -1;
		double x = 0, y = 0;


		if(StdDraw.isMousePressed()) {

			x = StdDraw.mouseX();
			y = StdDraw.mouseY();
		}
		Point3D p = new Point3D(x, y);
		Iterator<edge_data> iter = getGUI().getGraphAlgo().get_Dgraph().getE(src).iterator();
		edgeData edge;
		while (iter.hasNext()) {
			edge = (edgeData) iter.next();
			double check = p.distance2D(edge.getNodeDest().getLocation());
			if (check <= 0.0005) {
				return edge.getDest();
			}

		}
		return nextDest;
	}
	/**
	 * asking the user to place the robots at the start of the game
	 * on one of the nodes
	 */
	private void RobotsStartPosition() {
		JSONObject GameJson;
		try {
			GameJson = new JSONObject(getGUI().getGame().toString()).getJSONObject("GameServer");
			int Robot_num = GameJson.getInt("robots");
			getGUI().setRobList(new ArrayList<Robots>(Robot_num));
			for (int a = 0; a < Robot_num; a++) {
				getGUI().getGame().addRobot(StdDraw.dialogRobots(a, getGUI().getGraphAlgo().get_Dgraph().nodeSize()));
				getGUI().getKml().icon(a);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		// adding robots
		Iterator<String> iter = getGUI().getGame().getRobots().iterator();
		while (iter.hasNext()) {
			getGUI().getRobList().add(new Robots(iter.next()));
		}
	}
	

	/**** private data ***/

	private MyGameGUI _gui;
	
	
	/*** getters/setters ***/
	private void setGUI(MyGameGUI g) {
		this._gui = g;
	}
	public MyGameGUI getGUI() {
		return _gui;
	}

}
