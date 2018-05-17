package org.ofbiz.pickupdelivery;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.gooddelivery.model.PickupDeliverySolution;
import org.ofbiz.gooddelivery.model.RoutingElement;
import org.ofbiz.gooddelivery.model.RoutingSolution;
import org.ofbiz.utils.DateTimeUtils;

public class PickupDeliveryRequestServiceUtil {
	public static void storeRoute(PickupDeliverySolution sol, Delegator delegator){
		try{
			RoutingSolution[] routes = sol.getRoutes();
			delegator.removeAll("RouteDetail");
			delegator.removeAll("Routes");
			
			for(RoutingSolution rs: routes){
				GenericValue r = delegator.makeValue("Routes");
				String rid = delegator.getNextSeqId("Routes");
				r.put("id", rid);
				r.put("vehicleId", rs.getVehicle().getCode());
				r.put("length", rs.getDistance());
				delegator.create(r);
				
				RoutingElement[] elements = rs.getElements();
				for(int i = 0; i < elements.length; i++){
					RoutingElement e = elements[i];
					GenericValue rd = delegator.makeValue("RouteDetail");
					String rdid = delegator.getNextSeqId("RouteDetail");
					rd.put("routeDetailId", rdid);
					rd.put("routeId", rid);
					rd.put("clientId", e.getCode());
					rd.put("orderId", e.getOrderId());
					long seq = i+1;
					rd.put("sequence", seq);
					if(e.getArrivalTime() != null){
					Timestamp arrivalTime = DateTimeUtils.convertStr2Timestamp(e.getArrivalTime());
					rd.put("arrivalTime", arrivalTime);
					}
					if(e.getDepartureTime() != null){
					Timestamp departureTime = DateTimeUtils.convertStr2Timestamp(e.getDepartureTime());
					
					rd.put("departureTime", departureTime);
					}
					if(e.getDescription() != null) 
						rd.put("description", e.getDescription());
					delegator.create(rd);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static List<Map<String, Object>> getListRequests(Delegator delegator){
		try{
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("pickupClientId",EntityOperator.EQUALS,"Brenntag"));
			
			List<GenericValue> lst = delegator.findList("PickupDeliveryOrderView", 
					EntityCondition.makeCondition(conds),null,null,null,false);
			
			List<Map<String, Object>> retList = FastList.newInstance();
			for(GenericValue o: lst){
				String orderId = o.getString("orderId");
				conds.clear();
				conds.add(EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderId));
				List<GenericValue> items = delegator.findList("PickupDeliveryOrderItem",
						EntityCondition.makeCondition(conds), null,null,null,false);
				
				Map<String, Object> e = FastMap.newInstance();
				e.put("order", o);
				e.put("orderItems", items);
				
				retList.add(e);
			}
			
			return retList;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
}
