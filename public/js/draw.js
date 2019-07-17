'use strict';

module.exports.drawMap = (accessToken) => {
  mapboxgl.accessToken = mapAccessToken;
  let map = new mapboxgl.Map({
      container: 'map',
      style: 'mapbox://styles/mapbox/light-v9',
      center: [-73.9500, 40.770],
      zoom: 10
  }) 
};
