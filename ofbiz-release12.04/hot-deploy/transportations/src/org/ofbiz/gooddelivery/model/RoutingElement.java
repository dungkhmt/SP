package org.ofbiz.gooddelivery.model;

public class RoutingElement {
	private String code;
	private String address;
	private String latlng;
	private double lat;
	private double lng;
	private String arrivalTime;
	private String departureTime;
	private String description;
	private String orderId;
	
	public RoutingElement(String code, String address, String latlng,
			double lat, double lng, String arrivalTime, String departureTime,
			String description, String orderId) {
		super();
		this.code = code;
		this.address = address;
		this.latlng = latlng;
		this.lat = lat;
		this.lng = lng;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.description = description;
		this.orderId = orderId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public RoutingElement(String code, String address, String latlng,
			double lat, double lng, String arrivalTime, String departureTime,
			String description) {
		super();
		this.code = code;
		this.address = address;
		this.latlng = latlng;
		this.lat = lat;
		this.lng = lng;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public String getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
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
	public String getLatlng() {
		return latlng;
	}
	public void setLatlng(String latlng) {
		this.latlng = latlng;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public RoutingElement(String code, String address, String latlng) {
		super();
		this.code = code;
		this.address = address;
		this.latlng = latlng;
	}
	
	public RoutingElement(String code, String address, String latlng,
			double lat, double lng, String arrivalTime, String departureTime) {
		super();
		this.code = code;
		this.address = address;
		this.latlng = latlng;
		this.lat = lat;
		this.lng = lng;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
	}
	public RoutingElement(String code, String address, String latlng,
			double lat, double lng) {
		super();
		this.code = code;
		this.address = address;
		this.latlng = latlng;
		this.lat = lat;
		this.lng = lng;
	}
	public RoutingElement() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
