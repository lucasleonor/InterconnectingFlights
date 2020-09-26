package com.ryanair.task.interconnectingflights.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ryanair.task.interconnectingflights.dto.Schedule;
import net.bytebuddy.utility.RandomString;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ScheduleIntegrationServiceTest {
    private static MockWebServer mockBackEnd;
    private static ObjectMapper objectMapper;
    private ScheduleIntegrationService scheduleIntegrationService;
    private int month;
    private String year;
    private String departure;
    private String arrival;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
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
        List<Schedule> response = Arrays.asList(createSchedule(), createSchedule());
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));

        Set<Schedule> actual = scheduleIntegrationService.findSchedules(departure, arrival, year, String.valueOf(month));

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod(), is("GET"));
        assertThat(recordedRequest.getPath(), is(String.format("/%s/%s/years/%s/months/%s", departure, arrival, year, month)));
        assertThat(actual, Matchers.containsInAnyOrder(response.toArray(new Schedule[0])));
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
        return new Schedule.Flight(RandomString.make(), randomLocalTime(), randomLocalTime());
    }

    private LocalTime randomLocalTime() {
        Random random = new Random();
        return LocalTime.of(random.nextInt(24), random.nextInt(60));
    }

}