package org.ofbiz.pickupdelivery;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

public class PickupDeliveryRequestService {
	public static final String module = PickupDeliveryRequestService.class.getName();
	
	public static void addAPickupDeliveryRequest(HttpServletRequest request, HttpServletResponse response){
		try{
			Delegator delegator = (Delegator)request.getAttribute("delegator");
			String pickupClientId = (String)request.getParameter("pickupClientId");
			String s_earlyPickupDateTime = (String)request.getParameter("earlyPickupDateTime");
			String s_latePickupDateTime = (String)request.getParameter("latePickupDateTime");
			String deliveryClientId = (String)request.getParameter("deliveryClientId");
			String s_earlyDeliveryDateTime = (String)request.getParameter("earlyDeliveryDateTime");
			String s_lateDeliveryDateTime = (String)request.getParameter("lateDeliveryDateTime");
			String s_quantity = (String)request.getParameter("quantity");
			
			Debug.log(module + "::addAPickupDeliveryRequest, s_earlyPickupDateTime = " + s_earlyPickupDateTime +
					", s_latePickupDateTime = " + s_latePickupDateTime +
					", s_earlyDeliveryDateTime = " + s_earlyDeliveryDateTime + 
					", s_lateDeliveryDateTime = " + s_lateDeliveryDateTime +
					", pickupClientId = " + pickupClientId + ", deliveryClientId = " + deliveryClientId+
					", quantity = " + s_quantity);
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		    Date parsedDate = dateFormat.parse(s_earlyPickupDateTime);
		    Timestamp earlyPickupDateTime = new java.sql.Timestamp(parsedDate.getTime());
		    
		    parsedDate = dateFormat.parse(s_latePickupDateTime);
		    Timestamp latePickupDateTime = new java.sql.Timestamp(parsedDate.getTime());
		    
		    parsedDate = dateFormat.parse(s_earlyDeliveryDateTime);
		    Timestamp earlyDeliveryDateTime = new java.sql.Timestamp(parsedDate.getTime());
		    
		    parsedDate = dateFormat.parse(s_lateDeliveryDateTime);
		    Timestamp lateDeliveryDateTime = new java.sql.Timestamp(parsedDate.getTime());
		    
		    
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
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static void getClientPickupDeliveryRequests(HttpServletRequest request, HttpServletResponse response){
		try{
			Delegator delegator = (Delegator)request.getAttribute("delegator");
			List<EntityCondition> conds = FastList.newInstance();
			List<GenericValue> lst = delegator.findList("PickupDeliveryOrderView", 
					null,null,null,null,false);
			
			String json = "{\"requests\":[";
			for(int i = 0; i < lst.size(); i++){
				GenericValue r = lst.get(i);
				json = json + "{"
					+ "\"id\":" + "\"" + r.getString("orderId") +"\""
					+ ",\"pickuplatitude\":" + "" + r.getString("pickuplatitude") +""
					+ ",\"pickuplongitude\":" + "" + r.getString("pickuplongitude") +""
					+ ",\"deliverylatitude\":" + "" + r.getString("deliverylatitude") +""
					+ ",\"deliverylongitude\":" + "" + r.getString("deliverylongitude") +""
					+ "}\n";
				if(i < lst.size()-1)
					json += ",";
			}
			json += "]";
			json += "}";
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(json);
			out.close();
			
			Debug.log(module + "::getClientPickupDeliveryRequests, json = " + json);

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
