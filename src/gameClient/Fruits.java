package gameClient;

import org.json.JSONException;
import org.json.JSONObject;

public class Fruits {

	public Fruits(String f) {
		init(f);
		//setIsTarget(false);
	}
	
	
	// empty constructor
	public Fruits() {
		setType(1);
		setValue(0);
		setPosX(0);
		setPosY(0);
		//setIsTarget(false);
	}



	/**
	 * init new fruit from string
	 * @param g - string with json format
	 */
	public void init(String f) {
		String pos;
		String split[];
		int type;
		double value;
		try {
			JSONObject obj = new JSONObject(f);
			JSONObject robot_param = obj.getJSONObject("Fruit");
				value = (double) robot_param.getDouble("value");
				type =  (int) robot_param.getInt("type");
				pos = (String) robot_param.getString("pos");
				split = pos.split(",");
				

				setValue(value);
				setType(type);
				setPosX(Double.parseDouble(split[0]));
				setPosY(Double.parseDouble(split[1]));
			//	setIsTarget(false);

				
		} catch (JSONException e) {e.printStackTrace();}

	}
	/*** private data ***/

	private boolean _onTarget;
	private int _type;
	private double _value, _posX, _posY;
	
	/*** getter/setters ***/
	

	public boolean isTarget() {
		return _onTarget;
	}

	public void setIsTarget(boolean _onTarget) {
		this._onTarget = _onTarget;
	}
	
	public int getType() {
		return _type;
	}
	public double getValue() {
		return _value;
	}
	public double getPosX() {
		return _posX;
	}
	public double getPosY() {
		return _posY;
	}
	private void setType(int _type) {
		this._type = _type;
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


}
