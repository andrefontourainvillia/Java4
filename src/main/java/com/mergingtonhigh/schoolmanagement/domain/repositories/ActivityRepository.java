package com.mergingtonhigh.schoolmanagement.domain.repositories;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.mergingtonhigh.schoolmanagement.domain.entities.Activity;

public interface ActivityRepository {

    List<Activity> findAll();

    Optional<Activity> findByName(String name);

    List<Activity> findByDay(String day);

    List<Activity> findByTimeRange(LocalTime startTime, LocalTime endTime);

    List<Activity> findByDayAndTimeRange(String day, LocalTime startTime, LocalTime endTime);

    List<String> findAllUniqueDays();

    List<Activity> findByCategoryId(String categoryId);

    Activity save(Activity activity);

    void deleteByName(String name);

    boolean existsByName(String name);
}