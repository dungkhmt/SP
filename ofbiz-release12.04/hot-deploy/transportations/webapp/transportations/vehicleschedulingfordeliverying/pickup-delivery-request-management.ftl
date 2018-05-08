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

<div class="col-lg-4">
    <div class="form-group">
         <label>Nhap thong tin</label>
         <input id="quantity" class="form-control" placeholder="Luong hang">
    </div>
    <div class="form-group">
         <input id="pickupClientId" class="form-control" placeholder="Ma khach hang pickup">
    </div>
    
    <div class="form-group">
                <label class="control-label">Thoi gian nhan hang som nhat</label>
                <div class="controls input-append date form_datetime" 
                data-date-format="dd MM yyyy - HH:ii p" data-link-field="earlyPickupDateTime">
                    <input size="30" type="text" value="" readonly>
                    <span class="add-on"><i class="icon-remove"></i></span>
					<span class="add-on"><i class="icon-th"></i></span>
                </div>
				<input type="hidden" id="earlyPickupDateTime" value="" /><br/>
    </div>
    

    <div class="form-group">
                <label class="control-label">Thoi gian nhan hang muon nhat</label>
                <div class="controls input-append date form_datetime" 
                data-date-format="dd MM yyyy - HH:ii p" data-link-field="latePickupDateTime">
                    <input size="30" type="text" value="" readonly>
                    <span class="add-on"><i class="icon-remove"></i></span>
					<span class="add-on"><i class="icon-th"></i></span>
                </div>
				<input type="hidden" id="latePickupDateTime" value="" /><br/>
    </div>
            
    
    <div class="form-group">
         <input id="deliveryClientId" class="form-control" placeholder="Ma khach hang delivery">
    </div>
    
    <div class="form-group">
                <label class="control-label">Thoi gian tra hang som nhat</label>
                <div class="controls input-append date form_datetime" 
                data-date-format="dd MM yyyy - HH:ii p" data-link-field="earlyDeliveryDateTime">
                    <input size="30" type="text" value="" readonly>
                    <span class="add-on"><i class="icon-remove"></i></span>
					<span class="add-on"><i class="icon-th"></i></span>
                </div>
				<input type="hidden" id="earlyDeliveryDateTime" value="" /><br/>
    </div>
    

    <div class="form-group">
                <label class="control-label">Thoi gian tra hang muon nhat</label>
                <div class="controls input-append date form_datetime" 
                data-date-format="dd MM yyyy - HH:ii p" data-link-field="lateDeliveryDateTime">
                    <input size="30" type="text" value="" readonly>
                    <span class="add-on"><i class="icon-remove"></i></span>
					<span class="add-on"><i class="icon-th"></i></span>
                </div>
				<input type="hidden" id="lateDeliveryDateTime" value="" /><br/>
    </div>

   	<button type="button" class="btn btn-primary" onclick="addRequest()">Them yeu cau giao hang</button>
   	
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
var warehouses;
var routes = [];
var resultAjax;
var lastSelectMarker;

$(document).ready(function(){
	//alert('LOAD');
	initMap();
	
});

function initMap(){
	map = new google.maps.Map(document.getElementById('map'),Options);
	points = [];
	/*
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
	*/
	loadWarehouse();
	loadClients();
	loadClientPickupDeliveryRequest();
	
}	
function addClient(cl){
	var img = new google.maps.MarkerImage('/resource/transportations/image/icon/shop-sz-10.jpg');
	var position = {lat:cl.latitude,lng: cl.longitude};
	var marker = new google.maps.Marker({
		'map':map,
		'position': position,
		'visible': true,
		'icon': img,
		'ID': cl.id
	});
	marker.addListener('click', function() {
		document.getElementById("pickupClientId").value = marker.ID;
		
	});					
	marker.addListener('rightclick', function() {
		document.getElementById("deliveryClientId").value = marker.ID;
		
	});					

}
function loadClients(){
	$.ajax({
	url: "/transportations/control/get-client",
			type: 'POST',
			success: function(rs){
				console.log(rs);
				warehouses = rs;
				//alert(rs.warehouses.length);
				
				for(i = 0; i < rs.clients.length; i++){
					var cl = rs.clients[i];
					addClient(cl);
		 
				}
				
			}
		
	});
}

