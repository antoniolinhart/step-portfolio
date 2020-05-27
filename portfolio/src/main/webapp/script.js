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

/**
 * Generates a random color.
 * @returns {string} A color in HSL (hue, saturation, lightness) format
 */
function generateRandomColor() {
  const randomHue = Math.random() * 360; // max value 360
  const saturation =  (Math.random() * 40) + 40 // max value 100
  const lightness = (Math.random() * 30) + 60; // max value 100

  const randomHsl = `hsl(${randomHue}, ${saturation}%, ${lightness}%)`
  return randomHsl;
}

 /**
  * Changes the background of the page to be a random color.
  */
function setRandomBackgroundColor() {
  const bodyContainer = document.getElementById('main-body');
  bodyContainer.style.backgroundColor = generateRandomColor();
}