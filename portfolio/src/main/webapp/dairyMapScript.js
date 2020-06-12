// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/** Creates a map that shows a single marker. */
function createMap() {
  const map = new google.maps.Map(document.getElementById('dairy-map'),
      {center: {lat: 39.826499, lng: -98.580313}, zoom: 4});

  const trexMarker = new google.maps.Marker({
    position: {lat: 37.421903, lng: -122.084674},
    map: map,
    title: 'Stan the T-Rex',
    icon: './images/cow_annotation.png'
  });

  const trexInfoWindow =
      new google.maps.InfoWindow({content: 'This is Stan, the T-Rex statue.'});

  trexMarker.addListener('click', function() {
    trexInfoWindow.open(map, trexMarker);
  });
}

/**
 * Initializes map in dairy analytics webpage.
 */
function init() {
  document.getElementById('dairy-map').onload = createMap();
}

init();