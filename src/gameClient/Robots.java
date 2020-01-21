package gameClient;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import dataStructure.node_data;

public class Robots {



	public Robots(String r) {
		init(r);
	}

	// empty constructor
	public Robots() {
		setId(0);
		setSrc(0);
		setDest(-1);
		setValue(0);
		setPosX(0);
		setPosY(0);
		setSpeed(0);
		
	}

	/**
	 * init new robot from string
	 * @param g - string with json format
	 */
	public void init(String r) {
		String pos;
		String split[];
		int id, src, dest;
		double value, speed;
		try {
			JSONObject obj = new JSONObject(r);
			JSONObject robot_param = obj.getJSONObject("Robot");
			id =  (int) robot_param.getInt("id");
			value = (double) robot_param.getDouble("value");
			src = (int) robot_param.getInt("src");
			dest = (int) robot_param.getInt("dest");
			speed = (double) robot_param.getDouble("speed");
			pos = (String) robot_param.getString("pos");
			split = pos.split(",");

			setId(id);
			setValue(value);
			setSrc(src);
			setDest(dest);
			setSpeed(speed);
			setPosX(Double.parseDouble(split[0]));
			setPosY(Double.parseDouble(split[1]));

		} catch (JSONException e) {e.printStackTrace();}

	}


/**** private data ***/

	private Fruits _target;
	private ArrayList<node_data> _nextDest;
	private int _id, _src, _dest;
	private double _value, _posX, _posY;
	private double _speed;
	
	/*** getters/setters ***/
	
	public Fruits getTarget() {
		return _target;
	}

	public void setTarget(Fruits _target) {
		this._target = _target;
	}
	
	public ArrayList<node_data> getNextDest() {
		return _nextDest;
	}

	public void setNextDest(ArrayList<node_data> list) {
		this._nextDest = list;
	}

	public int getId() {
		return _id;
	}
	public int getSrc() {
		return _src;
	}
	public int getDest() {
		return _dest;
	}

	public double getPosX() {
		return _posX;
	}
	public double getPosY() {
		return _posY;
	}
	public double getSpeed() {
		return _speed;
	}
	public double getValue() {
		return _value;
	}
	
	private void setId(int _id) {
		this._id = _id;
	}
	private void setSrc(int _src) {
		this._src = _src;
	}
	private void setDest(int _dest) {
		this._dest = _dest;
	}
	private void setValue(double _value) {
		this._value = _value;
	}
	private void setPosX(double _posX) {
		this._posX = _posX;
	}
	private void setPosY(double _posY) {
		this._posY = _posY;
	}
	private void setSpeed(double _speed) {
		this._speed = _speed;
	}
}
