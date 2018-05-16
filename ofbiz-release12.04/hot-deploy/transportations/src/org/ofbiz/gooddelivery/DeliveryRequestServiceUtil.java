package org.ofbiz.gooddelivery;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.utils.googlemaps.GoogleMapsQuery;

public class DeliveryRequestServiceUtil {

	public static final String module = DeliveryRequestServiceUtil.class
			.getName();

	public static double getMahattanDistance(double lat1, double lng1,
			double lat2, double lng2) {
		return Math.abs(lat1 - lat2) + Math.abs(lng1 - lng2);
	}

	public static void computeUpdateAllDistanceDB(Delegator delegator) {
		try {
			List<GenericValue> lst = delegator.findList("GeoPoint", null, null,
					null, null, false);
			Debug.log(module + "::computeUpdateAllDistanceDB, lst.sz = " + lst.size());
			
			for (int i = 0; i < lst.size(); i++) {
				GenericValue pi = lst.get(i);
				String fromId = pi.getString("geoPointId");
				String lat_i = pi.getString("latitude");
				String lng_i = pi.getString("longitude");
				double lat1 = Double.valueOf(lat_i);
				double lng1 = Double.valueOf(lng_i);
				for (int j = 0; j < lst.size(); j++)
					if (i != j) {
						GenericValue pj = lst.get(j);
						String toId = pj.getString("geoPointId");
						String lat_j = pj.getString("latitude");
						String lng_j = pj.getString("longitude");
						double lat2 = Double.valueOf(lat_j);
						double lng2 = Double.valueOf(lng_j);
						double d = GoogleMapsQuery.computeDistanceHaversine(
								lat1, lng1, lat2, lng2);

						GenericValue dis = delegator.findOne("Distance",
								UtilMisc.toMap("fromGeoPointId", fromId,
										"toGeoPointId", toId), false);
						if (dis != null) {
							dis.put("distance", d);
							delegator.store(dis);
						} else {
							dis = delegator.makeValue("Distance");
							dis.put("fromGeoPointId", fromId);
							dis.put("toGeoPointId", toId);
							dis.put("distance", d);
							delegator.create(dis);
						}
						Debug.log(module + "::computeUpdateAllDistanceDB, " + i
								+ " --> " + j + ", distance = " + d);
					}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void updateDistance(Delegator delegator, String geoPointId,
			double lat, double lng) {
		try {
			List<GenericValue> lst = delegator.findList("GeoPoint", null, null,
					null, null, false);
			for (GenericValue gp : lst) {
				String id = gp.getString("geoPointId");
				if (id.equals(geoPointId))
					continue;
				// GenericValue dd = delegator.findOne("Distance",
				// UtilMisc.toMap("fromGeoPointId",id,"toGeoPointId",geoPointId),
				// false);

				String s_lat = gp.getString("latitude");
				String s_lng = gp.getString("longitude");
				double i_lat = Double.valueOf(s_lat);
				double i_lng = Double.valueOf(s_lng);

				// double d = getMahattanDistance(lat, lng, i_lat, i_lng);
				double d = GoogleMapsQuery.computeDistanceHaversine(lat, lng,
						i_lat, i_lng);

				GenericValue dis = delegator.makeValue("Distance");
				dis.put("fromGeoPointId", geoPointId);
				dis.put("toGeoPointId", id);
				dis.put("distance", d);
				delegator.create(dis);

				dis = delegator.makeValue("Distance");
				dis.put("fromGeoPointId", id);
				dis.put("toGeoPointId", geoPointId);
				dis.put("distance", d);
				delegator.create(dis);

				Debug.log(module + "::updateDistance, from " + geoPointId
						+ " to " + id + ", distance " + d);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static List<GenericValue> getListWarehouses(Delegator delegator) {
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("geoPointId",
					EntityOperator.NOT_EQUAL, null));

			List<GenericValue> lst = delegator.findList("WarehouseView",
					EntityCondition.makeCondition(conds), null, null, null,
					false);
			return lst;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static List<GenericValue> getListVehicleOfWarehouse(Delegator delegator, String warehouseId) {
		try {
			
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("warehouseId",
					EntityOperator.EQUALS, warehouseId));

			List<GenericValue> lst = delegator.findList("Vehicle",
					EntityCondition.makeCondition(conds), null, null, null,
					false);
			return lst;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static List<GenericValue> getListClients(Delegator delegator) {
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("geoPointId",
					EntityOperator.NOT_EQUAL, null));

			List<GenericValue> lst = delegator.findList("ClientView",
					EntityCondition.makeCondition(conds), null, null, null,
					false);
			return lst;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static List<Map<String, Object>> getListClientRequests(
			Delegator delegator) {
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("geoPointId",
					EntityOperator.NOT_EQUAL, null));

			List<GenericValue> lst = delegator.findList("ClientView",
					EntityCondition.makeCondition(conds), null, null, null,
					false);

			List<Map<String, Object>> retList = FastList.newInstance();

			for (GenericValue c : lst) {
				String clientId = c.getString("clientId");
				conds.clear();
				conds.add(EntityCondition.makeCondition("clientId",
						EntityOperator.EQUALS, clientId));
				List<GenericValue> lr = delegator.findList("DeliveryOrder",
						EntityCondition.makeCondition(conds), null, null, null,
						false);
				Map<String, Object> o = FastMap.newInstance();
				o.put("clientId", clientId);
				o.put("clientName", c.getString("clientName"));
				o.put("latitude", c.getString("latitude"));
				o.put("longitude", c.getString("longitude"));
				if (lr != null && lr.size() > 0) {
					o.put("hasRequest", "Y");
				} else {
					o.put("hasRequest", "N");
				}
				retList.add(o);
			}
			return retList;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static List<GenericValue> getListDeliveryRequests(Delegator delegator) {
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("clientId",EntityOperator.NOT_EQUAL,null));
			List<GenericValue> lst = delegator.findList("DeliveryOrderView", EntityCondition.makeCondition(conds),
					null, null, null, false);
			return lst;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
