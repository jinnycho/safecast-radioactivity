'use strict';
const width = 700;
const height = 580;
let view = "map";

/*
 * This file is mainly used to
 * 1. render the map
 * 2. visualize data 
 */

/**
 * Mapbox setup 
 */
mapboxgl.accessToken = 'pk.eyJ1IjoiamlubnljaG81MDMiLCJhIjoiY2o2am16cnA5MDhxMTMycGR0MXRhaDZxNiJ9.lNDt1qFFi4V7zUin8Jj1LQ';
var map = new mapboxgl.Map({
  container: 'map',
  style: 'mapbox://styles/mapbox/light-v10',
  zoom: 1,
  center: [-122.447303, 37.753574]
});

/**
 * Mapbox + D3
 */
var canvas = map.getCanvasContainer();
// overlay d3 on the map
var svg = d3.select(canvas)
    .append("svg")

map.on('load', function() {
  d3.json('./data/clusters-1.geojson', (err, data) => {
    drawMap(data);
  });
});

// project geojson coordinate to the map's current state
function project(d) {
  return map.project(new mapboxgl.LngLat(+d[0], +d[1]));
}

/**
 * D3
 */
var circles;
function drawMap(data) {
  console.log("drawMap");
  circles = svg.selectAll("circle")
              .data(data.features)
              .enter()
              .append("circle")
              .attr("r", 16)
  update();
  map.on("viewreset", update);
  map.on("move", update);
  map.on("moveend", update);
} 

function update() {
  console.log("update");
  circles
    .attr("cx", function(d) { return project(d.geometry.coordinates).x })
    .attr("cy", function(d) { return project(d.geometry.coordinates).y });
}
