package org.ofbiz.pickupdelivery;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.gooddelivery.DeliveryRequestServiceUtil;
import org.ofbiz.gooddelivery.model.ConfigParams;
import org.ofbiz.gooddelivery.model.Depot;
import org.ofbiz.gooddelivery.model.DistanceElement;
import org.ofbiz.gooddelivery.model.PickupDeliveryInput;
import org.ofbiz.gooddelivery.model.PickupDeliveryRequest;
import org.ofbiz.gooddelivery.model.Request;
import org.ofbiz.gooddelivery.model.RoutingDeliveryMultiDepotInput;
import org.ofbiz.gooddelivery.model.Vehicle;

import com.google.gson.Gson;

public class PickupDeliveryRequestService {
	public static final String module = PickupDeliveryRequestService.class
			.getName();

	public static void computePickupDeliveryRoutes(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			List<GenericValue> lstWarehouses = DeliveryRequestServiceUtil
					.getListWarehouses(delegator);
			List<GenericValue> lstRequests = PickupDeliveryRequestServiceUtil
					.getListRequests(delegator);

			PickupDeliveryRequest[] req = new PickupDeliveryRequest[lstRequests
					.size()];
			for (int i = 0; i < lstRequests.size(); i++) {
				GenericValue r = lstRequests.get(i);
				String s_pickupLat = r.getString("pickuplatitude");
				String s_pickupLng = r.getString("pickuplongitude");
				String s_deliveryLat = r.getString("deliverylatitude");
				String s_deliveryLng = r.getString("deliverylongitude");
				
				double pickupLat = Double.valueOf(s_pickupLat);
				double pickupLng = Double.valueOf(s_pickupLng);
				double deliveryLat = Double.valueOf(s_deliveryLat);
				double deliveryLng = Double.valueOf(s_deliveryLat);
				
				String pickupLocationCode = r.getString("pickupGeoPointId");
				String deliveryLocationCode = r.getString("deliveryGeoPointId");
				String orderId = r.getString("orderId");
				Debug.log(module + "::orderId = " + orderId + ", pickupLocationCode= " + pickupLocationCode + ", deliveryLocationCode = " + deliveryLocationCode);
				Timestamp fromDatePickup = r.getTimestamp("fromDatePickup");
				Timestamp thruDatePickup = r.getTimestamp("thruDatePickup");
				Timestamp fromDateDelivery = r.getTimestamp("fromDateDelivery");
				Timestamp thruDateDelivery = r.getTimestamp("thruDateDelivery");

				String earlyPickupTime = fromDatePickup.toString();
				String latePickupTime = thruDatePickup.toString();

				String earlyDeliveryTime = fromDateDelivery.toString();
				String lateDeliveryTime = thruDateDelivery.toString();

				req[i] = new PickupDeliveryRequest(orderId, null, pickupLocationCode, "-",
						 pickupLat, pickupLng, earlyPickupTime, latePickupTime,
						 deliveryLocationCode, "-",  deliveryLat, deliveryLng, earlyDeliveryTime,
						lateDeliveryTime);
			}
			ArrayList<Vehicle> lstVehicles = new ArrayList<Vehicle>();
			Depot[] depots = new Depot[lstWarehouses.size()];
			for (int i = 0; i < lstWarehouses.size(); i++) {
				GenericValue wh = lstWarehouses.get(i);
				String s_lat = wh.getString("latitude");
				String s_lng = wh.getString("longitude");
				double lat = Double.valueOf(s_lat);
				double lng = Double.valueOf(s_lng);
				
				String code = wh.getString("geoPointId");
				Debug.log(module + ":: code warehouse = " + code);	
				Vehicle[] vehicles = new Vehicle[1];

				vehicles[0] = new Vehicle(0, 0, 0, code, lat, lng, 100, code, code);
				
				depots[i] = new Depot(code, lat, lng, vehicles);

				lstVehicles.add(vehicles[0]);
			}

			Vehicle[] vehicles = new Vehicle[lstVehicles.size()];
			for (int i = 0; i < lstWarehouses.size(); i++) {
				vehicles[i] = lstVehicles.get(i);
			}

			HashSet<String> locationCodes = new HashSet<String>();

			for (int i = 0; i < req.length; i++) {
				String fromCode = req[i].getPickupLocationCode();
				String toCode = req[i].getDeliveryLocationCode();
				locationCodes.add(fromCode);
				locationCodes.add(toCode);
				Debug.log(module + "::req[" + i + ", fromCode = " + fromCode + ", toCode = " + toCode);
			}
			for (int i = 0; i < vehicles.length; i++) {
				Vehicle v = vehicles[i];
				String fromCode = v.getStartLocationCode();
				String toCode = v.getEndLocationCode();
				locationCodes.add(fromCode);
				locationCodes.add(toCode);
				Debug.log(module + "::vehicle[" + i + "], fromCode = " + fromCode + ", toCode = " + toCode);
			}
			DistanceElement[] distances = new DistanceElement[locationCodes
					.size() * (locationCodes.size() - 1)];
			int idx = -1;
			for (String fromCode : locationCodes) {
				for (String toCode : locationCodes)
					if (!fromCode.equals(toCode)) {
						GenericValue d = delegator.findOne("Distance", UtilMisc
								.toMap("fromGeoPointId", fromCode,
										"toGeoPointId", toCode), false);
						idx++;
						distances[idx] = new DistanceElement(fromCode, toCode,
								d.getDouble("distance"));

					}
			}
			ConfigParams params = new ConfigParams();
			PickupDeliveryInput input = new PickupDeliveryInput(req, vehicles, params);
			
			// URL url = new URL("http://localhost:8080/DailyOptAPI/basic");
			URL url = new URL(
					"http://localhost:8080/DailyOptAPI/pickup-delivery");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			// String input = "{\"a\":5,\"b\":3}";
			Gson gson = new Gson();
			String json = gson.toJson(input);

			Debug.log(module + "::computePickupDeliveryRoutes, json = " + json);

			OutputStream os = conn.getOutputStream();
			os.write(json.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				// throw new RuntimeException("Failed : HTTP error code : "
				// + conn.getResponseCode());
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
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void addAPickupDeliveryRequest(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			String pickupClientId = (String) request
					.getParameter("pickupClientId");
			String s_earlyPickupDateTime = (String) request
					.getParameter("earlyPickupDateTime");
			String s_latePickupDateTime = (String) request
					.getParameter("latePickupDateTime");
			String deliveryClientId = (String) request
					.getParameter("deliveryClientId");
			String s_earlyDeliveryDateTime = (String) request
					.getParameter("earlyDeliveryDateTime");
			String s_lateDeliveryDateTime = (String) request
					.getParameter("lateDeliveryDateTime");
			String s_quantity = (String) request.getParameter("quantity");

			Debug.log(module
					+ "::addAPickupDeliveryRequest, s_earlyPickupDateTime = "
					+ s_earlyPickupDateTime + ", s_latePickupDateTime = "
					+ s_latePickupDateTime + ", s_earlyDeliveryDateTime = "
					+ s_earlyDeliveryDateTime + ", s_lateDeliveryDateTime = "
					+ s_lateDeliveryDateTime + ", pickupClientId = "
					+ pickupClientId + ", deliveryClientId = "
					+ deliveryClientId + ", quantity = " + s_quantity);

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			Date parsedDate = dateFormat.parse(s_earlyPickupDateTime);
			Timestamp earlyPickupDateTime = new java.sql.Timestamp(
					parsedDate.getTime());

			parsedDate = dateFormat.parse(s_latePickupDateTime);
			Timestamp latePickupDateTime = new java.sql.Timestamp(
					parsedDate.getTime());

			parsedDate = dateFormat.parse(s_earlyDeliveryDateTime);
			Timestamp earlyDeliveryDateTime = new java.sql.Timestamp(
					parsedDate.getTime());

			parsedDate = dateFormat.parse(s_lateDeliveryDateTime);
			Timestamp lateDeliveryDateTime = new java.sql.Timestamp(
					parsedDate.getTime());

			GenericValue r = delegator.makeValue("PickupDeliveryOrder");
			String orderId = delegator.getNextSeqId("PickupDeliveryOrder");

			r.put("orderId", orderId);
			long quantity = Long.valueOf(s_quantity);
			r.put("quantity", quantity);

			r.put("pickupClientId", pickupClientId);
			r.put("deliveryClientId", deliveryClientId);
			r.put("fromDatePickup", earlyPickupDateTime);
			r.put("thruDatePickup", latePickupDateTime);
			r.put("fromDateDelivery", earlyDeliveryDateTime);
			r.put("thruDateDelivery", lateDeliveryDateTime);
			delegator.create(r);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void getClientPickupDeliveryRequests(
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			List<EntityCondition> conds = FastList.newInstance();
			List<GenericValue> lst = delegator.findList(
					"PickupDeliveryOrderView", null, null, null, null, false);

			String json = "{\"requests\":[";
			for (int i = 0; i < lst.size(); i++) {
				GenericValue r = lst.get(i);
				json = json + "{" + "\"id\":" + "\"" + r.getString("orderId")
						+ "\"" + ",\"pickuplatitude\":" + ""
						+ r.getString("pickuplatitude") + ""
						+ ",\"pickuplongitude\":" + ""
						+ r.getString("pickuplongitude") + ""
						+ ",\"deliverylatitude\":" + ""
						+ r.getString("deliverylatitude") + ""
						+ ",\"deliverylongitude\":" + ""
						+ r.getString("deliverylongitude") + "" + "}\n";
				if (i < lst.size() - 1)
					json += ",";
			}
			json += "]";
			json += "}";

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(json);
			out.close();

			Debug.log(module + "::getClientPickupDeliveryRequests, json = "
					+ json);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
