package com.ryanair.task.interconnectingflights.service;

import com.ryanair.task.interconnectingflights.dto.Interconnection;
import com.ryanair.task.interconnectingflights.integration.RouteIntegration;
import com.ryanair.task.interconnectingflights.integration.ScheduleIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class InterconnectingFlightsService {

    private final RouteIntegration routeIntegration;
    private final ScheduleIntegration scheduleIntegration;

    @Autowired
    private InterconnectingFlightsService(final RouteIntegration routeIntegration, final ScheduleIntegration scheduleIntegration) {
        this.routeIntegration = routeIntegration;
        this.scheduleIntegration = scheduleIntegration;
    }

    public Set<Interconnection> findFlights(final String departure, final String arrival,
                                            final LocalDateTime departureDateTime, final LocalDateTime arrivalDateTime) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
