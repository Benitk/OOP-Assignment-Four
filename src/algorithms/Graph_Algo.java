package algorithms;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dataStructure.DGraph;
import dataStructure.edgeData;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.nodeData;
import dataStructure.node_data;


public class Graph_Algo implements graph_algorithms, Serializable {

	public Graph_Algo() {
		set_Dgraph(new DGraph());
	}

	public Graph_Algo(graph _graph) {
		set_Dgraph((DGraph)_graph);
	}
	/*
	 * init a Graph_algo from a graph
	 */
	@Override
	public void init(graph g) {
		set_Dgraph((DGraph)g);

	}

	/**
	 * load a graph from a file
	 * using implemention Serializable
	 * @param file_name 
	 */
	@Override
	public void init(String file_name) {
		graph file_graph = null; 

		try
		{    
			FileInputStream file = new FileInputStream(file_name); 
			ObjectInputStream in = new ObjectInputStream(file); 

			file_graph = (graph)in.readObject(); 
			this.init(file_graph);

			in.close(); 
			file.close(); 
		} 

		catch(IOException e) 
		{ 
			throw new RuntimeException("File is not readable");	
		} 

		catch(ClassNotFoundException e) 
		{ 
			throw new RuntimeException("File is not Found");	
		} 



	}

	/**
	 * save graph to a file
	 * using implemention Serializable,
	 * @param file_name
	 */
	@Override
	public void save(String file_name) {
		graph dgraph = this.copy();
		try
		{    
			FileOutputStream file = new FileOutputStream(file_name); 
			ObjectOutputStream out = new ObjectOutputStream(file); 

			out.writeObject(dgraph); 

			out.close(); 
			file.close(); 
		}   
		catch(IOException e) 
		{ 
			throw new RuntimeException("File is not writable");
		} 
	}

	/**
	 * this algoritem check if one node 'A' connected to every other node
	 * then check if every other node can reach back to A.
	 * if empty or size == 1 return true becuase
	 * graph is connected if it has exactly one connected component.
	 * assume diectional graph
	 * @return true or false
	 */
	@Override
	public boolean isConnected() {

		if(this.get_Dgraph().get_graph().isEmpty()) {
			return true;
		}

		int size = this.get_Dgraph().nodeSize();
		if(size == 1) {
			return true;
		}
		nodeData current;
		Iterator<node_data> iter = (Iterator<node_data>) this.get_Dgraph().getV().iterator();
		nodeData first = (nodeData) iter.next();
		while(iter.hasNext()) {
			this.GreenTag();
			if(isConnected(iter.next().getKey(),first)==0)
				return false;
		}
		Iterator<node_data> iter2 = (Iterator<node_data>) this.get_Dgraph().getV().iterator();
		while(iter2.hasNext()) {
			this.GreenTag();
			current = (nodeData) iter2.next();
			if(current.getKey() != first.getKey()) {
				if(isConnected(first.getKey(),current)==0)
					return false;
			}
		}
		return true;
	}

	private int isConnected(int toFind, nodeData current) {
		int sum = 0;
		if(current.getTag() == 3) {
			return 0;
		}
		// found 
		else if(current.get_edges().get(toFind) != null) {
			return 1;
		}
		// not found
		else {
			current.setTag(3); // 3 --> Red
			Iterator<edge_data> iter = current.get_edges().values().iterator();
			while(iter.hasNext()) {
				edgeData current_edge = (edgeData)iter.next();
				nodeData dest = (nodeData) this.get_Dgraph().get_graph().get(current_edge.getDest());
				sum += isConnected(toFind, dest);
			}
		}
		return sum;
	}

	/**
	 * return the short distance from src to dest as double
	 * @param src -node key
	 * @param dest - node key
	 * @return double - cost of the length
	 */
	@Override
	public double shortestPathDist(int src, int dest) {
		if(src == dest) {
			throw new RuntimeException("No edge from a node to himself");
		}

		nodeData start =(nodeData) this._Dgraph.getNode(src);
		nodeData end =(nodeData) this._Dgraph.getNode(dest);
		if(start == null) {
			throw new RuntimeException("Source node doesn't exist in the graph");
		}
		else if(end == null) {
			throw new RuntimeException("Destination node doesn't exist in the graph");
		}
		SetNodeWeightMaxInt();
		// set src weight to 0
		start.setWeight(0.0);
		// makes every tag green
		GreenTag();
		// check if there is a path
		if(isConnected(dest,(nodeData)this.get_Dgraph().getNode(src))==0) {
			return Integer.MAX_VALUE;
		}
		// makes every tag green
		GreenTag();//you must do here greentag() because the isconnected mix it

		shortestPathDist(start,end);
		return end.getWeight();
	}

