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

<div id="map" class="col-lg-10"></div>

<div class="col-lg-4">
    <div class="form-group">
         <input id="name" class="form-control" placeholder="Ten">
    </div>
    <div class="form-group">
         <input id="warehouseId" class="form-control" placeholder="Ma warehouse">
    </div>
    <div class="form-group">
         <input id="height" class="form-control" placeholder="Chieu cao">
    </div>
    <div class="form-group">
         <input id="length" class="form-control" placeholder="Chieu dai">
    </div>
    <div class="form-group">
         <input id="width" class="form-control" placeholder="Chieu rong">
    </div>
    <div class="form-group">
         <input id="weight" class="form-control" placeholder="Tai trong">
    </div>
    
    

    
    <div class="form-group">
                <label class="control-label">Thoi gian bat dau lam viec</label>
                <div class="controls input-append date form_datetime" 
                data-date-format="dd MM yyyy - HH:ii p" data-link-field="startWorkingTime">
                    <input size="30" type="text" value="" readonly>
                    <span class="add-on"><i class="icon-remove"></i></span>
					<span class="add-on"><i class="icon-th"></i></span>
                </div>
				<input type="hidden" id="startWorkingTime" value="" /><br/>
    </div>
    

    <div class="form-group">
                <label class="control-label">Thoi gian ket thuc lam viec</label>
                <div class="controls input-append date form_datetime" 
                data-date-format="dd MM yyyy - HH:ii p" data-link-field="endWorkingTime">
                    <input size="30" type="text" value="" readonly>
                    <span class="add-on"><i class="icon-remove"></i></span>
					<span class="add-on"><i class="icon-th"></i></span>
                </div>
				<input type="hidden" id="endWorkingTime" value="" /><br/>
    </div>
            
    
   	<button type="button" class="btn btn-primary" onclick="addVehicle()">Them Xe</button>
   
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
						'ID': wh.id,
						'icon': img
					});
		 			marker.addListener('click', function() {
					document.getElementById("warehouseId").value = marker.ID;
		
	});					

				}
				
			}
		
	});
}

function addVehicle(){
	var warehouseId = document.getElementById("warehouseId").value;
	var name = document.getElementById("name").value;
	var height = document.getElementById("height").value;
	var length = document.getElementById("length").value;
	var weight = document.getElementById("weight").value;
	var width = document.getElementById("width").value;
	var startWorkingTime = document.getElementById("startWorkingTime").value;
	var endWorkingTime = document.getElementById("endWorkingTime").value;
	
	
	var name = document.getElementById("name").value;
		$.ajax({
			url: "/transportations/control/add-a-vehicle",
			type: 'POST',
			data: {
				"warehouseId": warehouseId,
				"name": name,
				"height": height,
				"length": length,
				"weight": weight,
				"width": width,
				"startWorkingTime": startWorkingTime,
				"endWorkingTime": endWorkingTime
			},
			success: function(rs){
				
				console.log(rs.result);
			}
		})
}


</script>

