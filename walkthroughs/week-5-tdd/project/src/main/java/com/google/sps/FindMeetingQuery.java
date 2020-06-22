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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public final class FindMeetingQuery {
  /**
   * Determine available times for a meeting to take place given a list of Events and a
   * Meeting Request.
   * @param events the list of events to take into consideration when finding available times
   * @param request the meeting request with meeting attendees and duration
   * @return A list of available TimeRanges
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Meeting cannot last longer than 24 hours
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) return Arrays.asList();

    Collection<TimeRange> availableTimesWithOptionalAttendees = queryHelper(events, request, true);

    // If there are no available times with all (mandatory & optional) consider mandatory 
    if (availableTimesWithOptionalAttendees.size() == 0 && request.getAttendees().size() > 0) {
      return queryHelper(events, request, false);
    }

    return availableTimesWithOptionalAttendees;
  }

  /**
   * Helper method for the query method which finds the available times to meet given a
   * list of events and a MeetingRequest,
   * depending on whether optional attendees are considered or not.
   * @param events the list of events to take into consideration when finding available times
   * @param request the meeting request with the meeting attendees and duration
   * @param considerOptional whether optional attendees are accounted for or not
   * @return available times for the group to meet
   *
   */
  private Collection<TimeRange> queryHelper(Collection<Event> events, MeetingRequest request,
    boolean considerOptional) {
    
    Collection<String> attendees = new HashSet<String>(request.getAttendees());
    if (considerOptional) {
      attendees.addAll(request.getOptionalAttendees());
    }
    
    // Needs to have at least 1 attendee at a meeting
    if (attendees.size() == 0) return Arrays.asList(TimeRange.WHOLE_DAY);

    // For a time to be unavailable, there must be someone at 
    // the event who is in the meeting attendees list
    List<TimeRange> unavailableTimes = 
      new ArrayList(convertAttendeesAndEventsToUnavailableTimes(events, attendees));

    // If there are no unavailable times, people can meet any time!
    if (unavailableTimes.size() == 0) return Arrays.asList(TimeRange.WHOLE_DAY);

    Collections.sort(unavailableTimes, TimeRange.ORDER_BY_START);

    Collection<TimeRange> unavailableNoOverlaps = mergeTimeRanges(unavailableTimes);

    Collection<TimeRange> availableTimes = 
      findAvailableTimesFromUnavailableTimes(unavailableNoOverlaps);

    Collection<TimeRange> finalAvailableTimes =
      removeAllTimeRangesLessThanGivenDuration(availableTimes, request.getDuration());

    return finalAvailableTimes;
  }

  /**
   * Removes items from a Collection if they are shorter than the specified duration.
   * @param times a collection of TimeRanges
   * @param duration the threshold length of the meeting 
   * @return a new collection with a list of sufficient times at least as long as the duration
   */
  private static Collection<TimeRange> removeAllTimeRangesLessThanGivenDuration(
    Collection<TimeRange> times, long duration) {
  
    Collection<TimeRange> filtered = times.stream()
      .filter( time -> time.duration() >= duration )
      .collect(Collectors.toList());

    return filtered;
  }

  /**
   * Converts unavailable TimeRanges to available TimeRanges by 'subtracting' from a full day.
   * @param unavailableTimes a collection of unavailable TimeRanges
   * @return a collection representing the available times throughout the day
   */
  private static Collection<TimeRange> findAvailableTimesFromUnavailableTimes(
    Collection<TimeRange> unavailableTimes) {

    Collection<TimeRange> availableTimes = new ArrayList<>();
    int startTime = TimeRange.START_OF_DAY;
    int endTime;

    for (TimeRange tr : unavailableTimes) {
      endTime = tr.start();
      if (startTime != endTime) {
        availableTimes.add(TimeRange.fromStartEnd(startTime, endTime, false));
      }
      startTime = tr.end();
    }

    // Fencepost problem
    endTime = TimeRange.WHOLE_DAY.duration();
    if (startTime != endTime) {
      availableTimes.add(TimeRange.fromStartEnd(startTime, endTime, false));
    }
    return availableTimes;
  }

  /**
   * Merges TimeRanges that overlap with eachother into a new collection of TimeRanges.
   * @param times the list of TimeRanges to merge
   * @return a list of merged TimeRanges
   */
  private static Collection<TimeRange> mergeTimeRanges(List<TimeRange> times) {
    int startTime;
    int endTime;
    Collection<TimeRange> mergedTimes = new ArrayList<>();

    for (int i = 0; i < times.size(); i++) {
      TimeRange currTimeRange = times.get(i);
      startTime = currTimeRange.start();
      endTime = currTimeRange.end();

      for (int j = i; j < times.size(); j++) {
        TimeRange nextTimeRange = times.get(j);
        if (currTimeRange.overlaps(nextTimeRange)) {
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

  /**
   * Converts a list of Events and attendees to a collection of unavailable times
   * for the attendees to meet.
   * @param events a collection of events to consider for attendee busyness
   * @param attendees the collection of attendees to cross-compare with the events
   * @return a collection of TimeRanges when the attendees are not available to have a meeting
   */
  private static Collection<TimeRange> convertAttendeesAndEventsToUnavailableTimes(
    Collection<Event> events, Collection<String> attendees) {
    
    // The event times are only unavailable if there is a participant from the MeetingRequest
    // at the event
    Collection<TimeRange> filtered = events.stream()
      .filter( event -> doesEventHaveMeetingAttendee(event.getAttendees(), attendees) )
      .map( event -> event.getWhen() )
      .collect(Collectors.toList());

    return filtered;
  }

  /**
   * Determines whether there exists a meeting attendee that is involved in a given event. 
   * @param eventAttendees a collection of strings representing the attendees of an event
   * @param meetingAttendees a collection of strings representing the meeting attendees
   * @return a boolean; true if there exists at least one meeting attendee is in an event
   * or false otherwise
   */
  private static boolean doesEventHaveMeetingAttendee(Collection<String> eventAttendees,
    Collection<String> meetingAttendees) {

    return meetingAttendees.stream()
      .anyMatch( person -> eventAttendees.contains(person) );
  }
}