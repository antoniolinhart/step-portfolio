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

package com.google.sps.data;

/** A CattleFarm contains the name, location data and description of a CattleFarm. */
public final class CattleFarm {

  private final String name;
  private final double latitude;
  private final double longitude;
  private final String city;
  private final String state;
  private final String website;

  public CattleFarm(String name, double latitude, double longitude, String city, String state, String website) {
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.city = city;
    this.state = state;
    this.website = website;
  }
}