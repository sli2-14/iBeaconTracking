/*var music_demo = [ 
	              {
	            	  type:'classical',
	            	  content:["Beethoven","Mozart","Tchaikovsky"]
	              },
				  {
	            	  type:'pop',
	            	  content:["Beatles!!!","Corrs","Fleetwood Mac","Status Quo"]
	              }
	         ]*/


var current_floorplan;
var current_deployment;
var loc_history;
var sliderleftvalue;
var sliderrightvalue;
var currentfloorplan;
var currentdeployment;
var zone_list;
//var start_date;
//var end_date;

function getCookie(name) {
    var cookieValue = null;
    if (document.cookie && document.cookie != '') {
        var cookies = document.cookie.split(';');
        for (var i = 0; i < cookies.length; i++) {
            var cookie = jQuery.trim(cookies[i]);
            // Does this cookie string begin with the name we want?
            if (cookie.substring(0, name.length + 1) == (name + '=')) {
                cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                break;
            }
        }
    }
    return cookieValue;
}

$.ajaxSetup({
    beforeSend: function(xhr, settings) {
        if (!(/^http:.*/.test(settings.url) || /^https:.*/.test(settings.url))) {
            // Only send the token to relative URLs i.e. locally.
            xhr.setRequestHeader("X-CSRFToken", getCookie('csrftoken'));
        }
    }
});

//draw the rssi displaying in canvas
function initDatasetImport(){
	$('#upload-file-btn').click(function() {
	    var form_data = new FormData($('#upload-file')[0]);
	    form_data.append("floorplan_id" , current_floorplan);
	    form_data.append("deployment_id" , current_deployment);
	    $.ajax({
	        type: 'POST',
	        url: '/ibeaconapp/dataset/',
	        data: form_data,
	        contentType: false,
	        cache: false,
	        processData: false,
	        async: false,
	        success: function(data) {
	            alert('Success!');
	            loc_history = data["location_history_list"];
	            drawResultOnCanvas(data["location_history_list"]);
				enableExportFile();
				enableZoneRestriction();
	        },
	        failure: function(){alert('Failed to get location history list calculated from server...');}
	    });
	});
	/*
	$('#clearcanvas').click(function() {
		$.ajax({
			type: 'POST',
	        success: function(data) {
	        	ctx.clearRect ( 0 , 0 , canvas_fp.width, canvas_fp.height );
	            alert('Success!');
	        }
		})
	})
	*/
}



function drawResultOnCanvas(data){
    var canvas_fp = document.getElementById("canvas_fp");
	var ctx = canvas_fp.getContext("2d");
	for(var i=0; i<data.length; i++){
		ctx.beginPath();
		ctx.arc(data[i].x,data[i].y,2,0,2*Math.PI);
		ctx.stroke();
	}
}


function enableZoneRestriction(){
	document.getElementById("update_zone_btn").disabled = false;
}

function disableZoneRestriction(){
	document.getElementById("update_zone_btn").disabled = true;
}

