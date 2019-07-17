'use strict';

const mapAccessToken = 'pk.eyJ1IjoiamlubnljaG81MDMiLCJhIjoiY2o2am16cnA5MDhxMTMycGR0MXRhaDZxNiJ9.lNDt1qFFi4V7zUin8Jj1LQ';

module.exports.drawMap = (accessToken) => {
  mapboxgl.accessToken = mapAccessToken;
  let map = new mapboxgl.Map({
      container: 'map',
      style: 'mapbox://styles/mapbox/light-v9',
      center: [-73.9500, 40.770],
      zoom: 10
  }) 
};
