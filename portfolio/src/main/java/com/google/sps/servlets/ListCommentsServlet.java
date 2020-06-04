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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for retrieving comments from Datastore. */
@WebServlet("/list-comments")
public class ListCommentsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    int numComments = parseNaturalNumber(request.getParameter("numComments"));

    List<Entity> entities = results.asList(FetchOptions.Builder.withLimit(numComments));
    List<Comment> comments = new ArrayList<>();
    for (Entity entity : entities) {
      long id = entity.getKey().getId();
      String authorName = (String) entity.getProperty("authorName");
      String commentText = (String) entity.getProperty("commentText");
      long timestamp = (long) entity.getProperty("timestamp");

      Comment comment = new Comment(id, authorName, commentText, timestamp);
      comments.add(comment);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  /**
   * Convert a string into a integer. If not a natural number,
   * return 0 with an error message.
   * @param A string that will be parsed into a natural number
   * @return The number as an int, 0 if input is invalid
   */
  private static int parseNaturalNumber(String stringNum) {
    int num = 0;

    try {
      num = Integer.parseInt(stringNum);
    } catch (NumberFormatException e) {
      System.err.println("Did not input a valid integer in String form.");
      System.err.println(e);
    }

    if (num < 0) {
      System.err.println("Incorrectly input a negative number, which is not a natural number.");
      num = 0;
    }

    return num;
  }
}
