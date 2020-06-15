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

/** Fetches data and uses it to create a chart depending on the dataset, properties and div name. */
async function drawChart(datasetName, properties, divName) {
  const data = createChartDataFromDataset(datasetName);
  let options = createChartsOptions();

  // merge custom properties with original options
  const modifiedOptions = { ...options, ...properties };

  const chart = new google.visualization.LineChart(document.getElementById(divName)); 
  chart.draw(data, modifiedOptions);
}

/** Creates both dairy charts with specified properties and names. */
async function createBothDairyCharts() {
  let consumptionProperties = {
    title: 'Dairy Consumption',
    vAxis: { 
      format: 'decimal',
      title: 'Consumption (millions of pounds)'
    }
  };

  let relativeConsumptionProperties = {
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
  
  const dairyDataInfo = [
    {
      name: 'consumption',
      properties: consumptionProperties,
      divName: 'yearly-chart-container'
    },
    {
      name: 'relativeConsumption',
      properties: relativeConsumptionProperties,
      divName: 'relative-chart-container'
    }
  ];
  
  dairyDataInfo.forEach((dataset) => {
    drawChart(dataset['name'], dataset['properties'], dataset['divName']);
  });
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
      google.charts.setOnLoadCallback(createBothDairyCharts);
    });
}

let dairyData;
init();