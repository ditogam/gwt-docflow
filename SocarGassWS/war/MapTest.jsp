<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
<meta name="apple-mobile-web-app-capable" content="yes">
<title>OpenLayers XYZ with Offset</title>
<link rel="stylesheet" href="../theme/default/style.css" type="text/css">
<link rel="stylesheet" href="style.css" type="text/css">
<script
	src="OpenLayers.js"></script>
<script type="text/javascript">
	var lon = 5;
	var lat = 40;
	var zoom = 5;
	var map, layer;
	function init() {
		var extent = new OpenLayers.Bounds(-2.003750834E7,-2.003750834E7,2.003750834E7,2.003750834E7);

		map = new OpenLayers.Map({
			div : "map",
			maxExtent : extent,
			resolutions : [  9.554628534317017,
					4.777314267158508, 2.388657133579254, 1.194328566789627,
					0.5971642833948135, 0.29858214169740677,
					0.14929107084870338, 0.07464553542435169,
					0.037322767712175846 ],
			projection : new OpenLayers.Projection("EPSG:900913"),
			units : "m",
			layers : [ new OpenLayers.Layer.XYZ("ESRI",
					"http://tilecache.osgeo.org/wms-c/Basic.py/", {
						sphericalMercator : true,
						getURL : function(bounds) {
							var xyz = this.getXYZ(bounds);
							var tilename = "XYZRequest?z=" + xyz.z + "&y="
									+ xyz.y + "&x=" + xyz.x;
							return tilename;
						}
					} // since our map maxResolution differs from cache max resolution
			) ]
		});

		map.zoomToExtent(extent);
		/*layer = new OpenLayers.Layer.XYZ(
				"ESRI",
				//"XYZRequest?z=${z}&y=${y}&x=${x}",
				"http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Portland/ESRI_LandBase_WebMercator/MapServer/tile/${z}/${y}/${x}",
				{
					sphericalMercator : true
				// load only tiles visible in map viewport
				});
		map.addLayer(layer);

		layer = new OpenLayers.Layer.WMS("OpenLayers WMS",
				"http://vmap0.tiles.osgeo.org/wms/vmap0", {
					layers : 'basic'
				});
		map.addLayer(layer);

		map.setCenter(new OpenLayers.LonLat(lon, lat), zoom);
		map.addControl(new OpenLayers.Control.LayerSwitcher());*/

	}
</script>
</head>
<body onload="init()">

	<div style="width: 100%; height: 800px" id="map"></div>

</body>
</html>