function enableExportFile(){
	document.getElementById("export-file-btn").disabled = false;
	$('#export-file-btn').click(function() {
		
		var data = loc_history;
		var csvContent = "data:text/csv;charset=utf-8,";
		data.forEach(function(infoArray, index){

		   dataString = infoArray["time"]+","+infoArray["x"]+","+infoArray["y"]
		   csvContent += dataString + "\n";

		});
		
		var encodedUri = encodeURI(csvContent);
		//window.open(encodedUri);
		var link = document.createElement("a");
		link.setAttribute("href", encodedUri);
		link.setAttribute("download", "my_data.csv");

		link.click();
		
	});
	
	document.getElementById("export-file-with-zone-btn").disabled = false;
	$('#export-file-with-zone-btn').click(function() {
		
		var data = loc_history;
		var csvContent = "data:text/csv;charset=utf-8,";
		data.forEach(function(infoArray, index){

			dataString = infoArray["time"]+","+infoArray["x"]+","+infoArray["y"];
			for(var i=0; i<zone_list.length; i++){
				//alert(infoArray["x"] > zone_list[i]['tl_x']);
				if(infoArray["x"] > zone_list[i]['tl_x'] && infoArray["y"] > zone_list[i]['tl_y'] && infoArray["x"] < zone_list[i]['br_x'] && infoArray["y"] < zone_list[i]['br_y']){
					dataString = dataString + ","+ zone_list[i]["zone_no"]+","+ zone_list[i]["note"];
				}
			}
			csvContent += dataString + "\n";
		});
		var encodedUri = encodeURI(csvContent);
		//window.open(encodedUri);
		var link = document.createElement("a");
		link.setAttribute("href", encodedUri);
		link.setAttribute("download", "my_data.csv");
		link.click();
	});
	
	document.getElementById("export-awc-btn").disabled = false;
	$('#export-awc-btn').click(function() {
		var data = loc_history;
		var awcContent = "data:text/plain;charset=utf-8,"
		awcContent += "Participant 1";
		awcContent += "\n";
		
		var t = data[0]["time"].split(" ");
		awcContent += t[2]+"-"+t[1]+"-"+t[5].substr(2,2);
		awcContent += "\n";
		awcContent += t[3];
		awcContent += "\n";
		awcContent += 1;
		awcContent += "\n";
		for(var i=5; i<12; i++){
			awcContent += "Not specified";
			awcContent += "\n";
		}
		data.forEach(function(infoArray, index){
			var zone_no_min = 0;
			for(var i=0; i<zone_list.length; i++){
				if(infoArray["x"] > zone_list[i]['tl_x'] && infoArray["y"] > zone_list[i]['tl_y'] && infoArray["x"] < zone_list[i]['br_x'] && infoArray["y"] < zone_list[i]['br_y']){
					if(zone_list[i]["zone_no"]<zone_no_min  || zone_no_min==0)
						zone_no_min = zone_list[i]["zone_no"];
					not_in_zone = false;
				}
			}
			awcContent += zone_no_min;
			awcContent += "\n";
		});
		var encodedUri = encodeURI(awcContent);
		//window.open(encodedUri);
		var link = document.createElement("a");
		link.setAttribute("href", encodedUri);
		link.setAttribute("download", "my_data.awc");
		link.click();
	});	
	
}


function cleanResultOnCanvas(){
	//var canvas_fp = document.getElementById("canvas_fp");
	//var ctx = canvas_fp.getContext("2d");
    //ctx.clearRect(0,0,canvas_fp.width,canvas_fp.height);
    //onFloorplanChange(currentfloorplan);
    //onDeploymentChange(currentdeployment);
	showFloorplan(current_floorplan,true);
}


function relMouseCoords(event){
    var totalOffsetX = 0;
    var totalOffsetY = 0;
    var canvasX = 0;
    var canvasY = 0;
    var currentElement = this;

    do{
        totalOffsetX += currentElement.offsetLeft - currentElement.scrollLeft;
        totalOffsetY += currentElement.offsetTop - currentElement.scrollTop;
    }
    while(currentElement = currentElement.offsetParent)

    canvasX = event.pageX - totalOffsetX;
    canvasY = event.pageY - totalOffsetY;

    return {x:canvasX, y:canvasY}
}
HTMLCanvasElement.prototype.relMouseCoords = relMouseCoords;

function initFpCanvas(){
	var canvas_fp = document.getElementById("canvas_fp");
	//Add event listener for `click` events to the floorplan canvas.
	canvas_fp.addEventListener('click', function(event) {
		var coords = canvas_fp.relMouseCoords(event);
		canvasX = coords.x;
		canvasY = coords.y;
	
		alert("x is: " + canvasX + "  y is: " + canvasY);
	}, false);
}


