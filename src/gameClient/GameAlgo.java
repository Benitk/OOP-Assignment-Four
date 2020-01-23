package gameClient;

import java.util.ArrayList;

import algorithms.Graph_Algo;
import dataStructure.edgeData;
import dataStructure.edge_data;
import dataStructure.nodeData;
import dataStructure.node_data;
import utils.Point3D;
import utils.Range;

public class GameAlgo {

	public GameAlgo(Graph_Algo ga) {
		setGraphAlgo(ga);
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
				if(getGraphAlgo().shortestPathDist(r.getSrc(), closestNode[0]) >
				getGraphAlgo().shortestPathDist(r.getSrc(), FruitsNode[0])) {
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
		nodes.addAll(getGraphAlgo().get_Dgraph().getV());
		nodeData src, dest;
		edgeData edge;
		int[] ans = new int[2];
		for (int i = 0; i < nodes.size(); i++) {
			src = (nodeData) nodes.get(i);
			edges.addAll(getGraphAlgo().get_Dgraph().getE(i));

			for (int j = 0; j < edges.size(); j++) {
				edge = (edgeData) ((edges).get(j));
				dest = edge.getNodeDest();

				flag = distanceTriangle(src.getLocation(), dest.getLocation(), new Point3D(f.getPosX(), f.getPosY()));
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
			}
			edges.clear();
		}
		// not found ans [-1, 0]
		ans[0] = -1;
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
	private boolean distanceTriangle(Point3D n1, Point3D n2, Point3D f) {
		if (n1.distance2D(f) + n2.distance2D(f) - eps <= n1.distance2D(n2))
			return true;
		return false;
	}




	/** private data ***/
	private Graph_Algo _graphAlgo;
	private final double eps = 0.0000001;


	/** getter/setter ***/



	public Graph_Algo getGraphAlgo() {
		return _graphAlgo;
	}


	private void setGraphAlgo(Graph_Algo _graphAlgo) {
		this._graphAlgo = _graphAlgo;
	}
}
