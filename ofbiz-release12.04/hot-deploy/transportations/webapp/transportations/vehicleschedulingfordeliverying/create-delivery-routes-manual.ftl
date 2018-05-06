<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Vehicle routing</title>
<script type="text/javascript" src="https://maps.google.com/maps/api/js?key=AIzaSyBdyNiBOdg6ikZli6MhG3ivZRw2fKdW-5I&sensor=true&libraries=geometry"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>


    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>SB Admin 2 - Bootstrap Admin Theme</title>
	<!-- date-time picker -->
	<!--
	<link href="/resource/transportations/bootstrap-datetimepicker/bootstrapv2/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen">
    -->
    <link href="/resource/transportations/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" rel="stylesheet" media="screen">
    
    <!-- Bootstrap Core CSS -->
    <link href="/resource/transportations/bootstrap/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- MetisMenu CSS -->
    <link href="/resource/transportations/bootstrap/vendor/metisMenu/metisMenu.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="/resource/transportations/bootstrap/dist/css/sb-admin-2.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="/resource/transportations/bootstrap/vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

	<script type="text/javascript" src="/resource/transportations/bootstrap-datetimepicker/bootstrapv2/jquery/jquery-1.8.3.min.js" charset="UTF-8"></script>
	<script type="text/javascript" src="/resource/transportations/bootstrap-datetimepicker/bootstrapv2/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="/resource/transportations/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js" charset="UTF-8"></script>
	<script type="text/javascript" src="/resource/transportations/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.fr.js" charset="UTF-8"></script>
	
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>


<#-- <body> -->

<!--
<table id="warehouse">
	<#list resultWarehouses.listWarehouses as wh>
		<tr>
			<td>${wh.warehouseId}</td>
			<td>${wh.warehouseName}</td>
			<td>${wh.latitude}</td>
			<td>${wh.longitude}</td>
		</tr>
	</#list>
</table>
-->

<div id="map" class="col-lg-10"></div>

<div class="col-lg-2">
    <div class="form-group">
         <label>Nhap thong tin</label>
         <input id="quantity" class="form-control" placeholder="Luong hang">
    </div>
    <div class="form-group">
         <input id="warehouse" class="form-control" placeholder="Kho hang">
    </div>
    
    <div class="form-group">
                <label class="control-label">Thoi gian giao som nhat</label>
                <div class="controls input-append date form_datetime" 
                data-date-format="dd MM yyyy - HH:ii p" data-link-field="earlyDateTime">
                    <input size="30" type="text" value="" readonly>
                    <span class="add-on"><i class="icon-remove"></i></span>
					<span class="add-on"><i class="icon-th"></i></span>
                </div>
				<input type="hidden" id="earlyDateTime" value="" /><br/>
    </div>
    

    <div class="form-group">
                <label class="control-label">Thoi gian giao muon nhat</label>
                <div class="controls input-append date form_datetime" 
                data-date-format="dd MM yyyy - HH:ii p" data-link-field="lateDateTime">
                    <input size="30" type="text" value="" readonly>
                    <span class="add-on"><i class="icon-remove"></i></span>
					<span class="add-on"><i class="icon-th"></i></span>
                </div>
				<input type="hidden" id="lateDateTime" value="" /><br/>
    </div>
            
    
	<div id="info">
	</div>
   	
   	<button type="button" class="btn btn-primary" onclick="computeRoutes()">Lap lo trinh</button>


</div>


<script type="text/javascript">
    $('.form_datetime').datetimepicker({
        language:  'vn',
        weekStart: 1,
        todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		forceParse: 0,
        showMeridian: 1
    });

</script>


<script type="text/javascript">

var Options = {
center:  new google.maps.LatLng(21.02422526767355, 105.84773308334343),//(21.008581634411787, 105.84569460449217),
zoom: 16,
mapTypeId: google.maps.MapTypeId.ROADMAP
}
var map;
var points;
var warehouses = [];
var clients = [];
var routes = [];
var markers = [];
var resultAjax;
var lastStartPoint;
var segments = [];
var lengths = [];
var loads = [];
var currentSelectWarehouse;

