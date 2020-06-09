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

/** A Year that contains the milk consumption per year for different dairy types. */
public final class DairyYear {

  private final int year;
  // Units for consumption are in millions of pounds
  private final double whole;
  private final double reducedFat;
  private final double lowFat;
  private final double skim;
  private final double flavoredWhole;
  private final double flavoredNonwhole;
  private final double buttermilk;
  private final double eggnog;
  private final double totalMilk;

  public DairyYear(int year, double whole, double reducedFat, double lowFat, double skim, double flavoredWhole, double flavoredNonwhole, double buttermilk, double eggnog, double totalMilk) {
    this.year = year;
    this.whole = whole;
    this.reducedFat = reducedFat;
    this.lowFat = lowFat;
    this.skim = skim;
    this.flavoredWhole = flavoredWhole;
    this.flavoredNonwhole = flavoredNonwhole;
    this.buttermilk = buttermilk;
    this.eggnog = eggnog;
    this.totalMilk = totalMilk;
  }

  public int getYear() {
    return year;
  }

  public double getWhole() {
    return whole;
  }

  public double getReducedFat() {
    return reducedFat;
  }

  public double getLowFat() {
    return lowFat;
  }

  public double getSkim() {
    return skim;
  }

  public double getFlavoredWhole() {
    return flavoredWhole;
  }

  public double getFlavoredNonwhole() {
    return flavoredNonwhole;
  }

  public double getButtermilk() {
    return buttermilk;
  }

  public double getEggnog() {
    return eggnog;
  }

  public double getTotalMilk() {
    return totalMilk;
  }
  
}



