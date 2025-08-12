package com.mergingtonhigh.schoolmanagement.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.mergingtonhigh.schoolmanagement.domain.entities.ActivityCategory;

public interface ActivityCategoryRepository {

    List<ActivityCategory> findAll();

    List<ActivityCategory> findAllActive();

    Optional<ActivityCategory> findById(String id);

    ActivityCategory save(ActivityCategory category);

    void deleteById(String id);

    boolean existsById(String id);
}
