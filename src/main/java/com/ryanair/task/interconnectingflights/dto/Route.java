package com.ryanair.task.interconnectingflights.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Route implements Serializable {
    private String airportFrom;
    private String airportTo;
    private String connectingAirport;
    private Boolean newRoute;
    private Boolean seasonalRoute;
    private String operator;
    private String group;
    private Set<String> similarArrivalAirportCodes;
    private Set<String> tags;
    private String carrierCode;
}
