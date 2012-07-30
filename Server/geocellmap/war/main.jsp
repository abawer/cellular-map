<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="javax.persistence.Query" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.geocellmap.EntityManagerService" %>
<%@ page import="com.geocellmap.DataPoint" %>
<%@ page import="javax.persistence.EntityManager" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Mobile Reception Map</title>
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBSMBZHetJUuw_CvqRtq_L0LuEGPmbjSL8&sensor=false&libraries=visualization"></script>
   <script type="text/javascript" src="convex_hull.js"></script>
   <script type="text/javascript">

      var map, pointarray, heatmap, markers = [], polygons=[], polypoints= {};
	  var markersVisible = true;
	  var polygonsVisible = true;
      var heatpoints = [   
<%
DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
EntityManager entityManager = EntityManagerService.get().createEntityManager();
Query pointsQuery = entityManager.createQuery("select d from DataPoint d");
List<DataPoint> points = new ArrayList<DataPoint>(pointsQuery.getResultList());

for (DataPoint p : points){
	%>
{location: new google.maps.LatLng( <%= p.getLatitude() %> , <%= p.getLongitude() %> ) , weight: <%= p.getSigLevel() %>}, 
	<%
}
%>     
      ];

      function initialize() {
        var mapOptions = {
          	zoom: 5,	 	 	
          	center: new google.maps.LatLng(31.7833, 35.2167),
          	mapTypeId: google.maps.MapTypeId.ROADMAP
        };

        map = new google.maps.Map(document.getElementById('map_canvas'),
            mapOptions);

        //create heatmap layer 
        pointArray = new google.maps.MVCArray(heatpoints);

        heatmap = new google.maps.visualization.HeatmapLayer({
          data: pointArray
        });

        heatmap.setMap(map);
		heatmap.setOptions({dissipating: true , maxIntensity: 31 });
		//create markers layer 
		<%
		for (DataPoint p : points){
			%>
			if(true)
			{
				var myLatLng = new google.maps.LatLng( <%= p.getLatitude() %> , <%= p.getLongitude() %> );
				var marker = new google.maps.Marker({
			        position: myLatLng,
			        map: map,
			        shadow: null,
			        title: 'Reception level: ' + <%= p.getSigLevel() %> + ' CID: ' + <%= p.getCellId() %>		      
			    });
				markers.push(marker);
			}
			<%
		}
		%>	
		
		//create polygons layer 		   
		<%
		for (DataPoint p : points){
			%>
			if(true){
		
				var myLatLng = new google.maps.LatLng( <%= p.getLatitude() %> , <%= p.getLongitude() %> );
				var cid = "<%= p.getCellId() %>" ;
		
				if (cid in polypoints == false){
					polypoints[cid]= [];
				}
				
				polypoints[cid].push( myLatLng );
			}
			<%
		}
		%>	
		
		for(var cid in polypoints){
			
			var points = polypoints[cid]; 
			
			points.sort(sortPointY);
			points.sort(sortPointX);
			var hullpoints = [];
		    chainHull_2D(points, points.length, hullpoints );
				   
		    var polygon = new google.maps.Polygon({
		        paths: hullpoints,
		        strokeColor: "#FF0000",
		        strokeOpacity: 0.8,
		        strokeWeight: 2,
		        fillColor: "#383838",
		        fillOpacity: 0.35
		      });
		    attachPolygonInfoWindow(polygon,"CID: "+cid)
			polygon.setMap(map);
			polygons.push(polygon);
		}
      }

      function toggleHeatmap() {
        heatmap.setMap(heatmap.getMap() ? null : map);
      }
      
      function toggleMarkers(){
    	  markersVisible = !markersVisible;
    	  for (var i = 0; i < markers.length; i++) {
    		  var marker = markers[i];
    		  marker.setVisible(markersVisible);
    		}
      }
      
      function togglePolygons(){
    	  polygonsVisible = !polygonsVisible;
    	  for (var i = 0; i < polygons.length; i++) {
    		  var polygon = polygons[i];
    		  polygon.setMap(polygonsVisible ? map : null);
    		}
      }
      
      function changeRadius() {
        heatmap.setOptions({radius: heatmap.get('radius') ? null : 20});
      }
      
      
      function attachPolygonInfoWindow(polygon, string)
      {
      	  polygon.infoWindow = new google.maps.InfoWindow({
      			content: string,
      			});
	      google.maps.event.addListener(polygon, 'mouseover', function(e) {
	      	var latLng = e.latLng;
	      	this.setOptions({fillOpacity:0.6, fillColor: "#FF0000"});
	      	polygon.infoWindow.setPosition(latLng);
	      	polygon.infoWindow.open(map);
	      });
	      google.maps.event.addListener(polygon, 'mouseout', function() {
	      	this.setOptions({fillOpacity:0.35, fillColor: "#383838"});
	      	polygon.infoWindow.close();
	      });
      }
	  
    </script>
  </head>

  <body onload="initialize()">
    <div id="map_canvas" style="height: 768px; width: 1024px;"></div>
    <button onclick="toggleHeatmap()">Toggle Heatmap</button>
    <button onclick="toggleMarkers()">Toggle Markers</button>
    <button onclick="togglePolygons()">Toggle Polygons</button>
    <button onclick="changeRadius()">Change Heatmap Radius</button>    
  </body>
</html>