	private void shortestPathDist(nodeData current,nodeData end) {
		if(current.getTag()==3 || current==end)
			return;
		UpDateWeightNeighbor(current);
		current.setTag(3);
		Iterator<edge_data> iter = current.get_edges().values().iterator();
		while(iter.hasNext()) {
			edgeData currentEdge=(edgeData) iter.next();
			shortestPathDist(currentEdge.getNodeDest(),end);
		}

	}

	private void UpDateWeightNeighbor(nodeData current) {
		Iterator<edge_data> iter = current.get_edges().values().iterator();
		while(iter.hasNext()) {
			edgeData currentEdge=(edgeData) iter.next();
			double value = current.getWeight()+currentEdge.getWeight();
			if(value<=currentEdge.getNodeDest().getWeight()) {
				currentEdge.getNodeDest().setWeight(value);
				currentEdge.getNodeDest().setInfo(Integer.toString(current.getKey()));
			}
		}
	}
	/**
	 * @param src - node key
	 * @param dest - node key
	 * @return a list of node_data that is the shortest way from src to dest
	 */
	@Override
	public List<node_data> shortestPath(int src, int dest) {
		ArrayList<node_data>listPathRev = new ArrayList<node_data>();//list in reverse
		double length = shortestPathDist(src,dest);
		if(length == Integer.MAX_VALUE) {
			return null;
		}
		nodeData current=(nodeData) this.get_Dgraph().getNode(dest);
		current.setBol('v');
		listPathRev.add(current);
		String info="";
		while(current!=this.get_Dgraph().getNode(src)) {
			info=current.getInfo();
			current=(nodeData) this.get_Dgraph().getNode(Integer.parseInt(info));
			current.setBol('v');
			listPathRev.add(current);
		}
		ArrayList<node_data>listPath = new ArrayList<node_data>();
		for(int i=listPathRev.size()-1;i>=0;i--)
			listPath.add(listPathRev.get(i));

		return listPath;
	}


	private ArrayList<Integer> checklist(List<Integer> targets){

		ArrayList<Integer> newlist=(ArrayList<Integer>) targets;
		for(int i=0;i<newlist.size();i++) {
			// node dont exist in graph throw 
			if(this.get_Dgraph().get_graph().get(newlist.get(i)) == null) {
				throw new RuntimeException("Node "+newlist.get(i)+" don't exist in the graph");
			}
			int count=0;
			for(int j=0;j<newlist.size();j++) {
				if(newlist.get(i)==newlist.get(j)) {
					count++;
				}
				if(count>1) {
					newlist.remove(i);
					i=0;
					break;
				}
			}
		}
		return newlist;
	}


	/**
	 * @param targest - list that the algorithms need to go throgh
	 * @return the short path that include every node in the input list 'targets'
	 */
	@Override
	public List<node_data> TSP(List<Integer> targets){
		ArrayList<Integer>arrTarget=checklist(targets);
		List<node_data> Tsplist=new ArrayList<node_data>();
		// O(targes)
		ColorsetX(targets);
		Iterator<Integer> iter = arrTarget.iterator();
		int srcKey = iter.next();
		int destKey;
		while(iter.hasNext()) {
			destKey = iter.next();
			nodeData dest = (nodeData) this.get_Dgraph().get_graph().get(destKey);
			if(dest.getBol() != 'v') {
				try {
					Tsplist.addAll(shortestPath(srcKey,destKey));
				}catch(Exception e) {
					// shortestPath return null
					if(e.getMessage() == null) {
						return null;
					}
					// shortestPath return Exception
					else {
						throw new RuntimeException(e.getMessage());
					}
				}
				srcKey = destKey;
				// delete duplicate
				if(iter.hasNext()) {
					Tsplist.remove(Tsplist.size()-1);
				}
			}
			else {
				Tsplist.add((nodeData) this.get_Dgraph().get_graph().get(srcKey));
			}

		}
		return Tsplist;
	}

	/**
	 * @return deep copy of this graph algo
	 */
	@Override
	public graph copy() {
		graph g = new DGraph(this.get_Dgraph());
		return g;
	}
	public DGraph get_Dgraph() {
		return _Dgraph;
	}



	/**** private data *****/

	private void set_Dgraph(DGraph _graphAlgo) {
		this._Dgraph = _graphAlgo;
	}


	private void ColorsetX(List<Integer> targets) {
		Iterator<Integer> iter = targets.iterator();
		while(iter.hasNext()) {
			nodeData current = (nodeData) this.get_Dgraph().get_graph().get(iter.next());
			current.setBol('x');
		}
	}

	private void GreenTag() {
		Iterator<node_data> iter = this.get_Dgraph().getV().iterator();
		while(iter.hasNext()) {
			nodeData current = (nodeData)iter.next();
			current.setTag(1);
		}
	}

	private void SetNodeWeightMaxInt(){
		Iterator<node_data> iter = this.get_Dgraph().getV().iterator();
		while(iter.hasNext()) {
			nodeData current = (nodeData)iter.next();
			current.setWeight(Double.MAX_VALUE);;
		}

	}
	private DGraph _Dgraph;

}
