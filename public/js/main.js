'use strict';
const width = 700;
const height = 580;

/*
 * This file is mainly used to
 * 1. render the map
 * 2. visualize data 
 */

mapboxgl.accessToken = 'pk.eyJ1IjoiamlubnljaG81MDMiLCJhIjoiY2o2am16cnA5MDhxMTMycGR0MXRhaDZxNiJ9.lNDt1qFFi4V7zUin8Jj1LQ';
var map = new mapboxgl.Map({
    container: 'map',
    style: 'mapbox://styles/mapbox/light-v10',
    zoom: 12,
    center: [-122.447303, 37.753574]
});

// create SVG canvas
let svg = d3.select("body")
    .append("svg")
    .attr("width", width)
    .attr("height", height);

// append empty placeholder g element to SVG
// g will contain geometry elements
let g = svg.append("g");

// Width and Height of the whole visualization
// Set Projection Parameters
var albersProjection = d3.geoAlbers()
    .scale( 190000 )
    .rotate( [71.057,0] )
    .center( [0, 42.313] )
    .translate( [width/2,height/2] );

// turn lat/lon coordinates into screen coordinates
var geoPath = d3.geoPath()
    .projection(albersProjection);

d3.json('./data/clusters-1.geojson', (err, data) => {
  console.log(data);
  g.selectAll("path")
    .data(data.features)
    .enter()
    .append("path")
    .attr("fill", "#900")
    .attr("stroke", "#999")
    .attr("d", geoPath);
});