function initSlider(){
    $('.nstSlider').nstSlider({
        "left_grip_selector": ".leftGrip",
        "right_grip_selector": ".rightGrip",
        "value_bar_selector": ".bar",
        "value_changed_callback": function(cause, leftValue, rightValue) {
            $(this).parent().find('.leftLabel').text(leftValue);
            $(this).parent().find('.rightLabel').text(rightValue);
            sliderleftvalue=leftValue;
            sliderrightvalue=rightValue;
        }
    });
    
	$('#update-time-btn').click(function() {
	    //var form_data = new FormData($('#upload-file')[0]);
	    //form_data.append("floorplan_id" , current_floorplan);
	    //form_data.append("deployment_id" , current_deployment);
        
        var canvas_fp = document.getElementById("canvas_fp");
		var ctx = canvas_fp.getContext("2d");
		var data = loc_history;
		var start_time = Math.round(data.length*sliderleftvalue/100);
		var end_time = Math.round(data.length*sliderrightvalue/100);
		//start_date=data.[start_time];
		//end_date=data.[end_time];
		for(var i=start_time; i<end_time; i++){
			ctx.beginPath();
			ctx.arc(data[i].x,data[i].y,2,0,2*Math.PI);
			ctx.stroke();
				}   
		//document.getElementById("starttime").value= data.[start_time];
		//document.getElementById("endtime").value= data.[end_time];
		//html += '	<span id="starttime" />	<span id="endtime" />'
		document.getElementById('starttime').innerHTML=data[start_time].time;
		document.getElementById('endtime').innerHTML=data[end_time].time;
		alert('Successfully updated!');    
	})
}

function initZoneRestrictionButton(){
	$('#update_zone_btn').click(function() {
		
		zone_info = getZoneInfo();
		var tl_x = Number(zone_info['tl_x']);
		var tl_y = Number(zone_info['tl_y']);
		var br_x = Number(zone_info['br_x']);
		var br_y = Number(zone_info['br_y']);

		zone_data = [];
		for(rec in loc_history){
			alert(rec["x"] + '>' + tl_x +' &&' + rec.y +'>'+ tl_y+' &&'+ rec.x+' <'+ br_x+' &&'+ rec.y +'<'+ br_y);
			if (rec.x > zone_info['tl_x'] && rec.y > zone_info['tl_y'] && rec.x < zone_info['br_x'] && rec.y < zone_info['br_y']) {
			//if(true){
				alert("one match");
				zone_data.push(rec);
			}
		}
		UpdateResultOnCanvase(zone_data);
		alert('Successfully updated!');
	});
}


function updateResultWithZone(){
	
	var zone_sel = document.getElementById("zone_sel");
	var zone_id = zone_sel[zone_sel.selectedIndex].value;
	 $.ajax({
		 url:'/ibeaconapp/zone/'+zone_id+'/',
		 type:'GET',
		 accept: 'application/json',
		 success: function(data, responseText, jqXHR) {
				var tl_x = parseInt(data['tl_x']);
				var tl_y = parseInt(data['tl_y']);
				var br_x = parseInt(data['br_x']);
				var br_y = parseInt(data['br_y']);
				zone_data = [];
				for(var i=0; i<loc_history.length; i++){
					if (loc_history[i].x > tl_x && loc_history[i].y > tl_y && loc_history[i].x < br_x && loc_history[i].y < br_y) {
						zone_data.push(loc_history[i]);
					}
				}
				UpdateResultOnCanvase(zone_data);
				alert('Successfully updated!');
		 },
		 failure: function(){alert('Failed to get zone info from server...');return {};}
	 });

}


// update the canvas with new result, AJAX calls were duplicated to make sure the order of drawing
// floorplan, deployment and then result
function UpdateResultOnCanvase(zone_data){
	
	 var parameters = {"floorplan_id" : current_floorplan};
	 //current_floorplan = fp_sel[fp_sel.selectedIndex].value;
	 $.ajax({
		 url:'/ibeaconapp/floorplan/img/',
		 type:'GET',
		 data: parameters,
		 accept: 'application/json',
		 success: function(data, responseText, jqXHR) {
			 //alert("the floorplan img file name is: " + data.floorplan_img);
			 var myCanvas = document.getElementById('canvas_fp');
			 var ctx = myCanvas.getContext('2d');
			 ctx.clearRect(0, 0, myCanvas.width, myCanvas.height);
			 var img = new Image;
			 img.onload = function(){
				 ctx.drawImage(img,0,0); // Or at whatever offset you like
			 };
			 img.src = "/static/ibeaconapp/image/"+data.floorplan_img;
			 

			var parameters = {"deployment_id" : current_deployment};
			current_deployment = deployment_id;
			$.ajax({
				url:'/ibeaconapp/deployment/beacons/',
				type:'GET',
				data: parameters,
				accept: 'application/json',
				success: function(data, responseText, jqXHR) {
					// draw the beacons on the canvas
					var canvas_fp = document.getElementById("canvas_fp");
					var ctx = canvas_fp.getContext("2d");
					for(var i=0; i<data.beaconlist.length; i++){
						ctx.beginPath();
						ctx.arc(data.beaconlist[i].x,data.beaconlist[i].y,10,0,2*Math.PI);
						ctx.stroke();
						ctx.beginPath();
						ctx.arc(data.beaconlist[i].x,data.beaconlist[i].y,15,0,2*Math.PI);
						ctx.stroke();
						ctx.beginPath();
						ctx.arc(data.beaconlist[i].x,data.beaconlist[i].y,20,0,2*Math.PI);
						ctx.stroke();
					}
					
					
					drawResultOnCanvas(zone_data);
					
				},
			});

			 
		 },
		 failure: function(){alert('Failed to get floorplan list from server...');}
	 });
	
	
}


