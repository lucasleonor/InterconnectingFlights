package com.ryanair.task.interconnectingflights.service;

import com.ryanair.task.interconnectingflights.dto.Interconnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class InterconnectingFlightsService {

    private final RouteIntegrationService routeIntegrationService;
    private final ScheduleIntegrationService scheduleIntegrationService;

    @Autowired
    public InterconnectingFlightsService(final RouteIntegrationService routeIntegrationService, final ScheduleIntegrationService scheduleIntegrationService) {
        this.routeIntegrationService = routeIntegrationService;
        this.scheduleIntegrationService = scheduleIntegrationService;
    }

    public Set<Interconnection> findFlights(final String departure, final String arrival,
                                            final LocalDateTime departureDateTime, final LocalDateTime arrivalDateTime) {
        Set<String> connectingAirports = routeIntegrationService.getConnectingAirports(departure, arrival);
        throw new UnsupportedOperationException("Not implemented yet.");
    }

}
