package gameClient;


import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import dataStructure.DGraph;
import dataStructure.node_data;

public class KML_Logger {
	

	/**
	 * init kml template file for the game, adding icons and nodes placemarks
	 * @param Scenario - number of the game
	 * @param graph - for getting nodes coordinates
	 */
	public KML_Logger(int Scenario, DGraph graph) {
		baseKML(Scenario);
		set_kmlFilePath("data\\"+Scenario+".kml");
		// set node icon to kml
		for(int i = 0; i < 8; i++) {
			icon(i);;
		}
		Iterator<node_data> iter = graph.getV().iterator();
		while(iter.hasNext()) {
			node_data current = iter.next();
			Placemark(7, current.getLocation().x(), current.getLocation().y(), currentTime());
		}
	}

	/**
	 * building the template format for kml
	 * @param Scenario - Scenario number of the game
	 */
	private void baseKML(int Scenario){
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder;
			documentBuilder = documentFactory.newDocumentBuilder();

			setDocument(documentBuilder.newDocument());

			// root element
			Element root = getDocument().createElement("kml");
			Attr attr_kml = getDocument().createAttribute("xmlns");
			attr_kml.setValue("http://earth.google.com/kml/2.2");
			root.setAttributeNode(attr_kml);
			getDocument().appendChild(root);

			setGame(getDocument().createElement("Document"));
			root.appendChild(this.getGame());

			Element GameName = getDocument().createElement("name");
			GameName.appendChild(getDocument().createTextNode("Game Scenario "+Scenario+""));
			this.getGame().appendChild(GameName);

		} catch (ParserConfigurationException e) {

			e.printStackTrace();
		}

	}


	private String GetIconHref(int id) {
		String icon = "";
		if(id == 0) {
			icon = "http://maps.google.com/mapfiles/kml/pal4/icon54.png";
		}
		else if(id == 1) {
			icon = "http://maps.google.com/mapfiles/kml/pal4/icon23.png";
		}
		else if(id == 2) {
			icon = "http://maps.google.com/mapfiles/kml/pal4/icon7.png";
		}
		else if(id == 3) {
			icon = "http://maps.google.com/mapfiles/kml/pal2/icon48.png";
		}
		else if(id == 4) {
			icon = "http://maps.google.com/mapfiles/kml/pal3/icon18.png";
		}
		//banana
		else if(id == 5) {
			icon = "http://maps.google.com/mapfiles/kml/paddle/ylw-blank.png";
		}
		// apple
		else if(id == 6) {
			icon = "http://maps.google.com/mapfiles/kml/paddle/pink-blank.png";
		}
		// nodes
		else {
			icon = "http://maps.google.com/mapfiles/kml/pushpin/purple-pushpin.png";
		}
		return icon;
	}

	
	private String IconId(int id) {
		String ans = "";
		if(id < 5) {
			ans = "Robot-"+id;
		}
		else if(id >=5 && id < 7){
			ans = "Fruit-"+id;
		}
		else {
			ans = "Node";
		}
		return ans;
	}


	
	/**
	 * building the icon format for kml
	 * @param id - (robots/node/fruits)
	 */
	public void icon(int id) {
		Element Style = getDocument().createElement("Style");
		Attr attr = getDocument().createAttribute("id");
		attr.setValue(IconId(id));
		Style.setAttributeNode(attr);
		getGame().appendChild(Style);
		
		Element IconStyle = getDocument().createElement("IconStyle");
		Style.appendChild(IconStyle);
		Element Icon= getDocument().createElement("Icon");
		Element href = getDocument().createElement("href");
		href.appendChild(getDocument().createTextNode(GetIconHref(id)));
		Icon.appendChild(href);
		IconStyle.appendChild(Icon);
		
		Element hotSpot= getDocument().createElement("hotSpot");
		Attr yunits = getDocument().createAttribute("yunits");
		yunits.setValue("pixels");
		Attr xunits = getDocument().createAttribute("xunits");
		xunits.setValue("pixels");
		Attr y = getDocument().createAttribute("y");
		y.setValue("1");
		Attr x = getDocument().createAttribute("x");
		x.setValue("32");
		
		hotSpot.setAttributeNode(x);
		hotSpot.setAttributeNode(y);
		hotSpot.setAttributeNode(xunits);
		hotSpot.setAttributeNode(yunits);

		IconStyle.appendChild(hotSpot);
	}



	/**
	 * building the placemark format for kml
	 * @param id - (robots/node/fruits)
	 * @param posX - coordinates
	 * @param posY - coordinates
	 * @param time - current time
	 */
	public void Placemark(int id, double posX, double posY, String time){
		Element Placemark = getDocument().createElement("Placemark");
		getGame().appendChild(Placemark);
		// nodes should be shown always
		if(id != 7) {
			Element TimeRemaining= getDocument().createElement("TimeStamp");
			Placemark.appendChild(TimeRemaining);
			Element when = getDocument().createElement("when");
			when.appendChild(getDocument().createTextNode(""+time));
			TimeRemaining.appendChild(when);
		}
		Element robot = getDocument().createElement("styleUrl");
		robot.appendChild(getDocument().createTextNode(IconId(id)));
		Placemark.appendChild(robot);
		Element point = getDocument().createElement("Point");
		Placemark.appendChild(point);
		Element coordinates = getDocument().createElement("coordinates");
		coordinates.appendChild(getDocument().createTextNode(""+posX+","+posY+",0.0"));
		point.appendChild(coordinates);
	
	}
	
	/**
	 * create the kml file
	 * transform the DOM Object to an kML File
	 */
	public void KMLtoFile() {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = transformerFactory.newTransformer();

			DOMSource domSource = new DOMSource(getDocument());
			StreamResult streamResult = new StreamResult(new File(get_kmlFilePath()));

			transformer.transform(domSource, streamResult);

			System.out.println("File Saved");
		} catch (TransformerConfigurationException e) {

			e.printStackTrace();

		} catch(TransformerException e) {

			e.printStackTrace();
		}
	}
	
	/**
	 * getting the time in specific foramt for kml
	 * @return string - time 
	 */
	public String currentTime(){
		Date date = new Date();
		DateFormat d1 = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat d2 = new SimpleDateFormat("HH:mm:ss");
		String time1 = d1.format(date);
		String time2 = d2.format(date);
		return time1+"T"+time2+"Z";
		}
	
	 /**
	  * 
	  * @param elem
	  * @return string - convert element to string
	  */
	 public String printKml(Document doc) {
			String output = "";
			try {
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");

				StreamResult result = new StreamResult(new StringWriter());
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, result);

				output = result.getWriter().toString();
				
			} catch (TransformerException e) {
				e.printStackTrace();
			}
			return output;
		}
	 public Document fromtexttodoc() {
		//Get Document Builder
		 Document document =null;
		 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		 DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
	
		  
		 //Build Document
		  try {
			document = builder.parse(new File("data\\kml\\19 moves 574 score 273 - delay 55.kml"));
			 document.getDocumentElement().normalize();
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		 //Normalize the XML Structure; It's just too important !!
		
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return document;
	 }
	
	
	
	/**** private data *****/
	private Element _game;
	private Document _document;
	private String _kmlFilePath;



	/*** getters/setter ***/
	
	private Element getGame() {
		return _game;
	}

	private void setGame(Element _game) {
		this._game = _game;
	}


	public Document getDocument() {
		return _document;
	}


	private void setDocument(Document _document) {
		this._document = _document;
	}


	private String get_kmlFilePath() {
		return _kmlFilePath;
	}


	private void set_kmlFilePath(String _kmlFilePath) {
		this._kmlFilePath = _kmlFilePath;
	}


}
