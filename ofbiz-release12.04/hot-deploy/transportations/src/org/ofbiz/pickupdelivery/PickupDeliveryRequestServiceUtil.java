package org.ofbiz.pickupdelivery;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

public class PickupDeliveryRequestServiceUtil {
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
