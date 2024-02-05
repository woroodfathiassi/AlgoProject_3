package application;

import java.util.ArrayList;

import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class CityNode_vertex implements Comparable<CityNode_vertex> {
	private String name;
	private boolean known;
	private double longitude;
	private double latitude;
	private double distancee;
	private CityNode_vertex path;
	private ArrayList<Adjacent> listOFAdj = new ArrayList<>();

	private double x;
	private double y;

	private double minLat = 31.593939268575955;
	private double maxLat = 31.193779320786113;
	private double minLong = 34.171357309110164;
	private double maxLong = 34.58680345584482;
	private double mapWIdth = 742;
	private double mapHeight = 673;

	public CityNode_vertex() {

	}

	public CityNode_vertex(String name, boolean known, double longitude, double latitude, double distancee,
			CityNode_vertex path) {
		super();
		this.name = name;
		this.known = known;
		this.longitude = longitude;
		this.latitude = latitude;
		this.distancee = distancee;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isKnown() {
		return known;
	}

	public void setKnown(boolean known) {
		this.known = known;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getDistancee() {
		return distancee;
	}

	public void setDistancee(double distancee) {
		this.distancee = distancee;
	}

	public CityNode_vertex getPath() {
		return path;
	}

	public void setPath(CityNode_vertex path) {
		this.path = path;
	}

	public ArrayList<Adjacent> getListOFAdj() {
		return listOFAdj;
	}

	public void setListOFAdj(ArrayList<Adjacent> listOFAdj) {
		this.listOFAdj = listOFAdj;
	}

	@Override
	public int compareTo(CityNode_vertex o) {
		// TODO Auto-generated method stub
		if (this.distancee > o.distancee)
			return 1;
		else if (this.distancee < o.distancee)
			return -1;
		else
			return 0;
	}

	// convert longitude to pixel coordinates using linear interpolation
	public double getX() {
		double x = this.mapWIdth * ((this.longitude - this.minLong) / (this.maxLong - this.minLong));

		return x;
	}

	// convert latitude to pixel coordinates using linear interpolation
	public double getY() {
		double y = this.mapHeight * ((this.latitude - this.minLat) / (this.maxLat - this.minLat));

		return y;
	}

}