$(document).ready(function(){
	//alert('LOAD');
	initMap();
	
});

function initMap(){
	map = new google.maps.Map(document.getElementById('map'),Options);
	points = [];
	google.maps.event.addListener(map,'click',function(event){
		var img = new google.maps.MarkerImage('/resource/transportations/image/icon/shop-sz-10.jpg');
		var marker = new google.maps.Marker({
			'map':map,
			'position': event.latLng,
			'visible': true,
			'icon': img
		});
		points.push(marker);
		document.getElementById("location").value = marker.position;
	});
	
	loadWarehouse();
	loadDeliveryRequest();

}	

function extendRoute(p){
	var s = currentSelectWarehouse.route[currentSelectWarehouse.route.length-1];
	
	currentSelectWarehouse.route.push(p);
	
	var list_points = [s.position,p.position];
	var path = new google.maps.Polyline({
						fromPoint: s,
						toPoint: p,
						path: list_points,
						geodisc: true,
						strokeColor: '#FF0000',
						strokeWeight: 2
					});
					path.setMap(map);
	console.log(s,p);				
	$.ajax({
	url: "/transportations/control/get-distance",
			type: 'POST',
			data:{"fromPoint": s.geopointid, "toPoint": p.geopointid},
			success: function(rs){
				console.log(rs);
				var l = rs.distance;
				console.log('before',l,currentSelectWarehouse.ID,currentSelectWarehouse.routeLength);
				currentSelectWarehouse.routeLength = Number(currentSelectWarehouse.routeLength) + Number(l);
				console.log('after',l,currentSelectWarehouse.ID,currentSelectWarehouse.routeLength);
				
				var html = '<table>';
				for(i = 0; i < warehouses.length; i++){
					html = html + '<tr>';
					html = html + '<td>' + warehouses[i].ID + '</td>';
					html = html + '<td>' + warehouses[i].routeLength + '</td>';
					html = html + '</tr>';
				}
				html = html + '</table>';
				document.getElementById("info").innerHTML = html;
	
			}
		
	});
	
					
	google.maps.event.addListener(path,'click',function(){
		alert(path.fromPoint.position + ' --> ' + path.toPoint.position);
		path.setMap(null);
		var idx = segments.indexOf(path);
		if(idx > -1)
			segments.splice(idx,1);
	});
	
	segments.push(path);				
	
}

function createLine(p1, p2){
	var list_points = [p1.position,p2.position];
	var path = new google.maps.Polyline({
						fromPoint: p1,
						toPoint: p2,
						path: list_points,
						geodisc: true,
						strokeColor: '#FF0000',
						strokeWeight: 2
					});
					path.setMap(map);
	google.maps.event.addListener(path,'click',function(){
		alert(path.fromPoint.position + ' --> ' + path.toPoint.position);
		path.setMap(null);
		var idx = segments.indexOf(path);
		if(idx > -1)
			segments.splice(idx,1);
	});
	
	segments.push(path);				
}

function addWarehouseMarker(wh){
					var img = new google.maps.MarkerImage('/resource/transportations/image/icon/warehouse-sz-30.jpg');
					var position = {lat:wh.latitude,lng: wh.longitude};
					var ID = 'wh' + wh.id;
					var marker = new google.maps.Marker({
						'map':map,
						'position': position,
						'geopointid': wh.geoPointId,
						'visible': true,
						'icon': img,
						'ID': ID
					});
					marker.route = [];
					marker.route.push(marker);
					marker.routeLength = 0;
					
					markers.push(marker);
					warehouses.push(marker);
					
					marker.addListener('click', function() {
    					//alert(marker.position + ', ID = ' + marker.ID);
    					if(lastStartPoint != null){
    						//createLine(lastStartPoint, marker);
    						extendRoute(marker);
    						lastStartPoint = marker;
    					}
  					});
  					
  					marker.addListener('rightclick', function() {
    					//alert('right-click ' + marker.position + ', ID = ' + marker.ID);
    					lastStartPoint = marker;	
    					currentSelectWarehouse = marker;
    					document.getElementById("warehouse").value = currentSelectWarehouse.ID;
  					});
					
		 			var point = new Object();
		 			point.id = ID;
		 			point.position = position;
		 			points.push(point);
				
}

