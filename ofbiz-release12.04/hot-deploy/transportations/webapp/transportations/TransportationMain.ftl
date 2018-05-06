<!--
<div>
	<a href="google-maps">GoogleMaps</a><br>
	<a href="routes-management">Quan ly tuyen giao hang</a><br>
	<a href="upload-file"> Upload File</a><br>
	<a href="https://localhost:8443/bkeuniv/control/main"> BKEUNIV </a><br>
</div>
-->


<script src="/resource/transportations/js/lib/Chart.min.js"></script>
<body>

<canvas id="paper-chart" width="700" height="400"></canvas> 


<script> <!--doan nay de ve bieu do cot len canvas-->

var barData = {
	labels : [1,2,3,4,5,6,7,8,9],
	datasets : [
		{
			fillColor : "#48A497", 
			data : [100,200,150,300,50,250,220,130,500]
		}		
		]
	}
	console.log(barData);
	var paperChart = document.getElementById("paper-chart").getContext("2d");
	new Chart(paperChart).Bar(barData);
</script>

<div class="body">
</div>
