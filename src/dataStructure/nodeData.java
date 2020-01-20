package dataStructure;

import java.io.Serializable;
import java.util.HashMap;
import utils.Point3D;

/**
 * 
 * @author Ben itzhak
 * @author Liad ben moshe
 *
 */
public class nodeData implements node_data, Serializable {
	
	/**
	 * init node with default param 
	 * @param point setting the location of the node
	 * and hashmap <Integer, edge_data> represent as <dest.key,edge>
	 * Example node 1 --> (connect) node 2 then hashmap<2, 1-->2(edge)>
	 */
	public nodeData(Point3D point) {
		setLocation(point);
		setKey(0);
		setWeight(Integer.MAX_VALUE);
		setInfo("");
		set_edges();
		setTag(1);
		setBol('x');
	}

	/**
	 * adding a new edge with this node as src and input as dest
	 * @param dest - nodeData
	 * @param weigth - edge weight
	 */
	public void new_edge(nodeData dest, double weight) {
		edgeData new_edge = new edgeData(this, dest, weight);
		this.get_edges().put(dest._key, new_edge);
	}

	public HashMap<Integer, edge_data> get_edges() {
		return this._edges;
	}

	@Override
	public int getKey() {
		return this._key;
	}
	
	public void setKey(int key) {
		this._key = key;
	}
	
	@Override
	public Point3D getLocation() {
		return this._location;
	}

	@Override
	public void setLocation(Point3D p) {
		this._location = p;

	}

	@Override
	public double getWeight() {
		return this._weight;
	}

	@Override
	public void setWeight(double w) {
		this._weight = w;

	}

	@Override
	public String getInfo() {
		return this._info;
	}

	@Override
	public void setInfo(String s) {
		this._info = s;

	}

	@Override
	public int getTag() {
		return this._tag;
	}

	@Override
	public void setTag(int t) {
		if (1 > t || t > 3) {
			this._tag = 1;
		}
		this._tag = t;
	}

	/******* private data **********/
	private void set_edges() {
		this._edges = new HashMap<Integer, edge_data>();
	}

	public char getBol() {
		return _bol;
	}

	public void setBol(char _bol) {
		this._bol = _bol;
	}

	private HashMap<Integer, edge_data> _edges;
	private int _tag, _key;
	private String _info;
	private char _bol;
	private double _weight;
	private Point3D _location;
}
