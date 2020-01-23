package gameClient;

import java.awt.Color;
import java.awt.Font;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import utils.Point3D;
import utils.Range;
import utils.StdDraw;
import dataStructure.DGraph;

import dataStructure.edgeData;
import dataStructure.edge_data;
import dataStructure.nodeData;
import dataStructure.node_data;
import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;

public class MyGameGUI implements Runnable {

	/**
	 * init the game by getting input from user about the scenario number and type of game(manual or auto)
	 */
	public MyGameGUI() {
		Game_Server.login(315026807);
		int scenario = StdDraw.dialogScenario();
		int ID=StdDraw.insertID();
		Game_Server.login(ID);
		game_service game = Game_Server.getServer(scenario); // you have [0,23] games

		// user choice of game type (manual or automatic)
		setType(StdDraw.dialogType());

		DGraph graph = new DGraph();
		graph.init(game.getGraph());
		Graph_Algo graphAlgo = new Graph_Algo(graph);
		setKml(new KML_Logger(scenario, graph));
		setGraphAlgo(graphAlgo);
		setGame(game);
		init();
	}

	/**
	 * init the gui windo using stdDraw, fruits and robot frpm the game given jsons
	 * setting the game to manual or automatic game 
	 * then drawing everything on canvas
	 * and start the game thread
	 */
	private void init() {
		StdDraw.setCanvasSize(1050, 600);
		set_x(getGraphAlgo().get_Dgraph().GraphScaleX());
		set_y(getGraphAlgo().get_Dgraph().GraphScaleY());
		Fruits();
		drawGraph();
		StdDraw.Visible();

		if(getType() == 1) {
			setAutoPlayer(new AutomaticPlayer(this));
		}else {
			setManual(new ManualPlayer(this));
			StdDraw.ManualInstructions();
		}
		drawFruits();
		drawRobots();
		StdDraw.enableDoubleBuffering();
		StdDraw.show();
		

		_t = new Thread(this);
		_t.start();
		music();

	}

	private void repaint() {
		this.drawGraph();
		this.drawRobots();
		this.drawFruits();
		StdDraw.show();
		// StdDraw.pause(50);
	}

