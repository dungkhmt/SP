package org.ofbiz.gooddelivery.model;

public class Vehicle {
	private int width;
	private int length;
	private int height;
	private String code;
	private double lat;
	private double lng;
	private int weight;
	private String startLocationCode;
	private String endLocationCode;
	private String startWorkingTime;
	private String endWorkingTime;
	
	
	public Vehicle(int width, int length, int height, String code, double lat,
			double lng, int weight, String startLocationCode,
			String endLocationCode, String startWorkingTime,
			String endWorkingTime) {
		super();
		this.width = width;
		this.length = length;
		this.height = height;
		this.code = code;
		this.lat = lat;
		this.lng = lng;
		this.weight = weight;
		this.startLocationCode = startLocationCode;
		this.endLocationCode = endLocationCode;
		this.startWorkingTime = startWorkingTime;
		this.endWorkingTime = endWorkingTime;
	}
	public String getStartWorkingTime() {
		return startWorkingTime;
	}
	public void setStartWorkingTime(String startWorkingTime) {
		this.startWorkingTime = startWorkingTime;
	}
	public String getEndWorkingTime() {
		return endWorkingTime;
	}
	public void setEndWorkingTime(String endWorkingTime) {
		this.endWorkingTime = endWorkingTime;
	}
	public Vehicle(int width, int length, int height, String code, double lat,
			double lng, int weight, String startLocationCode,
			String endLocationCode) {
		super();
		this.width = width;
		this.length = length;
		this.height = height;
		this.code = code;
		this.lat = lat;
		this.lng = lng;
		this.weight = weight;
		this.startLocationCode = startLocationCode;
		this.endLocationCode = endLocationCode;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public String getStartLocationCode() {
		return startLocationCode;
	}
	public void setStartLocationCode(String startLocationCode) {
		this.startLocationCode = startLocationCode;
	}
	public String getEndLocationCode() {
		return endLocationCode;
	}
	public void setEndLocationCode(String endLocationCode) {
		this.endLocationCode = endLocationCode;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public Vehicle() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Vehicle(int width, int length, int height, String code, double lat,
			double lng) {
		super();
		this.width = width;
		this.length = length;
		this.height = height;
		this.code = code;
		this.lat = lat;
		this.lng = lng;
	}
	
}