function addRequestMarker(r,i){
	var img = new google.maps.MarkerImage('/resource/transportations/image/icon/store-icon.png');
	var pickup_position = {lat:r.pickuplatitude,lng: r.pickuplongitude};
	var delivery_position = {lat:r.deliverylatitude,lng: r.deliverylongitude};
	
	var pickup_marker = new google.maps.Marker({
		'map':map,
		'position': pickup_position,
		'visible': true,
		'icon': img,
		'label': i,
		'ID': r.id
	});
	
	var delivery_marker = new google.maps.Marker({
		'map':map,
		'position': delivery_position,
		'visible': true,
		'icon': img,
		'label': i,
		'ID': r.id
	});

}

function loadClientPickupDeliveryRequest(){
	$.ajax({
	url: "/transportations/control/get-client-pickup-delivery-requests",
			type: 'POST',
			success: function(rs){
				console.log(rs);
				
				for(i = 0; i < rs.requests.length; i++){
					var r = rs.requests[i];
					addRequestMarker(r,i + "");
				}
			}
	});
}

function loadWarehouse(){
	$.ajax({
	url: "/transportations/control/get-warehouses",
			type: 'POST',
			success: function(rs){
				console.log(rs);
				warehouses = rs;
				//alert(rs.warehouses.length);
				
				for(i = 0; i < rs.warehouses.length; i++){
					var wh = rs.warehouses[i];
					//var img = new google.maps.MarkerImage('/resource/transportations/image/icon/ccmarker.png');
					var img = new google.maps.MarkerImage('/resource/transportations/image/icon/warehouse-sz-30.jpg');
					//alert(rs.warehouses[i].id);
					var position = {lat:wh.latitude,lng: wh.longitude};
					//alert(position);
					var marker = new google.maps.Marker({
						'map':map,
						'position': position,
						'visible': true,
						'icon': img
					});
		 
				}
				
			}
		
	});
}
function addRequest(){
	var quantity = document.getElementById("quantity").value;
	var earlyPickupDateTime = document.getElementById("earlyPickupDateTime").value;
	var latePickupDateTime = document.getElementById("latePickupDateTime").value;
	var pickupClientId = document.getElementById("pickupClientId").value;
	var earlyDeliveryDateTime = document.getElementById("earlyDeliveryDateTime").value;
	var lateDeliveryDateTime = document.getElementById("lateDeliveryDateTime").value;
	var deliveryClientId = document.getElementById("deliveryClientId").value;
	
	
	//alert('Them request quantty = ' + quantity + ', earlyPickupDateTime = ' + earlyPickupDateTime
	//+ ', latePickupDateTime = ' + latePickupDateTime);
	
		$.ajax({
			url: "/transportations/control/add-a-pickup-delivery-request",
			type: 'POST',
			data: {
				"quantity": quantity,
				"pickupClientId": pickupClientId,
				"earlyPickupDateTime": earlyPickupDateTime,
				"latePickupDateTime": latePickupDateTime,
				"deliveryClientId": deliveryClientId,
				"earlyDeliveryDateTime": earlyDeliveryDateTime,
				"lateDeliveryDateTime": lateDeliveryDateTime
			},
			success: function(rs){
				//window.location.href = "/bkeuniv/control/project-call-management";
				console.log(rs.result);
				//var img = new google.maps.MarkerImage('/resource/transportations/image/icon/shop-sz-10.jpg');
				//lastSelectMarker.setIcon(img);
			}
		})
}

function computeRoutes(){
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

}

</script>

