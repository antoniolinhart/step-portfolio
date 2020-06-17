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

package com.google.sps;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Meeting cannot last longer than 24 hours
    if(request.getDuration() > TimeRange.WHOLE_DAY.duration()) return Arrays.asList();

    // Needs to have at least 1 attendee at a meeting
    if(request.getAttendees().size() == 0) return Arrays.asList(TimeRange.WHOLE_DAY);

    // For a time to be unavailable, there must be someone at the event who is in the meeting attendees list
    List<TimeRange> unavailableTimes = new ArrayList(convertAttendeesAndEventsToUnavailableTimes(events, request));

    // If there are no unavailable times, people can meet any time!
    if(unavailableTimes.size() == 0) return Arrays.asList(TimeRange.WHOLE_DAY);

    Collections.sort(unavailableTimes, TimeRange.ORDER_BY_START);

    Collection<TimeRange> unavailableNoOverlaps = mergeTimeRanges(unavailableTimes);

    Collection<TimeRange> availableTimes = findAvailableTimesFromUnavailableTimes(unavailableNoOverlaps);

    Collection<TimeRange> finalAvailableTimes = removeAllTimeRangesLessThanGivenDuration(availableTimes, request.getDuration());

    return finalAvailableTimes;
  }

  private static Collection<TimeRange> removeAllTimeRangesLessThanGivenDuration(Collection<TimeRange> times, long duration) {
    Collection<TimeRange> sufficientTimes = new ArrayList<>(); 
    for(TimeRange tr : times) {
      if(tr.duration() >= duration) {
        sufficientTimes.add(tr);
      }
    }
    return sufficientTimes;
  }

  private static Collection<TimeRange> findAvailableTimesFromUnavailableTimes(Collection<TimeRange> unavailableTimes) {
    Collection<TimeRange> availableTimes = new ArrayList<>();
    int startTime = TimeRange.START_OF_DAY;
    int endTime;

    for(TimeRange tr : unavailableTimes) {
      endTime = tr.start();
      if(startTime != endTime) {
        availableTimes.add(TimeRange.fromStartEnd(startTime, endTime, false));
      }
      startTime = tr.end();
    }

    // Fencepost problem
    endTime = TimeRange.WHOLE_DAY.duration();
    if(startTime != endTime) {
      availableTimes.add(TimeRange.fromStartEnd(startTime, endTime, false));
    }
    return availableTimes;
  }

  private static Collection<TimeRange> mergeTimeRanges(List<TimeRange> times) {
    int startTime;
    int endTime;
    Collection<TimeRange> mergedTimes = new ArrayList<>();

    for(int i = 0; i < times.size(); i++) {
      TimeRange currTimeRange = times.get(i);
      startTime = currTimeRange.start();
      endTime = currTimeRange.end();

      for(int j = i; j < times.size(); j++) {
        TimeRange nextTimeRange = times.get(j);
        if(currTimeRange.overlaps(nextTimeRange)) {
          endTime = Math.max(currTimeRange.end(), nextTimeRange.end());
          i = j;
        } else {
          break;
        }
      }
      mergedTimes.add(TimeRange.fromStartEnd(startTime, endTime, false));
    }
    return mergedTimes;
  }

  private static Collection<TimeRange> convertAttendeesAndEventsToUnavailableTimes(Collection<Event> events, MeetingRequest request) {
    // The event times are only unavailable if there is a participant from the MeetingRequest at the event
    List<TimeRange> unavailableTimes = new ArrayList<>();
    for(Event e : events) {
      if(doesEventHaveRequiredMeetingAttendee(e.getAttendees(), request.getAttendees())) {
        unavailableTimes.add(e.getWhen());
      }
    }
    return unavailableTimes;
  }

  private static boolean doesEventHaveRequiredMeetingAttendee(Collection<String> eventAttendees, Collection<String> reqAttendees) {
    for(String person : reqAttendees) {
      if(eventAttendees.contains(person)) {
        return true;
      }
    }
    return false;
  }
}