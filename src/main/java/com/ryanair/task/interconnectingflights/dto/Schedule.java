package com.ryanair.task.interconnectingflights.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Schedule implements Serializable {
    private Integer month;
    private List<Day> days;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Day {
        private Integer day;
        private List<Flight> flights;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Flight implements Serializable {

        private String number;
        private String departureTime;
        private String arrivalTime;

    }

}
