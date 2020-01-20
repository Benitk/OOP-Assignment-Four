package Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.nodeData;
import dataStructure.node_data;
import utils.Point3D;


class Graph_AlgoTest {
	
	private static final DGraph dg=new DGraph();

	 static Point3D p0=new Point3D(20,14);
	 static Point3D p1=new Point3D(-4.0,4.0);
	 static Point3D p2=new Point3D(3,8);
	 static Point3D p3=new Point3D(8,5);
	 static Point3D p4=new Point3D(12,5);
	 static Point3D p5=new Point3D(10,10);
	 
	 static nodeData nd0 = new nodeData(p0);
	 static nodeData nd1 = new nodeData(p1);
	 static nodeData nd2 = new nodeData(p2);
	 static nodeData nd3 = new nodeData(p3);
	 static nodeData nd4 = new nodeData(p4);
	 static nodeData nd5 = new nodeData(p5);
	 

	@BeforeAll
	static void params(){
		dg.addNode(nd0);
		dg.addNode(nd1);
		dg.addNode(nd2);
		dg.addNode(nd3);
		dg.addNode(nd4);
		dg.addNode(nd5);
		
		
		dg.connect(1,2,1);
		dg.connect(2,3,2);
		dg.connect(1,3,0.2);
    	dg.connect(3,4,0.5);
		dg.connect(3,5,5);
    	dg.connect(5,0,4);
		dg.connect(4,0,2);
		dg.connect(0,1,1.2);
		
	}


	@Test
	void testInitGraph() {
		Graph_Algo ga= new Graph_Algo();
		ga.init(dg);
		
		
	}

	@Test
	void testInitString() {
		Graph_Algo ga= new Graph_Algo();
		ga.init(dg);
		ga.save("testalgo");
		ga.init("testalgo");
		
	}

	@Test
	void testSave() {
		Graph_Algo ga= new Graph_Algo();
		ga.init(dg);
		ga.save("testalgosave");
	}
	
	@Test
	void testIsConnected() {
		Graph_Algo ga= new Graph_Algo();
		ga.init(dg);
		assertTrue(ga.isConnected()==true);
		
		
	}

	@Test
	void testShortestPathDist() {
		Graph_Algo ga= new Graph_Algo();
		ga.init(dg);
		assertEquals(ga.shortestPathDist(1,0),2.7);
	}

	@Test
	void testShortestPath() {
		Graph_Algo ga= new Graph_Algo();
		ga.init(dg);
		ArrayList<node_data>arr=new ArrayList<node_data>();
		arr=(ArrayList<node_data>) ga.shortestPath(1,0);
		int arr2[]=new int[arr.size()];
		for(int i=0;i<arr.size();i++) {
			arr2[i]=arr.get(i).getKey();
		}
		int arr3[]= {1,3,4,0};
		assertArrayEquals(arr2,arr3);
		
	}

	@Test
	void testTSP() {
		Graph_Algo ga= new Graph_Algo();
		ga.init(dg);
		List<Integer> targets=new ArrayList<Integer>();
		targets.add(4);
		targets.add(3);
		targets.add(5);
		targets.add(1);
		List<node_data> targetsMMM=new ArrayList<node_data>();
		targetsMMM = ga.TSP(targets);
		int arr2[]=new int[targetsMMM.size()];
		for(int i=0;i<targetsMMM.size();i++) {
			arr2[i]=targetsMMM.get(i).getKey();
		}
		int arr3[]= {4, 0, 1, 3, 5};
		assertArrayEquals(arr2,arr3);
		
		
	}

}
