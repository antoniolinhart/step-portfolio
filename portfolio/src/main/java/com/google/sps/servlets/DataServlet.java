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
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays; 

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<String> data = createArrayList();
    String json = convertToJson(data);

    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
   * Creates an ArrayList with example data.
   * @return an ArrayList of strings with hardcoded data.
   */
  private static ArrayList<String> createArrayList() {
    return new ArrayList<String>(Arrays.asList("Apple", "Banana", "Clementine"));
  }

  /**
   * Converts an ArrayList of Strings into a JSON string using the Gson library.
   */
  private String convertToJson(ArrayList<String> data) {
    Gson gson = new Gson();
    String json = gson.toJson(data);
    return json;
  }
}
