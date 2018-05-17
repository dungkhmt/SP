 <head>

    <link href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.1/themes/south-street/jquery-ui.min.css" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" href="/resource/bkeuniv/css/lib/dataTables.bootstrap.min.css">

    
    <script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
	<script src="/resource/bkeuniv/js/lib/jquery.dataTables.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.1/jquery-ui.min.js"></script>
    
    
    <script src="https://cdn.jsdelivr.net/jquery.ui-contextmenu/1.7.0/jquery.ui-contextmenu.min.js"></script>
    
    <meta charset="utf-8" />
    
    <title>DataTables - Context menu integration</title>
  
  </head>

<div id="table-list-routes" style="overflow-y: auto; padding: 2em;">
	<table id="list-routes" cellspacing="0" width="100%" class="display dataTable">
		<thead>
			<tr>
				<th style="display: none"></th>
				<th>Vehicle</th>
				<th>Client</th>
				<th>Arrival Time</th>
				<th>Department Time</th>
				<th>Infos</th>
			</tr>
		</thead>
	<tbody>
	<#list resultDetailRoutes.detailRoutes as r>
		<tr>
			<td style="display: none">${r.routeDetailId}</td>
			<td>${r.vehicleId}</td>
			<td>${r.clientName}</td>
			<#if r.arrivalTime?exists>
				<td>${r.arrivalTime}</td>
			<#else>
				<td></td>
			</#if>
			<#if r.departureTime?exists>
				<td>${r.departureTime}</td>
			<#else>
				<td></td>
			</#if>
			<#if r.description?exists>
				<td>${r.description}</td>
			<#else>
				<td></td>
			</#if>
		</tr>
	</#list>
	</tbody>
	</table>
	
</div>
