package Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.StringWriter;


import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.w3c.dom.Element;

import dataStructure.DGraph;
import dataStructure.nodeData;
import gameClient.KML_Logger;
import utils.Point3D;

class KML_LoggerTest {
	private static final DGraph dg1=new DGraph();
	private static KML_Logger kml;

	 static Point3D p11=new Point3D(-4.0,4.0);

	 static nodeData nd11 = new nodeData(p11);

	 
	 @BeforeAll
		static void params(){
			dg1.addNode(nd11);
			// kml params is Scenario number(using 24 to not override real kml files), graph
			kml = new KML_Logger(24, dg1);
		}
	 
	 /**
	  * 
	  * @param elem
	  * @return string - convert element to string
	  */
	 static String printString(Element elem) {
			String output = "";
			try {
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");

				StreamResult result = new StreamResult(new StringWriter());
				DOMSource source = new DOMSource(elem);
				transformer.transform(source, result);

				output = result.getWriter().toString();
				
			} catch (TransformerException e) {
				e.printStackTrace();
			}
			return output;
		}

	@Test
	void PlacemarkTest() {
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Placemark>\r\n" + 
				"<TimeStamp>\r\n" + 
				"<when>mytime</when>\r\n" + 
				"</TimeStamp>\r\n" + 
				"<styleUrl>Robot-2</styleUrl>\r\n" + 
				"<Point>\r\n" + 
				"<coordinates>3.0,1.0,0.0</coordinates>\r\n" + 
				"</Point>\r\n" + 
				"</Placemark>\r\n";
		
		// placemark param is id, posX, posY, time
		kml.Placemark(2, 3, 1, "mytime");
		Element elem = (Element) kml.getDocument().getElementsByTagName("Placemark").item(1);
		String actual = printString(elem);
		assertEquals(expected, actual, "Test Placemark");
	}
	
	@Test
	void iconTest() {
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Style id=\"Robot-2\">\r\n" + 
				"<IconStyle>\r\n" + 
				"<Icon>\r\n" + 
				"<href>http://maps.google.com/mapfiles/kml/pal4/icon7.png</href>\r\n" + 
				"</Icon>\r\n" + 
				"<hotSpot x=\"32\" xunits=\"pixels\" y=\"1\" yunits=\"pixels\"/>\r\n" + 
				"</IconStyle>\r\n" + 
				"</Style>\r\n";
		
		// icon param is id
		kml.icon(2);
		// item(8) is the last one, when you create new object of kml_logger we already setting 7 defualt icons
		Element elem =(Element) kml.getDocument().getElementsByTagName("Style").item(8);
		String actual = printString(elem);
		assertEquals(expected, actual, "Test icon");
	}
	
	@Test
	void currentTimeTest() {
		if(kml.currentTime().length() == 0) {
			fail("expected a String that represent current time");
		}
	}
	
	@Test
	void KMLtoFileTest() {
		File file = new File("data\\"+24+".kml");
		kml.KMLtoFile();
		  if(!file.exists()) 
			  fail("File should have been created");
		  if(!file.delete()) {
			  fail("File should have been deleted");
		  }
		}
		


}