// get the information such as the cords of the top left and bottom right points of a zone
function getZoneInfo(){
	var zone_sel = document.getElementById("zone_sel");
	var zone_id = zone_sel[zone_sel.selectedIndex].value;
	var rtn_data = {};
	 $.ajax({
		 url:'/ibeaconapp/zone/'+zone_id+'/',
		 type:'GET',
		 accept: 'application/json',
		 success: function(data, responseText, jqXHR) {
			 for (attr in data)
				 rtn_data[attr]=data[attr];
		 },
		 failure: function(){alert('Failed to get zone info from server...');return {};}
	 });
	 return rtn_data;
}

// initialization of home page
function init(){
	initFpCanvas();
	initDatasetImport();
	initSlider();
	//initZoneRestrictionButton();
	var parameters = {};
	$.ajax({
		url: '/ibeaconapp/floorplan/',
		type: 'GET',
		data: parameters,
		accept: 'application/json',
		success: function(data, responseText, jqXHR) {
			 var html = "";
			 html += "<option disabled selected> -- select a floorplan -- </option>"
			 for(var i=0; i<data["floorplanlist"].length; i++){
				 html += '<option value=' +data["floorplanlist"][i]["id"] + '>';
				 html += data["floorplanlist"][i]["name"];
				 html += '</option>';
			 }
			 
			 document.getElementById("floorplan_sel").innerHTML = html;
			 
			},
		failure: function(){alert('Failed to get floorplan list from server...');}
	});
}

// process floorplan selection change
function onFloorplanChange(fp_sel){
	
	currentfloorplan=fp_sel;
	
	
	clearDatasetSelect();
	
	 var parameters = {"floorplan_id" : fp_sel[fp_sel.selectedIndex].value};
	 current_floorplan = fp_sel[fp_sel.selectedIndex].value;
	 $.ajax({
		 url:'/ibeaconapp/deployment/',
		 type:'GET',
		 data: parameters,
		 accept: 'application/json',
		 success: function(data, responseText, jqXHR) {
			 var html = "";
			 html += "<option disabled selected> -- select a deployment -- </option>"
			 for(var i=0; i<data["deploymentlist"].length; i++){
				 html += '<option value=' +data["deploymentlist"][i]["id"] + '>';
				 html += data["deploymentlist"][i]["name"];
				 html += '</option>';
	 		}
			 document.getElementById("deployment_sel").innerHTML = html;
			 // No zone restriction function until a dataset is selected or calculated
			 disableZoneRestriction();
			 //get list of zones available for this floorplan
			 getZone(current_floorplan);
		 },
		 failure: function(){alert('Failed to get deployment list from server...');}
	 });
	 
	 // You can display the floorplan already
	 showFloorplan(fp_sel[fp_sel.selectedIndex].value,false);
}


//get a list of zones associated with the floorplan with id fp_id
function getZone(fp_id){
	 $.ajax({
		 url:'/ibeaconapp/floorplan/'+fp_id+'/zone/',
		 type:'GET',
		 accept: 'application/json',
		 success: function(data, responseText, jqXHR) {
			 var html = "";
			 html += "<option disabled selected> -- select a zone -- </option>"
			 for(var i=0; i<data["zone_list"].length; i++){
				 html += '<option value=' +data["zone_list"][i]["zone_id"] + '>';
				 html += data["zone_list"][i]["zone_no"]+"__"+data["zone_list"][i]["note"];
				 html += '</option>';
	 		}
			 document.getElementById("zone_sel").innerHTML = html;
			 zone_list = data["zone_list"];
		 },
		 failure: function(){alert('Failed to get deployment list from server...');}
	 });
}


