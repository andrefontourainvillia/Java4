package com.mergingtonhigh.schoolmanagement.application.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mergingtonhigh.schoolmanagement.domain.entities.Activity;
import com.mergingtonhigh.schoolmanagement.domain.entities.ActivityCategory;
import com.mergingtonhigh.schoolmanagement.domain.entities.Teacher;
import com.mergingtonhigh.schoolmanagement.domain.repositories.ActivityCategoryRepository;
import com.mergingtonhigh.schoolmanagement.domain.repositories.ActivityRepository;
import com.mergingtonhigh.schoolmanagement.domain.repositories.TeacherRepository;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.CategoryReference;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.TeacherReference;

@Service
public class ActivitySyncServiceImpl implements ActivitySyncService {

    private final ActivityRepository activityRepository;
    private final TeacherRepository teacherRepository;
    private final ActivityCategoryRepository categoryRepository;

    public ActivitySyncServiceImpl(ActivityRepository activityRepository,
            TeacherRepository teacherRepository,
            ActivityCategoryRepository categoryRepository) {
        this.activityRepository = activityRepository;
        this.teacherRepository = teacherRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void syncTeacherDataInActivities(Teacher teacher) {
        if (teacher == null) {
            return;
        }

        List<Activity> activities = activityRepository.findAll();
        TeacherReference teacherRef = TeacherReference.fromTeacher(teacher);

        for (Activity activity : activities) {
            if (activity.isTeacherAssigned(teacher.getUsername())) {
                // Atualiza os dados embarcados do professor
                activity.getAssignedTeacherReferences().removeIf(t -> t.getUsername().equals(teacher.getUsername()));
                activity.assignTeacher(teacherRef);
                activityRepository.save(activity);
            }
        }
    }

    @Override
    public void syncCategoryDataInActivities(ActivityCategory category) {
        if (category == null) {
            return;
        }

        List<Activity> activities = activityRepository.findByCategoryId(category.getId());
        CategoryReference categoryRef = CategoryReference.fromActivityCategory(category);

        for (Activity activity : activities) {
            activity.setCategory(categoryRef);
            activityRepository.save(activity);
        }
    }

    @Override
    public void removeTeacherFromActivities(String teacherUsername) {
        if (teacherUsername == null || teacherUsername.trim().isEmpty()) {
            return;
        }

        List<Activity> activities = activityRepository.findAll();

        for (Activity activity : activities) {
            if (activity.isTeacherAssigned(teacherUsername)) {
                activity.removeTeacher(teacherUsername);
                activityRepository.save(activity);
            }
        }
    }

    @Override
    public void removeCategoryFromActivities(String categoryId) {
        if (categoryId == null || categoryId.trim().isEmpty()) {
            return;
        }

        List<Activity> activities = activityRepository.findByCategoryId(categoryId);

        for (Activity activity : activities) {
            activity.setCategory(null);
            activity.setCategoryId(null);
            activityRepository.save(activity);
        }
    }

    @Override
    public void syncActivityEmbeddedData(String activityName) {
        if (activityName == null || activityName.trim().isEmpty()) {
            return;
        }

        Optional<Activity> optionalActivity = activityRepository.findByName(activityName);
        if (!optionalActivity.isPresent()) {
            return;
        }

        Activity activity = optionalActivity.get();
        boolean hasChanges = false;

        // Sincroniza categoria
        if (activity.getCategoryId() != null) {
            Optional<ActivityCategory> optionalCategory = categoryRepository.findById(activity.getCategoryId());
            if (optionalCategory.isPresent()) {
                CategoryReference categoryRef = CategoryReference.fromActivityCategory(optionalCategory.get());
                activity.setCategory(categoryRef);
                hasChanges = true;
            }
        }

        // Sincroniza professores
        List<String> teacherUsernames = activity.getAssignedTeacherUsernames();
        if (!teacherUsernames.isEmpty()) {
            activity.getAssignedTeacherReferences().clear();

            for (String username : teacherUsernames) {
                Optional<Teacher> optionalTeacher = teacherRepository.findByUsername(username);
                if (optionalTeacher.isPresent()) {
                    TeacherReference teacherRef = TeacherReference.fromTeacher(optionalTeacher.get());
                    activity.assignTeacher(teacherRef);
                    hasChanges = true;
                }
            }
        }

        if (hasChanges) {
            activityRepository.save(activity);
        }
    }

    @Override
    public void syncAllActivities() {
        List<Activity> activities = activityRepository.findAll();

        for (Activity activity : activities) {
            syncActivityEmbeddedData(activity.getName());
        }
    }
}
