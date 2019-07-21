/*
 * This file is mainly used to
 * 1. render the map
 * 2. visualize data
 */

/**
 * Mapbox setup
 */
mapboxgl.accessToken = 'pk.eyJ1IjoiamlubnljaG81MDMiLCJhIjoiY2o2am16cnA5MDhxMTMycGR0MXRhaDZxNiJ9.lNDt1qFFi4V7zUin8Jj1LQ';
const map = new mapboxgl.Map({
  container: 'map',
  style: 'mapbox://styles/mapbox/light-v10',
  zoom: 1,
  center: [-122.447303, 37.753574],
});

/**
 * Mapbox + D3
 */
const canvas = map.getCanvasContainer();
// overlay d3 on the map
const svg = d3.select(canvas)
  .append('svg');

map.on('load', function(result, err) {
  if (err) throw (new Error('Error loading a map'));
  d3.json('./data/clusters-1.geojson', (err, data1) => {
    if (err) throw (new Error('Error reading clusters-1.geojson'));
    d3.json('./data/clusters-2.geojson', (err, data2) => {
      if (err) throw (new Error('Error reading clusters-2.geojson'));
      d3.json('./data/clusters-3.geojson', (err, data3) => {
        if (err) throw (new Error('Error reading clusters-3.geojson'));
        d3.json('./data/clusters-4.geojson', (err, data4) => {
          if (err) throw (new Error('Error reading clusters-4.geojson'));
          d3.json('./data/clusters-5.geojson', (err, data5) => {
            if (err) throw (new Error('Error reading clusters-5.geojson'));
            drawMap(data1, data2, data3, data4, data5);
          });
        });
      });
    });
  });
});

// project geojson coordinate to the map's current state
function project(d) {
  return map.project(new mapboxgl.LngLat(+d[0], +d[1]));
}

/**
 * D3
 */
function circleColor(d, i) {
  if (d.properties.value < 10) return '#9ACD32';
  if (d.properties.value >= 10 && d.properties.value < 30) return '#FFD700';
  if (d.properties.value >= 30 && d.properties.value < 50) return '#FFA500';
  if (d.properties.value >= 50 && d.properties.value < 100) return '#DC143C';
  // warning level
  return '#283747';
}

const tooltip = d3.select('body')
  .append('div')
  .attr('class', 'tooltip')
  .style('opacity', 0);

let circles;
function drawMap(data1, data2, data3, data4, data5) {
  circles = svg.selectAll('circle')
    .data(data1.features)
    .data(data2.features)
    .data(data3.features)
    .data(data4.features)
    .data(data5.features)
    .enter()
    .append('circle')
    .attr('r', 7)
    .attr('fill', (d, i) => circleColor(d, i))
    .on('mouseover', (d) => {
      tooltip.transition()
        .duration(200)
        .style('opacity', 0.9);
      tooltip.html(d.properties.value.toFixed(2))
        .style('left', (d3.event.pageX) + 'px')
        .style('top', (d3.event.pageY - 10) + 'px');
    })
    .on('mouseout', (d) => {
      tooltip.transition()
        .duration(500)
        .style('opacity', 0);
    });

  update();
  map.on('viewreset', update);
  map.on('move', update);
  map.on('moveend', update);
}

function update() {
  circles
    .attr('cx', function(d) { return project(d.geometry.coordinates).x; })
    .attr('cy', function(d) { return project(d.geometry.coordinates).y; });
}
