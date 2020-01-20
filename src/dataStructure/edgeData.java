package dataStructure;

import java.io.Serializable;

/**
 * 
 * @author Ben itzhak
 * @author Liad ben moshe
 */
public class edgeData implements edge_data, Serializable {

	public edgeData(nodeData src, nodeData dest,double weight) {
		set_Src(src);
		set_Dest(dest);
		setInfo("");
		setTag(0);
		set_Weight(weight);
	}

	@Override
	public int getSrc() {
		return this._Src.getKey();
	}
	
	public nodeData getNodeSrc() {
		return this._Src;
	}

	@Override
	public int getDest() {
		return this._Dest.getKey();
	}
	
	public nodeData getNodeDest() {
		return this._Dest;
	}

	@Override
	public double getWeight() {
		return this._Weight;
	}


	@Override
	public String getInfo() {

		return this._Info;
	}

	@Override
	public void setInfo(String s) {
		this._Info=s;
	}

	@Override
	public int getTag() {

		return this._Tag;
	}

	@Override
	public void setTag(int t) {
		this._Tag=t;

	}
	/******* private data ********/
	private void set_Src(nodeData _Src) {
		this._Src = _Src;
	}

	private void set_Dest(nodeData _Dest) {
		this._Dest = _Dest;
	}

	private void set_Weight(double _Weight) {
		this._Weight = _Weight;
	}
	
	private nodeData _Src;
	private nodeData _Dest;
	private double _Weight;
	private String _Info;
	private int _Tag;

}
