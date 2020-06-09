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

package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.sps.data.DairyYear;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Returns milk data as a JSON object */
@WebServlet("/milk-data")
public class MilkDataServlet extends HttpServlet {

  private ArrayList<DairyYear> milkConsumption = new ArrayList<DairyYear>();
  private ArrayList<DairyYear> relativeMilkConsumption = new ArrayList<DairyYear>();

  @Override
  public void init() {
    milkConsumption = loadYearlyMilkData();
    relativeMilkConsumption = generateRelativeMilkData(milkConsumption);
  }

  private ArrayList<DairyYear> loadYearlyMilkData() {
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream(
      "/WEB-INF/milk-consumption-by-year.csv"));
    ArrayList<DairyYear> milkConsumption = new ArrayList<DairyYear>();
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");
      
      DairyYear milkYear = new DairyYear(
        Integer.parseInt(cells[0]), Double.parseDouble(cells[1]),
        Double.parseDouble(cells[2]), Double.parseDouble(cells[3]),
        Double.parseDouble(cells[4]), Double.parseDouble(cells[5]),
        Double.parseDouble(cells[6]), Double.parseDouble(cells[7]),
        Double.parseDouble(cells[8]), Double.parseDouble(cells[9])
      );

      milkConsumption.add(milkYear);
    }
    scanner.close();

    return milkConsumption;
  }

  private ArrayList<DairyYear> generateRelativeMilkData(ArrayList<DairyYear> originalData) {
    ArrayList<DairyYear> relativeMilkConsumption = new ArrayList<DairyYear>();
    
    DairyYear baseYear = new DairyYear(1975, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    relativeMilkConsumption.add(baseYear);

    for(int i = 0; i < originalData.size() - 1; i++) {
      baseYear = originalData.get(i);
      DairyYear compareYear = originalData.get(i + 1);

      // Calculate the differences
      double wholeD = calculatePercentageChange(baseYear.getWhole(), compareYear.getWhole());
      double reducedFatD = calculatePercentageChange(baseYear.getReducedFat(),
                                                     compareYear.getReducedFat());
      double lowFatD = calculatePercentageChange(baseYear.getLowFat(), compareYear.getLowFat());
      double skimD = calculatePercentageChange(baseYear.getSkim(), compareYear.getSkim());
      double flavoredWholeD = calculatePercentageChange(baseYear.getFlavoredWhole(),
                                                        compareYear.getFlavoredWhole());
      double flavoredNonWholeD = calculatePercentageChange(baseYear.getFlavoredNonwhole(),
                                                           compareYear.getFlavoredNonwhole());
      double buttermilkD = calculatePercentageChange(baseYear.getButtermilk(),
                                                     compareYear.getButtermilk());
      double eggnogD = calculatePercentageChange(baseYear.getEggnog(), compareYear.getEggnog());
      double totalMilkD = calculatePercentageChange(baseYear.getTotalMilk(),
                                                    compareYear.getTotalMilk());

      DairyYear relativeYear = new DairyYear(
        compareYear.getYear(), wholeD, reducedFatD, lowFatD, skimD, flavoredWholeD,
        flavoredNonWholeD, buttermilkD, eggnogD, totalMilkD
      );
      relativeMilkConsumption.add(relativeYear);
    }
    return relativeMilkConsumption;
  }

  /**
   * Calculate the percentage difference of two doubles in comparison to the original.
   * @param original The 'base' number to consider in the calculation
   * @param newData The number to compare to the original
   * @return The percentage difference between the two numbers
   */
  private static double calculatePercentageChange(double original, double newData) {
    return ((newData - original) / original);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    HashMap<String, ArrayList<DairyYear>> dairyData = new HashMap<String, ArrayList<DairyYear>>();
    dairyData.put("consumption", milkConsumption);
    dairyData.put("relativeConsumption", relativeMilkConsumption);
    
    String json = gson.toJson(dairyData);
    response.getWriter().println(json);
  }
}