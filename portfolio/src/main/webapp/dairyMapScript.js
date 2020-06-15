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

/** Instantiates the location of the map view and zoom level. */
function instantiateMap() {
  const geographicalUSCenter = {
    lat: 39.826499,
    lng: -98.580313
  };
  map = new google.maps.Map(document.getElementById('dairy-map'), {
    center: geographicalUSCenter,
    zoom: 4
  });
}

/** Creates a map that shows major US cattle farms. */
function createCattleFarmMap() {
  const cowAnnotationFilePath = './images/cow_annotation.png';
  cattleFarms.forEach((cFarm) => {
    const marker = new google.maps.Marker({
      map: map,
      position: {
        lat: cFarm.latitude,
        lng: cFarm.longitude
      },
      title: cFarm.name,
      icon: cowAnnotationFilePath
    });

    let infoWindow = new google.maps.InfoWindow({content: generateMarkerContent(cFarm)});
    infoWindows.push(infoWindow);
    
    marker.addListener('click', function() {
      // Close info windows so we can ensure there is only 1 visible at a time on the screen
      closeInfoWindows();
      infoWindow.open(map, marker);
    });
  });
}

/** Closes all info windows so only one displays at a time. */
function closeInfoWindows() {
  infoWindows.forEach((window) => {
    window.close();
  });
}

/** Generates the marker DOM content to be displayed when clicking on a map marker. */
function generateMarkerContent(cattleFarm) {
  const contentDiv = document.createElement('div');

  // Create header
  const locationTitle = document.createElement('h1');
  locationTitle.innerText = cattleFarm.name;
  locationTitle.className = 'info-window-txt';

  // Create description
  const locationDesc = document.createElement('p');
  locationDesc.innerText = `${cattleFarm.name} is a cattle farm located in ${cattleFarm.city}, ${cattleFarm.state}!`
  locationDesc.className = 'info-window-txt';

  // Link related descriptions
  const link1 = document.createElement('p');
  link1.innerText = "To learn more, please visit their website!"
  link1.className = 'info-window-txt';

  const link2 = document.createElement('a');
  link2.href = cattleFarm.website;
  link2.text = cattleFarm.website;
  link2.target = '_blank';

  contentDiv.appendChild(locationTitle);
  contentDiv.appendChild(locationDesc);
  contentDiv.appendChild(link1);
  contentDiv.appendChild(link2);

  return contentDiv;
}

/** Fetch cattle farm data from backend. */
async function fetchCattleFarms() {
  const response = await fetch("/cattle-farm-data");
  cattleFarms = await response.json();
}

/**
 * Initializes map in dairy analytics webpage.
 */
function init() {
  fetchCattleFarms()
  .then(() => {
    instantiateMap();
    document.getElementById('dairy-map').onload = createCattleFarmMap();
  });
}

let cattleFarms;
let map;
let infoWindows = [];
init();