package com.ryanair.task.interconnectingflights.service;

import com.ryanair.task.interconnectingflights.dto.Flight;
import com.ryanair.task.interconnectingflights.dto.Schedule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@Service
public class ScheduleIntegrationService extends Integration {

    public ScheduleIntegrationService(@Value("${ryanair.services.schedule}") final String scheduleUrl) {
        super(scheduleUrl);
    }

    protected Schedule clientGetSingle(Function<UriBuilder, URI> uri) {
        return get(uri).bodyToMono(Schedule.class).block();
    }

    public Schedule findSchedules(final String departureAirport, final String arrivalAirport,
                                  final String year, final String month) {
        return clientGetSingle((uriBuilder -> uriBuilder
                .path("{departure}/{arrival}/years/{year}/months/{month}")
                .build(departureAirport, arrivalAirport, year, month)
        ));
    }

    public Set<Flight> findAllFlights(final String departure, final String arrival, final LocalDateTime departureDateTime, final LocalDateTime arrivalDateTime) {
        final LocalDate departureDate = departureDateTime.toLocalDate();
        final LocalDate arrivalDate = arrivalDateTime.toLocalDate();

        final Set<Flight> flights = new HashSet<>();
        YearMonth flightsOn = YearMonth.from(departureDateTime);
        do {
            Schedule schedule = findSchedules(departure, arrival, String.valueOf(flightsOn.getYear()), String.valueOf(flightsOn.getMonthValue()));
            for (Schedule.Day day : schedule.getDays()) {
                final LocalDate date = flightsOn.atDay(day.getDay());
                if (date.isBefore(departureDate) || date.isAfter(arrivalDate)) continue;
                for (Schedule.Flight flight : day.getFlights()) {
                    final LocalDateTime flightDeparture = LocalDateTime.of(date, flight.getDepartureTime());
                    final LocalDateTime flightArrival = LocalDateTime.of(date, flight.getArrivalTime());
                    if (!flightDeparture.isBefore(departureDateTime) && !flightArrival.isAfter(arrivalDateTime))
                        flights.add(new Flight(departure, arrival, flightDeparture, flightArrival));
                    // todo: do I need to check for edge cases? e.g. flight leaves at 11 pm and arrives at 1am on the following day
                }
            }

            flightsOn = flightsOn.plusMonths(1);
        } while (flightsOn.atDay(1).atStartOfDay().isBefore(arrivalDateTime));
        return flights;
    }
}
