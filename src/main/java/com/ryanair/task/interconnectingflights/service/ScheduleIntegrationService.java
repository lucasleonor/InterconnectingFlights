package com.ryanair.task.interconnectingflights.service;

import com.ryanair.task.interconnectingflights.dto.Schedule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ScheduleIntegrationService extends Integration {

    public ScheduleIntegrationService(@Value("${ryanair.services.schedule}") final String scheduleUrl) {
        super(scheduleUrl);
    }

    public Set<Schedule> findSchedules(String departureAirport, String arrivalAirport, String year, String month) {
        return clientGet(Schedule.class, (uriBuilder -> uriBuilder
                .path("{departure}/{arrival}/years/{year}/months/{month}")
                .build(departureAirport, arrivalAirport, year, month)
        ));
    }
}