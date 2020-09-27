package com.ryanair.task.interconnectingflights;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

public class RandomDateTime {

    private final Random random;

    public RandomDateTime() {
        random = new Random();
    }

    public LocalTime randomLocalTime() {
        return LocalTime.of(random.nextInt(24), random.nextInt(60));
    }

    public LocalDate randomLocalDate() {
        return LocalDate.of(random.nextInt(3000), random.nextInt(11) + 1, random.nextInt(27) + 1);
    }

    public LocalDateTime randomLocalDateTime() {
        return LocalDateTime.of(randomLocalDate(), randomLocalTime());
    }
}
