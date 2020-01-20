package dataStructure;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.Point3D;
import utils.Range;

public class DGraph implements graph, Serializable {


	/**
	 * init DGraph with hashmap <Integer, node_data> represent as <node.key,node>
	 *  and fill it from a given graph
	 * using Iterator to run on each node and add it to new graph
	 * @param g - graph
	 */
	public DGraph(DGraph g) {
		set_graph(new HashMap<Integer, node_data>());
		Iterator<node_data> iter = g.getV().iterator(); // key, node
		while(iter.hasNext()) {
			nodeData current = (nodeData) iter.next();
			this.get_graph().put(current.getKey(), current);
		}
		set_mc(0);
		set_number_key(-1);
	}

	//init Dgraph with hashmap <Integer, node_data> represent as <node.key,node>
	public DGraph() {
		set_graph(new HashMap<Integer, node_data>());
		set_mc(0);
		set_number_key(-1);
	}

	/**
	 * init new graph from string
	 * @param g - string with json format
	 */
	public void init(String g) {
		String pos;
		String split[];
		Point3D p;
		int src, dest;
		double weight;
		try {
			JSONObject obj = new JSONObject(g);
			JSONArray nodes = obj.getJSONArray("Nodes");
			for (int i = 0; i < nodes.length(); i++) {
				pos = (String) nodes.getJSONObject(i).get("pos");
				split = pos.split(",");
				p = new Point3D(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
				this.addNode(new nodeData(p));
			}
			
			JSONArray edges = obj.getJSONArray("Edges");
			for (int i = 0; i < edges.length(); i++) {
				src =  (int) edges.getJSONObject(i).get("src");
				dest = (int) edges.getJSONObject(i).get("dest");
				weight = (double) edges.getJSONObject(i).get("w");
				this.connect(src, dest, weight);
			}

		} catch (JSONException e) {e.printStackTrace();}

	}


	/**
	 * @return node_data
	 * @param key - node key number
	 * return the node_data by key
	 * if dont exist or empty return null
	 */
	@Override
	public node_data getNode(int key) {

		nodeData node = (nodeData)this.get_graph().get(key);
		return node;
	}

	/**
	 * if src node doesnt exist or  graph is empty return null,
	 * else if src == dest throw Exception
	 * src.get_edges() --> return the edge_data
	 * @param src - node key
	 * @param dest - node key
	 * @return edge_data 
	 */
	@Override
	public edge_data getEdge(int src, int dest) {
		if(src == dest) {
			throw new RuntimeException("No edge from a node to himself");
		}

		try {
			nodeData src_node = (nodeData) this.get_graph().get(src);
			edgeData src_to_dest = (edgeData) src_node.get_edges().get(dest);
			return src_to_dest;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * if node is null throw Exception
	 * else add it to the hashmap 
	 * increment mc and nodeKeys (_number_key)
	 * @param m - node_data 
	 */
	@Override
	public void addNode(node_data n) {
		if(n == null) {
			throw new RuntimeException("Input is null");
		}
		set_number_key(this.get_number_key() + 1);
		((nodeData) n).setKey(this.get_number_key());
		this.get_graph().put(this.get_number_key(),(nodeData) n);
		this.set_mc(this.getMC()+1);
	}

	/**
	 * if src == dest throw Exception - not possible to connect node to himself
	 * src or dest not null create new edge with src.new_edge(dest, weight);
	 * @param src node key
	 * @param dest node key
	 * @param w - edge weight
	 */
	@Override
	public void connect(int src, int dest, double w) {
		if(src == dest) {
			throw new RuntimeException("No edge from a node to himself");
		}

		nodeData src_node = (nodeData) this.get_graph().get(src);
		nodeData dest_node =  (nodeData) this.get_graph().get(dest);
		if(src_node == null) {
			throw new RuntimeException("Source node doesn't exist in the graph");
		}
		else if(dest_node == null) {
			throw new RuntimeException("destination node doesn't exist in the graph");
		}
		// create edge
		else {
			src_node.new_edge(dest_node, w);
			this.set_mc(this.getMC()+1);
		}
	}

	/**
	 * @return collection of the node_data
	 */
	@Override
	public Collection<node_data> getV() {
		if(this.get_graph().isEmpty()) {
			throw new RuntimeException("Graph is empty");
		}
		return this.get_graph().values();
	}



	/**
	 * check if node is valid in the hashmap else throw Exception
	 *@return given node inner hashmap values aka his edges the from him to any dest
	 * 
	 */
	@Override
	public Collection<edge_data> getE(int node_id) {
		nodeData node = (nodeData) this.get_graph().get(node_id);
		if(node == null) {
			throw new RuntimeException("Node doesn't exist in the graph");
		}
		else if(node.get_edges().isEmpty()) {
			throw new RuntimeException("there is no edges from this Node");
		}
		else {
			return node.get_edges().values();
		}
	}
	/**
	 * Delete the node (with the given ID) from the graph -
	 * and removes all edges which starts or ends at this node.
	 * all the edges should be removed.
	 * @param key - node key
	 * @return the data of the removed node (null if none). 
	 */
	@Override
	public node_data removeNode(int key) {
		// if node doesnt exist
		nodeData node = (nodeData) this.get_graph().get(key);
		if(node == null) {
			throw new RuntimeException("Node doesn't exist in the graph");
		}

		// delete this node with all his edges as dest
		Iterator<node_data> iter = getV().iterator();
		while(iter.hasNext()) {
			nodeData current = (nodeData)iter.next();
			if(current.get_edges().get(key) != null) {
				current.get_edges().remove(key);
				this.set_mc(this.getMC()+1);
			}
		}
		// delete this node with all his edges as src
		this.set_mc(this.getMC()+1);

		return this.get_graph().remove(key);
	}

	/**
	 * check validation of src and dest else throw Exception	 
	 * Delete the edge from the graph, 
	 * @param src - node key
	 * @param dest - node key
	 * @return the data of the removed edge (null if none).
	 */
	@Override
	public edge_data removeEdge(int src, int dest) {
		if(src == dest) {
			throw new RuntimeException("No edge from a node to himself");
		}

		nodeData src_node = (nodeData) this.get_graph().get(src);
		nodeData dest_node = (nodeData) this.get_graph().get(dest);
		if(src_node == null) {
			throw new RuntimeException("Source Node doesn't exist in the graph");
		}
		else if(dest_node == null) {
			throw new RuntimeException("destination Node doesn't exist in the graph");
		}
		else {
			this.set_mc(this.getMC()+1);
			return src_node.get_edges().remove(dest);
		}
	}

	/** 
	 * return the number of nodes.
	 * @return size
	 */
	@Override
	public int nodeSize() {
		return this.get_graph().size();
	}

	/** 
	 * return the number of edges (assume directional graph).
	 * @return size
	 */
	@Override
	public int edgeSize() {
		int edge_size = 0;
		Iterator<node_data> iter = getV().iterator();
		while(iter.hasNext()) {
			nodeData current = (nodeData)iter.next();
			edge_size += current.get_edges().size();
		}
		return edge_size;
	}

	/**
	 * return the Mode Count - counting changes in the graph.
	 * @return mc
	 */
	@Override
	public int getMC() {
		return _mc;
	}
	/**
	 * iterate on all the nodes in the graph and return
	 * the Max and min Y value, check the scale
	 * this method is for drawing the graph with GUI class
	 * @return Range of y
	 */
	public Range GraphScaleY() {
		double minY, maxY;
		Iterator<node_data> iter = this.getV().iterator();
		minY = maxY = iter.next().getLocation().y();
		while(iter.hasNext()) {
			nodeData current = (nodeData) iter.next();
			if(current.getLocation().y() < minY) {
				minY = current.getLocation().y();
			}
			else if(current.getLocation().y() > maxY){
				maxY = current.getLocation().y();
			}
		}
		return new Range(minY, maxY);
	}

	/**
	 * iterate on all the nodes in the graph and return
	 * the Max and min X value, check the scale
	 * this method is for drawing the graph with GUI class
	 * @return Range  of x
	 */
	public Range GraphScaleX() {
		double minX, maxX;
		Iterator<node_data> iter = this.getV().iterator();
		minX = maxX = iter.next().getLocation().x();
		while(iter.hasNext()) {
			nodeData current = (nodeData) iter.next();
			if(current.getLocation().x() < minX) {
				minX = current.getLocation().x();
			}
			else if(current.getLocation().x() > maxX){
				maxX = current.getLocation().x();
			}
		}
		return new Range(minX, maxX);
	}
	public int get_number_key() {
		return _number_key;
	}

	public HashMap<Integer, node_data> get_graph() {
		return _graph;
	}


	/***** private data ****/
	private void set_mc(int m) {
		this._mc = m;
	}

	private void set_graph(HashMap<Integer, node_data> _graph) {
		this._graph = _graph;
	}

	private void set_number_key(int _number_key) {
		this._number_key = _number_key;
	}
	private int _number_key;
	private int _mc;
	private HashMap<Integer, node_data> _graph;

}
