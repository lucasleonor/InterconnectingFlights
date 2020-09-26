package com.ryanair.task.interconnectingflights.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Connection implements Serializable {
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;

}
