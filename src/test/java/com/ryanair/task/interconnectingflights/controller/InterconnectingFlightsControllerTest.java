package com.ryanair.task.interconnectingflights.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ryanair.task.interconnectingflights.RandomDateTime;
import com.ryanair.task.interconnectingflights.dto.Flight;
import com.ryanair.task.interconnectingflights.dto.Interconnection;
import com.ryanair.task.interconnectingflights.service.InterconnectingFlightsService;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InterconnectingFlightsControllerTest {

    private static RandomDateTime dateTimeGenerator;
    private static ObjectMapper objectMapper;
    @LocalServerPort
    private int port;
    @MockBean
    private InterconnectingFlightsService mockService;
    private String departure;
    private String arrival;
    private LocalDateTime arrivalDateTime;
    private LocalDateTime departureDateTime;

    @BeforeAll
    static void beforeAll() {
        dateTimeGenerator = new RandomDateTime();
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void setUp() {
        departure = RandomString.make();
        arrival = RandomString.make();
        arrivalDateTime = dateTimeGenerator.randomLocalDateTime();
        departureDateTime = dateTimeGenerator.randomLocalDateTime();
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void findFlights() throws JsonProcessingException {
        Set<Interconnection> interconnections = Set.of(
                new Interconnection(Set.of(new Flight(departure, arrival, departureDateTime.plusHours(4), arrivalDateTime.minusHours(1)))),
                new Interconnection(Set.of(new Flight(departure, arrival, departureDateTime.plusHours(6), arrivalDateTime.minusHours(7))))
        );
        doReturn(interconnections).when(mockService).findFlights(departure, arrival, departureDateTime, arrivalDateTime);

        Response response = given()
                .param("departure", departure)
                .param("arrival", arrival)
                .param("departureDateTime", departureDateTime.format(DateTimeFormatter.ISO_DATE_TIME))
                .param("arrivalDateTime", arrivalDateTime.format(DateTimeFormatter.ISO_DATE_TIME))
                .when()
                .get("/interconnections");
        response
                .then()
                .assertThat()
                .statusCode(200);
        Set<Interconnection> actual = Set.of(objectMapper.readValue(response.body().asString(), Interconnection[].class));
        assertThat(actual, is(interconnections));
    }
}