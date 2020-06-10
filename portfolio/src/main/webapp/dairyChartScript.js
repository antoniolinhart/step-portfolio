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

/** Fetches yearly dairy consumption data and uses it to create a chart. */
async function drawDairyChart() {
  const data = createChartDataFromDataset('consumption');
  let options = createChartsOptions();

  let properties = {
    title: 'Dairy Consumption',
    vAxis: { 
      format: 'decimal',
      title: 'Consumption (millions of pounds)'
    }
  };
  // Add all custom properties to the options object
  for(let p in properties) options[p] = properties[p];

  const chart = new google.visualization.LineChart(document.getElementById('yearly-chart-container'));    
  chart.draw(data, options);
}

/** Fetches relative dairy consumption data and uses it to create a chart. */
async function drawRelativeDairyChart() {
  const data = createChartDataFromDataset('relativeConsumption');
  let options = createChartsOptions();
  
  let properties = {
    title: 'Year-Over-Year Dairy Growth',
    vAxis: { 
      format: 'percent',
      title: 'Percentage Change',
      viewWindow: {
        max: 0.35,
        min: -0.35
      },
      ticks: [-0.3, -0.2, -0.1, 0, 0.1, 0.2, 0.3]
    }
  };
  // Add all custom properties to the options object
  for(let p in properties) options[p] = properties[p];

  const chart = new google.visualization.LineChart(document.getElementById('relative-chart-container'));    
  chart.draw(data, options);
}

/** Creates data variable used in chart creation depending on dataset name. */
function createChartDataFromDataset(datasetName) {
  const data = new google.visualization.DataTable();
  data.addColumn('number', 'Year')
  data.addColumn('number', 'Whole')
  data.addColumn('number', 'Reduced-fat')
  data.addColumn('number', 'Low-fat')
  data.addColumn('number', 'Skim')
  data.addColumn('number', 'Flavored whole')
  data.addColumn('number', 'Flavored nonwhole')
  data.addColumn('number', 'Buttermilk')
  data.addColumn('number', 'Eggnog')
  data.addColumn('number', 'Total')

  dairyData[datasetName].forEach((dairyYear) => {
    data.addRow([dairyYear.year, dairyYear.whole,
                 dairyYear.reducedFat, dairyYear.lowFat,
                 dairyYear.skim, dairyYear.flavoredWhole,
                 dairyYear.flavoredNonwhole, dairyYear.buttermilk,
                 dairyYear.eggnog, dairyYear.totalMilk]);
  });

  return data;
}

/** Creates options variable used in chart creation. */
function createChartsOptions() {
  let options = {
    theme: 'material',
    width: 700,
    height: 400,
    hAxis: { 
      format: '####',
      title: 'Year',
      viewWindow: {
        max: 2018,
        min: 1975
      },
      ticks: [1975, 1985, 1995, 2005, 2015]
    }
  };
  return options;
}

/** Fetch dairy data from backend. */
async function fetchDairyData() {
  const response = await fetch("/milk-data");
  dairyData = await response.json();
}

/**
 * Initializes charts in dairy analytics webpage.
 */
function init() {
  google.charts.load('current', {'packages':['corechart']});

  fetchDairyData()
    .then(() => { 
      google.charts.setOnLoadCallback(drawDairyChart);
      google.charts.setOnLoadCallback(drawRelativeDairyChart);
    });
}

let dairyData;
init();