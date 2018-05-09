package org.ofbiz.pickupdelivery;
import java.util.List;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

public class PickupDeliveryRequestServiceUtil {
	public static List<GenericValue> getListRequests(Delegator delegator){
		try{
			List<GenericValue> lst = delegator.findList("PickupDeliveryOrderView", 
					null,null,null,null,false);
			return lst;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
}