//process deployment selection change
function onDeploymentChange(dp_sel){
	currentdeployment=dp_sel;
	
	clearDatasetSelect()
	
	deployment_id = dp_sel[dp_sel.selectedIndex].value;
	
	 var parameters = {"deployment_id" : deployment_id, "floorplan_id" : current_floorplan};
	 current_deployment = deployment_id;
	 $.ajax({
		 url:'/ibeaconapp/dataset/',
		 type:'GET',
		 data: parameters,
		 accept: 'application/json',
		 success: function(data, responseText, jqXHR) {
			 var html = "";
			 for(var i=0; i<data.datasetlist.length; i++){
			 html += '<option>';
			 html += data.datasetlist[i];
			 html += '</option>';
	 		}
	 document.getElementById("dataset_sel").innerHTML = html;
		 },
	 });
	 
	 // you may want to show the deployment when the user select one
	 showFloorplan(current_floorplan,true)
	 
	 
	 
}

//process dataset selection change
function onDatasetChange(dataset_id){
	// you may want to show dataset when user select one
	showDataset(current_floorplan,current_deployment,dataset_id);
}


function clearDatasetSelect(){
	var ds_sel = document.getElementById("dataset_sel");
	ds_sel.innerHTML = "";
}

function showFloorplan(floorplan_id,show_deployment){
	// show the floorplan here, you may need further AJAX calls to get the floorplan details back
	//alert("showing the floorplan");
	 var parameters = {"floorplan_id" : floorplan_id};
	 //current_floorplan = fp_sel[fp_sel.selectedIndex].value;
	 $.ajax({
		 url:'/ibeaconapp/floorplan/img/',
		 type:'GET',
		 data: parameters,
		 accept: 'application/json',
		 success: function(data, responseText, jqXHR) {
			 //alert("the floorplan img file name is: " + data.floorplan_img);
			 var myCanvas = document.getElementById('canvas_fp');
			 var ctx = myCanvas.getContext('2d');
			 ctx.clearRect(0, 0, myCanvas.width, myCanvas.height);
			 var img = new Image;
			 img.onload = function(){
				 ctx.drawImage(img,0,0); // Or at whatever offset you like
			 };
			 img.src = "/static/ibeaconapp/image/"+data.floorplan_img;
			 if(show_deployment)
				 showDeployment(floorplan_id,current_deployment)
		 },
		 failure: function(){alert('Failed to get floorplan list from server...');}
	 });
}

function showDeployment(floorplan_id,deployment_id){
	// show the deployment here, you may need further AJAX calls to get the deployment details back
	//alert("showing the deployment");
	var parameters = {"deployment_id" : deployment_id};
	current_deployment = deployment_id;
	$.ajax({
		url:'/ibeaconapp/deployment/beacons/',
		type:'GET',
		data: parameters,
		accept: 'application/json',
		success: function(data, responseText, jqXHR) {
			// draw the beacons on the canvas
			var canvas_fp = document.getElementById("canvas_fp");
			var ctx = canvas_fp.getContext("2d");
			for(var i=0; i<data.beaconlist.length; i++){
				ctx.beginPath();
				ctx.arc(data.beaconlist[i].x,data.beaconlist[i].y,10,0,2*Math.PI);
				ctx.stroke();
				ctx.beginPath();
				ctx.arc(data.beaconlist[i].x,data.beaconlist[i].y,15,0,2*Math.PI);
				ctx.stroke();
				ctx.beginPath();
				ctx.arc(data.beaconlist[i].x,data.beaconlist[i].y,20,0,2*Math.PI);
				ctx.stroke();
			}
			
		},
	});
}

function showDataset(floorplan_id,deployment_id,dataset_id){
	// show the dataset here, you may need further AJAX calls to get the dataset details back
	alert("showing the dataset");
}