function addClientMarker(client){
					var img = new google.maps.MarkerImage('/resource/transportations/image/icon/shop-sz-10.jpg');
					var position = {lat:client.latitude,lng: client.longitude};
					var ID = 'client' + client.id;
					var marker = new google.maps.Marker({
						'map':map,
						'position': position,
						'geopointid': client.geoPointId,
						'visible': true,
						'icon': img,
						'ID': ID
					});
					markers.push(marker);
					
					marker.addListener('click', function() {
    					//alert(marker.position + ', ID = ' + marker.ID);
    					if(lastStartPoint != null){
    						//createLine(lastStartPoint, marker);
    						extendRoute(marker);
    						lastStartPoint = marker;
    					}
    					
  					});
  					/*
					marker.addListener('rightclick', function() {
    					//alert('right-click ' + marker.position + ', ID = ' + marker.ID);
    					lastStartPoint = marker;
  					});
  					*/
		 			var point = new Object();
		 			point.id = ID;
		 			point.position = position;
		 			points.push(point);
				
}

function loadWarehouse(){
	$.ajax({
	url: "/transportations/control/get-warehouses",
			type: 'POST',
			success: function(rs){
				console.log(rs);
				
				for(i = 0; i < rs.warehouses.length; i++){
					var wh = rs.warehouses[i];
					addWarehouseMarker(wh);
				}		
			}
		
	});
}

function loadDeliveryRequest(){
	$.ajax({
	url: "/transportations/control/get-delivery-requests",
			type: 'POST',
			success: function(rs){
				console.log(rs);
				
				
				for(i = 0; i < rs.deliveryrequests.length; i++){
					var r = rs.deliveryrequests[i];
					addClientMarker(r);
				}
				
			}
		
	});
}
/*
function addRequest(){
	var quantity = document.getElementById("quantity").value;
	var earlyDateTime = document.getElementById("earlyDateTime").value;
	var lateDateTime = document.getElementById("lateDateTime").value;
	var location = document.getElementById("location").value;
	
	//alert('Them request quantty = ' + quantity + ', earlyDateTime = ' + earlyDateTime
	//+ ', lateDateTime = ' + lateDateTime);
	
		$.ajax({
			url: "/transportations/control/add-a-delivery-request",
			type: 'POST',
			data: {
				"quantity": quantity,
				"location": location,
				"earlyDateTime": earlyDateTime,
				"lateDateTime": lateDateTime
			},
			success: function(rs){
				//window.location.href = "/bkeuniv/control/project-call-management";
				console.log(rs.result);
			}
		})
}
*/
function computeRoutes(){
	var routes = [];
	for(let i = 0; i < segments.length; i++){
		var o = new Object();
		o.fromPoint = segments[i].fromPoint.ID;
		o.toPoint = segments[i].toPoint.ID;
		routes.push(o);
	}
	
	console.log(routes);
	
		/*
		$.ajax({
			url: "/transportations/control/compute-delivery-routes",
			type: 'POST',
			success: function(rs){
				console.log(rs);
				resultAjax = rs;
				
				for(i = 0; i < rs.routes.length; i++){
					var route = rs.routes[i].elements;
					var aRoute = [];
					for(j = 0; j < route.length; j++){
						//var latlng = route[j].latlng;
						var lat = route[j].lat;
						var lng = route[j].lng;
						var latlng = new Object();
						latlng.lat = lat;
						latlng.lng = lng; 
						//alert(latlng);
						aRoute.push(latlng);
					}
					routes.push(aRoute);
					
					var path = new google.maps.Polyline({
						path: aRoute,
						geodisc: true,
						strokeColor: '#FF0000',
						strokeWeight: 2
					});
					path.setMap(map);
				}
				
			}
		})
		*/
}

</script>

