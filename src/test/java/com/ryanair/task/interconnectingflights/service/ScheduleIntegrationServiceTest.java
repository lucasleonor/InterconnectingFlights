package com.ryanair.task.interconnectingflights.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ryanair.task.interconnectingflights.RandomDateTime;
import com.ryanair.task.interconnectingflights.dto.Flight;
import com.ryanair.task.interconnectingflights.dto.Schedule;
import net.bytebuddy.utility.RandomString;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class ScheduleIntegrationServiceTest {
    private static MockWebServer mockBackEnd;
    private static ObjectMapper objectMapper;
    private ScheduleIntegrationService scheduleIntegrationService;
    private int month;
    private String year;
    private String departure;
    private String arrival;
    private static RandomDateTime dateTimeGenerator;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        dateTimeGenerator = new RandomDateTime();
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void setUp() {
        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        scheduleIntegrationService = new ScheduleIntegrationService(baseUrl);
        month = new Random().nextInt();
        year = RandomString.make();
        departure = RandomString.make();
        arrival = RandomString.make();
    }

    @Test
    void findSchedules() throws JsonProcessingException, InterruptedException {
        Schedule schedule = createSchedule();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(schedule))
                .addHeader("Content-Type", "application/json"));

        Schedule actual = scheduleIntegrationService.findSchedules(departure, arrival, year, String.valueOf(month));

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod(), is("GET"));
        assertThat(recordedRequest.getPath(), is(String.format("/%s/%s/years/%s/months/%s", departure, arrival, year, month)));
        assertThat(actual, is(schedule));
    }

    private Schedule createSchedule() {
        return new Schedule(month, Arrays.asList(
                createDay(), createDay()
        ));
    }

    private Schedule.Day createDay() {
        return new Schedule.Day(new Random().nextInt(), Arrays.asList(
                createFlight(),
                createFlight()
        ));
    }

    private Schedule.Flight createFlight() {
        return new Schedule.Flight(dateTimeGenerator.randomLocalTime(), dateTimeGenerator.randomLocalTime());
    }

    @Test
    void findAllFlights() {
        scheduleIntegrationService = mock(ScheduleIntegrationService.class, CALLS_REAL_METHODS);
        String departure = RandomString.make();
        String arrival = RandomString.make();
        LocalDateTime departureDateTime = dateTimeGenerator.randomLocalDate().atStartOfDay();
        LocalDateTime arrivalDateTime = departureDateTime.plusDays(1);

        Schedule.Flight flight = createFlight();
        Schedule.Flight flight1 = createFlight();
        doReturn(new Schedule(departureDateTime.getMonthValue(), Arrays.asList(
                new Schedule.Day(departureDateTime.minusDays(2).getDayOfMonth(), Arrays.asList(
                        createFlight(), createFlight()
                )),
                new Schedule.Day(arrivalDateTime.plusDays(2).getDayOfMonth(), Arrays.asList(
                        createFlight(), createFlight()
                )),
                new Schedule.Day(departureDateTime.getDayOfMonth(), Arrays.asList(
                        flight, flight1
                ))))
        ).when(scheduleIntegrationService).findSchedules(departure, arrival,
                String.valueOf(departureDateTime.getYear()), String.valueOf(departureDateTime.getMonthValue()));

        Set<Flight> allFlights = scheduleIntegrationService.findAllFlights(departure, arrival,
                departureDateTime, arrivalDateTime);

        LocalDate departureDate = departureDateTime.toLocalDate();
        assertThat(allFlights, containsInAnyOrder(
                new Flight(departure, arrival, departureDate.atTime(flight.getDepartureTime()), departureDate.atTime(flight.getArrivalTime())),
                new Flight(departure, arrival, departureDate.atTime(flight1.getDepartureTime()), departureDate.atTime(flight1.getArrivalTime()))
        ));

    }
}