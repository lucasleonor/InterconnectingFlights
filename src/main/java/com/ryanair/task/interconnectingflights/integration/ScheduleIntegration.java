package com.ryanair.task.interconnectingflights.integration;

import com.ryanair.task.interconnectingflights.dto.Schedule;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScheduleIntegration {

    private static final String SCHEDULE_URL = "https://services-api.ryanair.com/timtbl/3/schedules/";
    private final WebClient WEB_CLIENT = WebClient.create(SCHEDULE_URL);

    public Set<Schedule> findSchedules(String departureAirport, String arrivalAirport, String year, String month) {
        String uri = String.format("%s/%s/years/%s/months/%s", departureAirport, arrivalAirport, year, month);
        WebClient.ResponseSpec retrieve = WEB_CLIENT.get().uri(uri).retrieve();
        return retrieve.bodyToFlux(Schedule.class).toStream().collect(Collectors.toSet());
    }
}
