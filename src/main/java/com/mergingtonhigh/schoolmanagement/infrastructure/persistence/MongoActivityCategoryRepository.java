package com.mergingtonhigh.schoolmanagement.infrastructure.persistence;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mergingtonhigh.schoolmanagement.domain.entities.ActivityCategory;

@Repository
public interface MongoActivityCategoryRepository extends MongoRepository<ActivityCategory, String> {

    List<ActivityCategory> findByActiveTrue();
}
