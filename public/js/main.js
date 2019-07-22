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

// load data
map.on('load', (result, err) => {
  if (err) throw (new Error('Error loading a map'));
  queue(6)
    .defer(d3.json, './data/clusters-1.geojson')
    .defer(d3.json, './data/clusters-2.geojson')
    .defer(d3.json, './data/clusters-3.geojson')
    .defer(d3.json, './data/clusters-4.geojson')
    .defer(d3.json, './data/clusters-5.geojson')
    .defer(d3.json, './data/clusters-6.geojson')
    .await(drawMap);
});

// project geojson coordinate to the map's current state
const project = (d) => {
  return map.project(new mapboxgl.LngLat(+d[0], +d[1]));
}

/**
 * D3
 */
const circleColor = (d, i) => {
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
const drawMap = (err, data1, data2, data3, data4, data5, data6) => {
  if (err) throw (new Error('Error calling drawMap()'));
  circles = svg.selectAll('circle')
    .data(data1.features)
    .data(data2.features)
    .data(data3.features)
    .data(data4.features)
    .data(data5.features)
    .data(data6.features)
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

const update = () => {
  circles
    .attr('cx', (d) => { return project(d.geometry.coordinates).x; })
    .attr('cy', (d) => { return project(d.geometry.coordinates).y; });
}
