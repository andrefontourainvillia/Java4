package com.mergingtonhigh.schoolmanagement.domain.valueobjects;

import java.time.LocalTime;
import java.util.List;

public record ScheduleDetails(
        List<String> days,
        LocalTime startTime,
        LocalTime endTime) {
    public ScheduleDetails {
        if (days == null || days.isEmpty()) {
            throw new IllegalArgumentException("Dias não podem ser nulos ou vazios");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("Horário de início não pode ser nulo");
        }
        if (endTime == null) {
            throw new IllegalArgumentException("Horário de fim não pode ser nulo");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Horário de início não pode ser posterior ao horário de fim");
        }
    }
}