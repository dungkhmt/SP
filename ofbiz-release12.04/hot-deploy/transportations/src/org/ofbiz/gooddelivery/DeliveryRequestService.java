package org.ofbiz.gooddelivery;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.gooddelivery.model.ConfigParams;
import org.ofbiz.gooddelivery.model.Depot;
import org.ofbiz.gooddelivery.model.DistanceElement;
import org.ofbiz.gooddelivery.model.Request;
import org.ofbiz.gooddelivery.model.RoutingDeliveryMultiDepotInput;
import org.ofbiz.gooddelivery.model.RoutingLoad3DInput;
import org.ofbiz.gooddelivery.model.Vehicle;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.google.gson.Gson;


public class DeliveryRequestService {
	
	public static final String module = DeliveryRequestService.class.getName();
	
	
	public static Map<String, Object> computeUpdateAllDistanceDB(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		DeliveryRequestServiceUtil.computeUpdateAllDistanceDB(delegator);
		return retSucc;
	}
	public static void computeDeliveryRoutes(HttpServletRequest request, 
			HttpServletResponse response){
		try{
			Delegator delegator = (Delegator)request.getAttribute("delegator");
			List<GenericValue> lstWarehouses = DeliveryRequestServiceUtil.getListWarehouses(delegator);
			List<GenericValue> lstRequests = DeliveryRequestServiceUtil.getListDeliveryRequests(delegator);
			
			Request[] req = new Request[lstRequests.size()];
			for(int i = 0; i < req.length; i++){
				GenericValue gv = lstRequests.get(i);
				double lat = (double)gv.get("latitude");
				double lng = (double)gv.get("longitude");
				String orderID = "DO-" + gv.getString("deliveryOrderId");
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				Timestamp e = (Timestamp)gv.get("fromDate");
				Timestamp l = (Timestamp)gv.get("thruDate");
				
				String earlyDeliveryTime = e.toString();
				String lateDeliveryTime = l.toString();
				req[i] = new Request("-", lat, lng, orderID, null, earlyDeliveryTime, lateDeliveryTime);
			}
			ArrayList<Vehicle> lstVehicles = new ArrayList<Vehicle>();
			Depot[] depots = new Depot[lstWarehouses.size()];
			for(int i = 0; i < lstWarehouses.size(); i++){
				GenericValue wh = lstWarehouses.get(i);
				double lat = wh.getDouble("latitude");
				double lng = wh.getDouble("longitude");
				String code = wh.getString("warehouseId");
				
				Vehicle[] vehicles = new Vehicle[1];
				
				vehicles[0] = new Vehicle(0,0,0,code,lat,lng);
				depots[i] = new Depot(code,lat,lng,vehicles);
				
				lstVehicles.add(vehicles[0]);
			}

			Vehicle[] vehicles = new Vehicle[lstVehicles.size()];
			for(int i = 0; i < lstWarehouses.size(); i++){
				vehicles[i] = lstVehicles.get(i);
			}
			
			int N = req.length + vehicles.length;
			DistanceElement[] distances = new DistanceElement[N*(N-1)];
			int idx = -1;
			for(int i = 0; i < req.length; i++){
				String codei = req[i].getOrderID();
				for(int j = 0; j < req.length; j++)if(i != j){
					String codej = req[j].getOrderID();
					idx++;
					double d = DeliveryRequestServiceUtil.getMahattanDistance(req[i].getLat(), req[i].getLng(), 
							req[j].getLat(),req[j].getLng());
					distances[idx]= new DistanceElement(codei, codej, d);
				}
				for(int j = 0; j < vehicles.length; j++){
					String codej = vehicles[j].getCode();
					idx++;
					double lat = (double)lstWarehouses.get(j).get("latitude");
					double lng = (double)lstWarehouses.get(j).get("longitude");
					double d = DeliveryRequestServiceUtil.getMahattanDistance(req[i].getLat(), req[i].getLng(), 
							lat, lng);
					distances[idx] = new DistanceElement(codei, codej, d);
				}
			}
			for(int i = 0; i < vehicles.length; i++){
				String codei = vehicles[i].getCode();
				double lati = (double)lstWarehouses.get(i).get("latitude");
				double lngi = (double)lstWarehouses.get(i).get("longitude");
			 
				for(int j = 0; j < req.length; j++){
					String codej = req[j].getOrderID();
					idx++;
					double d = DeliveryRequestServiceUtil.getMahattanDistance(lati, lngi, 
							req[j].getLat(),req[j].getLng());
					distances[idx]= new DistanceElement(codei, codej, d);
				}
				for(int j = 0; j < vehicles.length; j++)if(i != j){
					String codej = vehicles[j].getCode();
					idx++;
					double latj = (double)lstWarehouses.get(j).get("latitude");
					double lngj = (double)lstWarehouses.get(j).get("longitude");
					double d = DeliveryRequestServiceUtil.getMahattanDistance(lati, lngi, 
							latj, lngj);
					distances[idx] = new DistanceElement(codei, codej, d);
				}
			}
			
			ConfigParams configParams = new ConfigParams();
			//RoutingLoad3DInput input = new RoutingLoad3DInput(req, vehicles, distances, null, vehicles.length, configParams);
			RoutingDeliveryMultiDepotInput input = new RoutingDeliveryMultiDepotInput(req, distances, depots, configParams);
			
			
			//URL url = new URL("http://localhost:8080/DailyOptAPI/basic");
			URL url = new URL("http://localhost:8080/DailyOptAPI/cvrp-timewindows");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			
			
			//String input = "{\"a\":5,\"b\":3}";
			Gson gson = new Gson();
			String json = gson.toJson(input);
			
			Debug.log(module + "::computeDeliveryRoutes, json = " + json);
					
			OutputStream os = conn.getOutputStream();
			os.write(json.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				//throw new RuntimeException("Failed : HTTP error code : "
				//	+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String rs = "";
			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				rs += output;
			}

			conn.disconnect();
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(rs);
			out.close();
			System.out.println("RETURN json = " + rs);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static void addAWarehouse(HttpServletRequest request, 
			HttpServletResponse response){
		try{
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			String s_location = (String)request.getParameter("location");
			String name = (String)request.getParameter("name");
			
			if(s_location != null && s_location.length() > 0){
				s_location = s_location.substring(1, s_location.length()-1);
				String[] s = s_location.split(",");
				Debug.log(module + "::addAWarehouse, s_location = " + s_location);
				double lat = Double.valueOf(s[0]);
				double lng = Double.valueOf(s[1]);
				GenericValue gp = delegator.makeValue("GeoPoint");
				String geoPointId = delegator.getNextSeqId("GeoPoint");
				gp.put("geoPointId", geoPointId);
				gp.put("latitude", s[0]);
				gp.put("longitude", s[1]);
				
				delegator.create(gp);
				
				DeliveryRequestServiceUtil.updateDistance(delegator, geoPointId, lat, lng);
				
				
				GenericValue wh = delegator.makeValue("Warehouse");
				String warehouseId = delegator.getNextSeqId("Warehouse");
				wh.put("warehouseId", warehouseId);
				wh.put("warehouseName", name);
				wh.put("geoPointId", geoPointId);
				
				delegator.create(wh);
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static void addAClient(HttpServletRequest request, 
			HttpServletResponse response){
		try{
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			String s_location = (String)request.getParameter("location");
			String name = (String)request.getParameter("name");
			
			if(s_location != null && s_location.length() > 0){
				s_location = s_location.substring(1, s_location.length()-1);
				String[] s = s_location.split(",");
				Debug.log(module + "::addAClient, s_location = " + s_location);
				double lat = Double.valueOf(s[0]);
				double lng = Double.valueOf(s[1]);
				GenericValue gp = delegator.makeValue("GeoPoint");
				String geoPointId = delegator.getNextSeqId("GeoPoint");
				gp.put("geoPointId", geoPointId);
				gp.put("latitude", s[0]);
				gp.put("longitude", s[1]);
				
				delegator.create(gp);
				
				DeliveryRequestServiceUtil.updateDistance(delegator, geoPointId, lat, lng);
				
				
				GenericValue cl = delegator.makeValue("Client");
				String clientId = delegator.getNextSeqId("Client");
				cl.put("clientId", clientId);
				cl.put("clientName", name);
				cl.put("geoPointId", geoPointId);
				
				delegator.create(cl);
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static void addAVehicle(HttpServletRequest request, 
			HttpServletResponse response){
		try{
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			String warehouseId = (String)request.getParameter("warehouseId");
			String name = (String)request.getParameter("name");
			String s_height = (String)request.getParameter("height");
			String s_length = (String)request.getParameter("length");
			String s_weight = (String)request.getParameter("weight");
			String s_width = (String)request.getParameter("width");
			String s_startWorkingTime = (String)request.getParameter("startWorkingTime");
			String s_endWorkingTime = (String)request.getParameter("endWorkingTime");

			Debug.log(module + "::addAVehicle, warehouseId = " + warehouseId + ", s_height = " + s_height +
					", s_length = " + s_length + ", s_weight= " + s_weight + ", s_width = " + s_width
					+ ", s_startWorkingTime = " + s_startWorkingTime + ", s_endWorkingTime = " + s_endWorkingTime);
			
			double height = Double.valueOf(s_height);
			double weight = Double.valueOf(s_weight);
			double length = Double.valueOf(s_length);
			double width = Double.valueOf(s_width);
			
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			Date parsedDate = dateFormat.parse(s_startWorkingTime);
			Timestamp startWorkingTime = new java.sql.Timestamp(
					parsedDate.getTime());

			parsedDate = dateFormat.parse(s_endWorkingTime);
			Timestamp endWorkingTime = new java.sql.Timestamp(
					parsedDate.getTime());

			
				
				
				
				GenericValue v = delegator.makeValue("Vehicle");
				String vehicleId = delegator.getNextSeqId("Vehicle");
				v.put("vehicleId", vehicleId);
				v.put("warehouseId", warehouseId);
				v.put("name", name);
				v.put("height", height);
				v.put("length", length);
				v.put("width", width);
				v.put("weight", weight);
				v.put("startWorkingTime", startWorkingTime);
				v.put("endWorkingTime", endWorkingTime);
				
				
				delegator.create(v);
				
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void addADeliveryRequest(HttpServletRequest request, 

			HttpServletResponse response){
		try{
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			String s_quantity = (String)request.getParameter("quantity");
			String clientId = (String)request.getParameter("clientId");
			String s_early_date_time = (String)request.getParameter("earlyDateTime");
			String s_late_date_time = (String)request.getParameter("lateDateTime");
			
			GenericValue client = delegator.findOne("ClientView",UtilMisc.toMap("clientId",clientId),false);
			
			Debug.log(module + "::addADeliveryRequest, quantity = " + s_quantity + ", clientId = " + clientId
					+ ", s_early_date_time = " + s_early_date_time + ", s_late_date_time = " + s_late_date_time);
			
			long quantity = 1;
			if(s_quantity != null && s_quantity.length() > 0){
				quantity = Long.valueOf(s_quantity);
			}
			
			GenericValue gv = delegator.makeValue("DeliveryOrder");
			String deliveryOrderId = delegator.getNextSeqId("DeliveryOrder");
			gv.put("deliveryOrderId", deliveryOrderId);
			gv.put("quantity", Long.valueOf(quantity));
			gv.put("clientId", clientId);
			
			/*
			if(s_location != null && s_location.length() > 0){
				s_location = s_location.substring(1, s_location.length()-1);
				String[] s = s_location.split(",");
				Debug.log(module + "::addADeliveryRequest, s_location = " + s_location);
				double lat = Double.valueOf(s[0]);
				double lng = Double.valueOf(s[1]);
				gv.put("latitude", lat);
				gv.put("longitude", lng);
			}
			*/
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		    Date parsedDate = dateFormat.parse(s_early_date_time);
		    Timestamp early_date_time = new java.sql.Timestamp(parsedDate.getTime());
		    gv.put("fromDate", early_date_time);
		    parsedDate = dateFormat.parse(s_late_date_time);
		    Timestamp late_date_time = new java.sql.Timestamp(parsedDate.getTime());
		    gv.put("thruDate", late_date_time);
		    
			
			delegator.create(gv);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static Map<String, Object> getWarehouses(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try{
			//List<GenericValue> lst = delegator.findList("Warehouse", null,null,null,null,false);
			List<GenericValue> lst = DeliveryRequestServiceUtil.getListWarehouses(delegator);
			retSucc.put("listWarehouses", lst);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return retSucc;
	}
	public static void getDistance(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		String fromPoint = request.getParameter("fromPoint");
		String toPoint = request.getParameter("toPoint");
		Debug.log(module + "::getDistance, fromPoint = " + fromPoint + ", toPoint = " + toPoint);
		try{
			GenericValue dis = delegator.findOne("Distance", UtilMisc.toMap("fromGeoPointId",fromPoint,"toGeoPointId",toPoint),false);
			double d = -1;
			if(dis != null){
				d = dis.getDouble("distance");
			}
			String rs = "{\"distance\":";
			rs += d;
			rs += "}";
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(rs);
			out.close();
			
			Debug.log(module + "::getCliets, json = " + rs);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static void getClients(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		try{
			//List<GenericValue> lst = delegator.findList("Warehouse", null,null,null,null,false);
			List<GenericValue> lst = DeliveryRequestServiceUtil.getListClients(delegator);
			
			String rs = "{\"clients\":[";
			for (int i = 0; i < lst.size(); i++) {
				GenericValue st = lst.get(i);
				rs += "{\"id\":\"" + st.get("clientId") + "\""
						+",\"name\":\""	+ st.get("clientName") + "\"" 
						+ ",\"latitude\":" + st.get("latitude") + ""
						+ ",\"longitude\":" + st.get("longitude") + ""
						+ ",\"geoPointId\":\"" + st.get("geoPointId") + "\""
						+ "}\n";
				if (i < lst.size() - 1)
					rs += ",";

				
			}
			rs += "]}";
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(rs);
			out.close();
			
			Debug.log(module + "::getCliets, json = " + rs);

		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

	public static void getClientRequests(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		try{
			//List<GenericValue> lst = delegator.findList("Warehouse", null,null,null,null,false);
			List<Map<String, Object>> lst = DeliveryRequestServiceUtil.getListClientRequests(delegator);
			
			String rs = "{\"clients\":[";
			for (int i = 0; i < lst.size(); i++) {
				Map<String, Object> st = lst.get(i);
				rs += "{\"id\":\"" + st.get("clientId") + "\""
						+",\"name\":\""	+ st.get("clientName") + "\"" 
						+ ",\"latitude\":" + st.get("latitude") + ""
						+ ",\"longitude\":" + st.get("longitude") + ""
						+ ",\"hasRequest\":\"" + st.get("hasRequest") + "\""
						+ "}\n";
				if (i < lst.size() - 1)
					rs += ",";

				
			}
			rs += "]}";
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(rs);
			out.close();
			
			Debug.log(module + "::getCliets, json = " + rs);

		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

	public static void getWarehouses(HttpServletRequest request, HttpServletResponse response){
		try{
			Delegator delegator = (Delegator)request.getAttribute("delegator");
			//List<GenericValue> lst = delegator.findList("Warehouse", null,null,null,null,false);
			List<GenericValue> lst = DeliveryRequestServiceUtil.getListWarehouses(delegator);
			
			String rs = "{\"warehouses\":[";
			for (int i = 0; i < lst.size(); i++) {
				GenericValue st = lst.get(i);
				rs += "{\"id\":\"" + st.get("warehouseId") + "\""
						+",\"name\":\""	+ st.get("warehouseName") + "\"" 
						+ ",\"latitude\":" + st.get("latitude") + ""
						+ ",\"longitude\":" + st.get("longitude") + ""
						+ ",\"geoPointId\":\"" + st.get("geoPointId") + "\""
						+ "}\n";
				if (i < lst.size() - 1)
					rs += ",";

				
			}
			rs += "]}";
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(rs);
			out.close();
			
			Debug.log(module + "::getWarehouses, json = " + rs);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static Map<String, Object> getDeliveryRequests(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try{
			//List<GenericValue> lst = delegator.findList("DeliveryOrder", null,null,null,null,false);
			List<GenericValue> lst = DeliveryRequestServiceUtil.getListDeliveryRequests(delegator);
			retSucc.put("listRequests", lst);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return retSucc;
	}

	public static void getDeliveryRequests(HttpServletRequest request, HttpServletResponse response){
		try{
			Delegator delegator = (Delegator)request.getAttribute("delegator");
			//List<GenericValue> lst = delegator.findList("DeliveryOrder", null,null,null,null,false);
			List<GenericValue> lst = DeliveryRequestServiceUtil.getListDeliveryRequests(delegator);
			
			String rs = "{\"deliveryrequests\":[";
			for (int i = 0; i < lst.size(); i++) {
				GenericValue st = lst.get(i);
				rs += "{\"id\":\"" + st.get("deliveryOrderId") + "\""
						+",\"quantity\":\""	+ st.get("quantity") + "\"" 
						+ ",\"latitude\":" + st.get("latitude") + ""
						+ ",\"longitude\":" + st.get("longitude") + ""
						+ ",\"geoPointId\":\"" + st.get("geoPointId") + "\""
						+ "}\n";
				if (i < lst.size() - 1)
					rs += ",";

				
			}
			rs += "]}";
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(rs);
			out.close();
			
			Debug.log(module + "::getWarehouses, json = " + rs);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
