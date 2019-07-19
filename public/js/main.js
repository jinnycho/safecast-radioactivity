'use strict';
const width = 700;
const height = 580;
let view = "map";

/*
 * This file is mainly used to
 * 1. render the map
 * 2. visualize data 
 */

d3.json('./data/clusters-1.geojson', (err, data) => {
  drawMap(data);

  function drawMap(data) {
  mapboxgl.accessToken = 'pk.eyJ1IjoiamlubnljaG81MDMiLCJhIjoiY2o2am16cnA5MDhxMTMycGR0MXRhaDZxNiJ9.lNDt1qFFi4V7zUin8Jj1LQ';
  var map = new mapboxgl.Map({
    container: 'map',
    style: 'mapbox://styles/mapbox/light-v10',
    zoom: 1,
    center: [-122.447303, 37.753574]
  });

  var canvas = map.getCanvasContainer();

  // create SVG canvas
  var svg = d3.select(canvas)
    .append("svg")
    .attr("width", "100%")
    .attr("height", "100vh")
    .attr("position", "absolute");


  // Map d3 projection coordinates to Mapbox map coordinates 
  function projectPoint(lon, lat) {
    let point = map.project(new mapboxgl.LngLat(lon, lat));
    this.stream.point(point.x, point.y);
  }
  var transform = d3.geoTransform({point: projectPoint});

  // turn lat/lon coordinates into screen coordinates
  var path = d3.geoPath()
    .projection(transform);

  var points = svg.selectAll("path")
    .data(data.features)
    .enter()
    .append("path")
    .attr("d", path)
    .attr("fill", "#900")
    .attr("stroke", "#999");

  }

  function update() {
    if (view === "map") {
      drawMap(data);
    }
  }
  update(500);
});
