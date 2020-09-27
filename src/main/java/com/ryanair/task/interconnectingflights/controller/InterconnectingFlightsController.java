package com.ryanair.task.interconnectingflights.controller;

import com.ryanair.task.interconnectingflights.dto.Interconnection;
import com.ryanair.task.interconnectingflights.service.InterconnectingFlightsService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("interconnections")
public class InterconnectingFlightsController {

    @Autowired
    private InterconnectingFlightsController(final InterconnectingFlightsService service) {
        this.service = service;
    }

    private final InterconnectingFlightsService service;

    @RequestMapping(method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 503, message = "Service Unavailable")
    })
    public Set<Interconnection> findFlights(@RequestParam String departure, @RequestParam String arrival,
                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalDateTime) {
        return service.findFlights(departure, arrival, departureDateTime, arrivalDateTime);
    }
}