	/**
	 * thread that start the game, play music and repaint
	 */
	@Override
	public void run() {
		getGame().startGame();
		//double time = getGame().timeToEnd(); 
		while(getGame().isRunning()) {
			if(getType() == 1) {
				getAutoPlayer().moveRobotsAuto();

				try{Thread.sleep(Delay());} catch(InterruptedException e){}
				System.out.println(Delay());
			}else {
				getGame().move();
				getManual().moveRobotsGUI();
			}
			repaint();
		}

		// game finished print results
		String results = this.getGame().toString();
		StdDraw.clear();
		// drawing map
		StdDraw.picture((this.get_x().get_max()+this.get_x().get_min())/2, (this.get_y().get_min()+this.get_y().get_max())/2, "icons\\map.png", 0.05,
				0.02);
		StdDraw.setScale(-5, 5);
		try {
			JSONObject resultsJson = new JSONObject(results);
			JSONObject GameServer = resultsJson.getJSONObject("GameServer");
			int moves = GameServer.getInt("moves");
			int Score = GameServer.getInt("grade");

			StdDraw.setPenColor(Color.BLACK);
			StdDraw.setFont(new Font("Arial", Font.PLAIN, 40));
			StdDraw.text(0, 3, "Game over:");
			StdDraw.setFont(new Font("Arial", Font.PLAIN, 25));
			StdDraw.text(0,2, "you did "+moves+" Moves");
			StdDraw.text(0,1.5, "Your Score is: "+Score);
			StdDraw.show();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(StdDraw.dialogKML() == 0) {
			getGame().sendKML(getKml().printKml(getKml().getDocument()));
			getKml().KMLtoFile();
		}
	}

	/**
	 * drawRobots using pictures from icons folder
	 * icons from https://www.flaticon.com/
	 */
	private void drawRobots() {
		Iterator<Robots> r_iter = this.getRobList().iterator();
		while (r_iter.hasNext()) {
			Robots r = r_iter.next();

			if(r.getSpeed()==2) {
				StdDraw.picture(r.getPosX(), r.getPosY(), "icons\\p" + (r.getId()+10) + ".png");
			}
			else if(r.getSpeed()==5) {
				StdDraw.picture(r.getPosX(), r.getPosY(), "icons\\p" + (r.getId()+5) + ".png");


			}
			else
				StdDraw.picture(r.getPosX(), r.getPosY(), "icons\\p" + r.getId() + ".png");

		}
	}


	/**
	 * drawFruits using pictures from icons folder
	 * icons from https://www.flaticon.com/
	 */
	private void drawFruits() {
		// drawGraph Robots
		Iterator<Fruits> f_iter = this.getFruitList().iterator();
		while (f_iter.hasNext()) {
			Fruits f = f_iter.next();
			if (f.getType() == 1) {
				StdDraw.picture(f.getPosX(), f.getPosY(), "icons\\redpoke.png");

			}
			// type -1
			else {
				StdDraw.picture(f.getPosX(), f.getPosY(), "icons\\yellowpoke.png");
			}
		}

	}

	/**
	 * Open windo that adjusted scale bound by the nodes drawing the Graph by
	 * drawGraph points using iterator on each nodes location including key and
	 * drawGraph lines to the edges including direction and weight
	 * 
	 */
	public void drawGraph() {
		StdDraw.clear();
		Range x = this.get_x();
		Range y = this.get_y();



		StdDraw.setXscale(x.get_min() - x.get_min() * (_eps*_eps*10), x.get_max() + x.get_min() * (_eps*_eps*10));
		StdDraw.setYscale(y.get_min() - y.get_min() * (_eps*_eps*10), y.get_max() + y.get_min() * (_eps*_eps*10));


		// drawing map
		StdDraw.picture((this.get_x().get_max()+this.get_x().get_min())/2, (this.get_y().get_min()+this.get_y().get_max())/2, "icons\\map.png", 0.05,
				0.02);

		// directions compute;
		double directionX = 0;
		double directionY = 0;

		// for drawing edge weight

		/*
		 * double middleX = 0; double middleY = 0;
		 */

		// draw points
		Iterator<node_data> iter = this.getGraphAlgo().get_Dgraph().getV().iterator();

		Iterator<edge_data> iter_edge;
		while (iter.hasNext()) {
			nodeData current = (nodeData) iter.next();
			iter_edge = current.get_edges().values().iterator();
			// drawGraph edges
			while (iter_edge.hasNext()) {
				StdDraw.setPenColor(Color.BLACK);
				StdDraw.setPenRadius(_eps*5);
				edgeData current_edge = (edgeData) iter_edge.next();

				// calculations
				directionX = current.getLocation().x() * 0.1 + current_edge.getNodeDest().getLocation().x() * 0.9;
				directionY = current.getLocation().y() * 0.1 + current_edge.getNodeDest().getLocation().y() * 0.9;

				// drawGraph edges
				StdDraw.line(current.getLocation().x(), current.getLocation().y(),
						current_edge.getNodeDest().getLocation().x(), current_edge.getNodeDest().getLocation().y());

				// drawGraph direction
				StdDraw.setPenColor(Color.CYAN);
				StdDraw.setPenRadius(0.015);
				StdDraw.point(directionX, directionY);


				// drawing edge weight
				/*
				 * middleX = (current.getLocation().x() +
				 * current_edge.getNodeDest().getLocation().x()) / 2; middleY =
				 * (current.getLocation().y() + current_edge.getNodeDest().getLocation().y()) /
				 * 2; StdDraw.setPenColor(new Color(0, 153, 0)); StdDraw.setFont(new
				 * Font("Arial", Font.PLAIN, 20)); StdDraw.text(middleX, middleY + 0.0001,
				 * String.valueOf(current_edge.getWeight()));
				 */

			}
			// drawGraph point
			StdDraw.setPenColor(Color.BLUE);
			StdDraw.setPenRadius(0.02);
			StdDraw.point(current.getLocation().x(), current.getLocation().y());

			// node key
			StdDraw.setPenColor(Color.BLACK);
			StdDraw.setFont(new Font("Arial", Font.PLAIN, 20));
			StdDraw.text(current.getLocation().x(), current.getLocation().y() + _eps/10,
					String.valueOf(current.getKey()));

			// drawGraph timer
			StdDraw.text(this.get_x().get_max() - _eps*3, this.get_y().get_max(), "Time: " + this.getGame().timeToEnd()/1000);

			if(getGame().isRunning()) {
				// draw score
				double score = 0;
				for(int i = 0; i < getRobList().size(); i++) {
					score += getRobList().get(i).getValue();
				}
				StdDraw.text(this.get_x().get_max() - _eps*6, this.get_y().get_max(), "Score: " + score);

			}
			// manual game - drawing the icon near to the robot_id
			if(getType() != 1) {
				for(int i = 0; i < 5; i++) {
					StdDraw.picture(get_x().get_min() + _eps*i + _eps, get_y().get_min()+_eps*_eps, "icons\\p"+i+".png");
					StdDraw.text(get_x().get_min() + _eps*i + _eps, get_y().get_min()+_eps*_eps*300, "id-"+i+"");
				}
			}

		}
	}

	/**
	 * this method calculates time by simple formula if any robot is on the right edge as fruit
	 * divid the current delay by 2
	 * @param r - robot
	 * @return double - the delay time the robot need to catch the fruit
	 */
	private long Delay() {
		long lowest = _delay;
		int fruitEdge[] = new int[2];
		for(int r = 0; r < getRobList().size(); r++) {
			for(int f = 0; f < getFruitList().size(); f++) {
				fruitEdge = getAutoPlayer().getGameAlgo().nearestNode(getFruitList().get(f));
				if(fruitEdge[0] == getRobList().get(r).getSrc() || fruitEdge[1] == getRobList().get(r).getSrc())
					return lowest/(2);
			}
		}
		return lowest;
	}



	/**
	 * init FruitList then use the given Json from the game to
	 * create new fruits objects
	 */
	private void Fruits() {
		// adding fruits
		this.setFruitList(new ArrayList<Fruits>());
		Iterator<String> iter = this.getGame().getFruits().iterator();
		while (iter.hasNext()) {
			this.getFruitList().add(new Fruits(iter.next()));
		}
	}



	/**
	 * playing music while the game running
	 */
	private void music() 
	{       
		AudioPlayer MGP = AudioPlayer.player;
		AudioStream BGM;
		AudioData MD;

		ContinuousAudioDataStream loop = null;

		try
		{
			InputStream test = new FileInputStream("icons\\Pokemon.wav");
			BGM = new AudioStream(test);
			AudioPlayer.player.start(BGM);
			MD = BGM.getData();

			loop = new ContinuousAudioDataStream(MD);

		}
		catch(FileNotFoundException e){

		}
		catch(IOException error)
		{

		}
		MGP.start(loop);
	}
	
	public static void main(String[] args) {
		MyGameGUI gui = new MyGameGUI();
	}



	public Graph_Algo getGraphAlgo() {
		return _graphAlgo;
	}

	public game_service getGame() {
		return _game;
	}

	public ArrayList<Robots> getRobList() {
		return _rob_list;
	}

	public void setRobList(ArrayList<Robots> rob_list) {
		this._rob_list = rob_list;
	}

	public ArrayList<Fruits> getFruitList() {
		return _fruit_list;
	}

	public void setFruitList(ArrayList<Fruits> fruit_list) {
		this._fruit_list = fruit_list;
	}


	public AutomaticPlayer getAutoPlayer() {
		return _auto;
	}

	public ManualPlayer getManual() {
		return _manual;
	}
	public KML_Logger getKml() {
		return _kml;
	}



	/****private  data *****/

	private static long _delay = 60;
	private Thread _t;
	private AutomaticPlayer _auto;
	private ManualPlayer _manual;
	private Range _x, _y;
	private game_service _game;
	private Graph_Algo _graphAlgo;
	private ArrayList<Robots> _rob_list;
	private ArrayList<Fruits> _fruit_list;
	private KML_Logger _kml;
	private int _type;
	private final double _eps = 0.001;

	/******* getters/setter *****/


	private void setKml(KML_Logger _kml) {
		this._kml = _kml;
	}


	private void setManual(ManualPlayer _manual) {
		this._manual = _manual;
	}
	private void setGame(game_service _game) {
		this._game = _game;
	}

	private void setGraphAlgo(Graph_Algo _graph) {
		this._graphAlgo = _graph;
	}

	private Range get_x() {
		return _x;
	}

	private void set_x(Range _x) {
		this._x = _x;
	}

	private Range get_y() {
		return _y;
	}

	private void set_y(Range _y) {
		this._y = _y;
	}

	private void setAutoPlayer(AutomaticPlayer _auto) {
		this._auto = _auto;
	}

	private int getType() {
		return _type;
	}

	private void setType(int _type) {
		this._type = _type;
	}

}
