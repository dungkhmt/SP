package org.ofbiz.gooddelivery;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

public class DeliveryRequestServiceUtil {

		public static final String module = DeliveryRequestServiceUtil.class.getName();
		
		
		public static double getMahattanDistance(double lat1, double lng1, double lat2, double lng2){
			return Math.abs(lat1-lat2) + Math.abs(lng1 - lng2);
		}
		public static List<GenericValue> getListWarehouses(Delegator delegator){
			try{
				List<GenericValue> lst = delegator.findList("Warehouse", null,null,null,null,false);
				return lst;
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return null;
		}

		public static List<GenericValue> getListDeliveryRequests(Delegator delegator){
			try{
				List<GenericValue> lst = delegator.findList("DeliveryOrder", null,null,null,null,false);
				return lst;
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return null;
		}

}
