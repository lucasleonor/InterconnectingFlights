package com.ryanair.task.interconnectingflights.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Schedule implements Serializable {
    private Integer month;
    private List<Day> days;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class Day {
        private Integer day;
        private List<Flight> flights;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class Flight implements Serializable {

        @JsonFormat(pattern = "HH:mm")
        private LocalTime departureTime;
        @JsonFormat(pattern = "HH:mm")
        private LocalTime arrivalTime;

    }

}
