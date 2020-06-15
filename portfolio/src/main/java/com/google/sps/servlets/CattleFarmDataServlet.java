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
import com.google.sps.data.CattleFarm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Returns CattleFarm data as a JSON object */
@WebServlet("/cattle-farm-data")
public class CattleFarmDataServlet extends HttpServlet {

  private List<CattleFarm> cattleFarms = new ArrayList<>();

  @Override
  public void init() {
    cattleFarms = loadCattleFarmData();
  }

  private List<CattleFarm> loadCattleFarmData() {
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream(
      "/WEB-INF/american-cattle-farm-locations.csv"));
    List<CattleFarm> cattleFarms = new ArrayList<>();
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");
      
      CattleFarm cattleFarm = new CattleFarm(cells[0], Double.parseDouble(cells[1]),
        Double.parseDouble(cells[2]), cells[3], cells[4], cells[5]);

      cattleFarms.add(cattleFarm);
    }
    scanner.close();

    return cattleFarms;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    
    String json = gson.toJson(cattleFarms);
    response.getWriter().println(json);
  }
}