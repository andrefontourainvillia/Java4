package com.mergingtonhigh.schoolmanagement.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.mergingtonhigh.schoolmanagement.domain.entities.ActivityCategory;
import com.mergingtonhigh.schoolmanagement.domain.repositories.ActivityCategoryRepository;

@Repository
public class ActivityCategoryRepositoryImpl implements ActivityCategoryRepository {

    private final MongoActivityCategoryRepository mongoRepository;

    public ActivityCategoryRepositoryImpl(MongoActivityCategoryRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public List<ActivityCategory> findAll() {
        return mongoRepository.findAll();
    }

    @Override
    public List<ActivityCategory> findAllActive() {
        return mongoRepository.findByActiveTrue();
    }

    @Override
    public Optional<ActivityCategory> findById(String id) {
        return mongoRepository.findById(id);
    }

    @Override
    public ActivityCategory save(ActivityCategory category) {
        return mongoRepository.save(category);
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return mongoRepository.existsById(id);
    }
}
