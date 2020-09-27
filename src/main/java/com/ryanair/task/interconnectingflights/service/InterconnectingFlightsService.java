package com.ryanair.task.interconnectingflights.service;

import com.ryanair.task.interconnectingflights.dto.Flight;
import com.ryanair.task.interconnectingflights.dto.Interconnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        Set<Flight> directFlights = scheduleIntegrationService.findAllFlights(departure, arrival, departureDateTime, arrivalDateTime);

        Set<Interconnection> availableOptions = directFlights.stream().
                map(connection -> new Interconnection(Collections.singleton(connection)))
                .collect(Collectors.toSet());

        for (String connectingAirport : connectingAirports) {
            Set<Flight> allFlightsDepartureToConnection = scheduleIntegrationService.findAllFlights(departure, connectingAirport, departureDateTime, arrivalDateTime);
            Set<Flight> allFlightsConnectionToArrival = scheduleIntegrationService.findAllFlights(connectingAirport, arrival, departureDateTime, arrivalDateTime);
            availableOptions.addAll(matchFlights(allFlightsDepartureToConnection, allFlightsConnectionToArrival));
        }
        return availableOptions;
    }

    protected Set<Interconnection> matchFlights(final Set<Flight> flightsDepartureToConnection, final Set<Flight> flightsConnectionToArrival) {
        Set<Interconnection> matches = new HashSet<>();
        for (Flight departureToConnection : flightsDepartureToConnection) {
            LocalDateTime flightsFrom = departureToConnection.getArrivalDateTime().plusHours(2);
            for (Flight flight : flightsConnectionToArrival) {
                if (!flight.getDepartureDateTime().isBefore(flightsFrom)) {
                    matches.add(new Interconnection(Set.of(departureToConnection, flight)));
                }
            }
        }
        return matches;
    }

}
