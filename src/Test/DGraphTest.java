
package Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.nodeData;
import utils.Point3D;



class DGraphTest {
	private static final DGraph dg1=new DGraph();
	private static final DGraph dg2=new DGraph();
	private static game_service _game = Game_Server.getServer(0);

	 static Point3D p01=new Point3D(20,14);
	 static Point3D p11=new Point3D(-4.0,4.0);
	 static Point3D p21=new Point3D(3,8);
	 static Point3D p31=new Point3D(8,5);
	 static Point3D p41=new Point3D(12,5);
	 static Point3D p51=new Point3D(10,10);
	 

	 static nodeData nd01 = new nodeData(p01);
	 static nodeData nd11 = new nodeData(p11);
	 static nodeData nd21 = new nodeData(p21);
	 static nodeData nd31 = new nodeData(p31);
	 static nodeData nd41 = new nodeData(p41);
	 static nodeData nd51 = new nodeData(p51);
	 
	 
	 @BeforeAll
		static void params(){
		 	dg1.addNode(nd01);
			dg1.addNode(nd11);
			dg1.addNode(nd21);
			dg1.addNode(nd31);
			dg1.addNode(nd41);
			dg1.addNode(nd51);
			
			
			dg1.connect(1,2,1);
			dg1.connect(2,3,2);
			dg1.connect(1,3,0.2);
	    	dg1.connect(3,4,0.5);
			dg1.connect(3,5,5);
	    	dg1.connect(5,0,4);
			dg1.connect(4,0,2);
			dg1.connect(0,1,1.2);
			
			dg2.init(_game.getGraph());
			
		}
	@Test
	void testGetNode() {
		assertEquals(dg1.getNode(2),nd21);
	}

	@Test
	void testGetEdge() {
		assertEquals(dg1.getEdge(1,2),nd11.get_edges().get(2));
	}

	@Test
	void testAddNode() {
		Point3D p61=new Point3D(7.0,4.0);
		nodeData nd61 = new nodeData(p61);
		dg1.addNode(nd61);
		assertTrue(dg1.get_number_key()==6);
	}

	@Test
	void testConnect() {
		dg1.connect(2,0,20);
		assertTrue(dg1.getEdge(2,0).getWeight()==20);
	}

	@Test
	void testGetV() {
		assertTrue(dg1.getV().size()-1==dg1.get_number_key());
	}

	@Test
	void testGetE() {
		assertTrue(dg1.getE(2).size()==1);
	}

	@Test
	void testRemoveNode() {
		dg1.removeNode(1);
		assertTrue(dg1.getNode(1)==null);
		
	}

	@Test
	void testRemoveEdge() {
		dg1.removeEdge(1,2);
		assertTrue(dg1.getEdge(1,2)==null);
	}
	@Test
	void GraphScaleTest() {
		double expectedRangeX[] = {3,20}; 
		double expectedRangeY[] = {4, 14}; 

		assertEquals(expectedRangeX[0], dg1.GraphScaleX().get_min(),"ScaleX Test");
		assertEquals(expectedRangeX[1], dg1.GraphScaleX().get_max(),"ScaleX Test");
		assertEquals(expectedRangeY[0], dg1.GraphScaleY().get_min(),"ScaleY Test");
		assertEquals(expectedRangeY[1], dg1.GraphScaleY().get_max(),"ScaleY Test");
	}
	
	@Test
	void initTest() {
		double weight_expected = 1.4004465106761335;
		double weight_actual  = dg2.getEdge(0, 1).getWeight();
		double posX_expected = 35.18753053591606;
		double posY_expected = 32.10378225882353;
		double posX_actual = dg2.getNode(0).getLocation().x();
		double posY_actual = dg2.getNode(0).getLocation().y(); 
		assertEquals(weight_expected, weight_actual, "initTest, edge weight");
		assertEquals(posX_expected, posX_actual, "initTest, posX weight");
		assertEquals(posY_expected, posY_actual, "initTest, posY weight");
	}
	
}
