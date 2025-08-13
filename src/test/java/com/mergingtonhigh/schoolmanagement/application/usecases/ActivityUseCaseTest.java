package com.mergingtonhigh.schoolmanagement.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mergingtonhigh.schoolmanagement.application.dtos.ActivityDTO;
import com.mergingtonhigh.schoolmanagement.application.mappers.ActivityMapper;
import com.mergingtonhigh.schoolmanagement.domain.entities.Activity;
import com.mergingtonhigh.schoolmanagement.domain.enums.ActivityCategory;
import com.mergingtonhigh.schoolmanagement.domain.repositories.ActivityRepository;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.ScheduleDetails;

@ExtendWith(MockitoExtension.class)
class ActivityUseCaseTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ActivityMapper activityMapper;

    private ActivityUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ActivityUseCase(activityRepository, activityMapper);
    }

    @Test
    void shouldReturnAllActivitiesWhenNoFiltersProvided() {
        Activity activity1 = createTestActivity("Chess Club", ActivityCategory.ACADEMIC);
        Activity activity2 = createTestActivity("Art Club", ActivityCategory.ARTS);
        List<Activity> activities = Arrays.asList(activity1, activity2);

        ActivityDTO dto1 = createTestActivityDTO("Chess Club", ActivityCategory.ACADEMIC);
        ActivityDTO dto2 = createTestActivityDTO("Art Club", ActivityCategory.ARTS);

        when(activityRepository.findAll()).thenReturn(activities);
        when(activityMapper.toDTO(activity1)).thenReturn(dto1);
        when(activityMapper.toDTO(activity2)).thenReturn(dto2);

        Map<String, ActivityDTO> result = useCase.getActivities(null, null, null, null);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("Chess Club"));
        assertTrue(result.containsKey("Art Club"));
    }

    @Test
    void shouldFilterActivitiesByDay() {
        String day = "Monday";
        Activity activity = createTestActivity("Chess Club", ActivityCategory.ACADEMIC);
        List<Activity> activities = Arrays.asList(activity);

        ActivityDTO dto = createTestActivityDTO("Chess Club", ActivityCategory.ACADEMIC);

        when(activityRepository.findByDay(day)).thenReturn(activities);
        when(activityMapper.toDTO(activity)).thenReturn(dto);

        Map<String, ActivityDTO> result = useCase.getActivities(day, null, null, null);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("Chess Club"));
    }

    @Test
    void shouldFilterActivitiesByTimeRange() {
        String startTime = "15:00";
        String endTime = "17:00";
        LocalTime start = LocalTime.of(15, 0);
        LocalTime end = LocalTime.of(17, 0);

        Activity activity = createTestActivity("Chess Club", ActivityCategory.ACADEMIC);
        List<Activity> activities = Arrays.asList(activity);

        ActivityDTO dto = createTestActivityDTO("Chess Club", ActivityCategory.ACADEMIC);

        when(activityRepository.findByTimeRange(start, end)).thenReturn(activities);
        when(activityMapper.toDTO(activity)).thenReturn(dto);

        Map<String, ActivityDTO> result = useCase.getActivities(null, startTime, endTime, null);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("Chess Club"));
    }

    @Test
    void shouldFilterActivitiesByDayAndTimeRange() {
        String day = "Monday";
        String startTime = "15:00";
        String endTime = "17:00";
        LocalTime start = LocalTime.of(15, 0);
        LocalTime end = LocalTime.of(17, 0);

        Activity activity = createTestActivity("Chess Club", ActivityCategory.ACADEMIC);
        List<Activity> activities = Arrays.asList(activity);

        ActivityDTO dto = createTestActivityDTO("Chess Club", ActivityCategory.ACADEMIC);

        when(activityRepository.findByDayAndTimeRange(day, start, end)).thenReturn(activities);
        when(activityMapper.toDTO(activity)).thenReturn(dto);

        Map<String, ActivityDTO> result = useCase.getActivities(day, startTime, endTime, null);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("Chess Club"));
    }

    @Test
    void shouldFilterActivitiesByCategory() {
        Activity activity1 = createTestActivity("Chess Club", ActivityCategory.ACADEMIC);
        Activity activity2 = createTestActivity("Art Club", ActivityCategory.ARTS);
        List<Activity> activities = Arrays.asList(activity1, activity2);

        ActivityDTO dto1 = createTestActivityDTO("Chess Club", ActivityCategory.ACADEMIC);
        ActivityDTO dto2 = createTestActivityDTO("Art Club", ActivityCategory.ARTS);

        when(activityRepository.findAll()).thenReturn(activities);
        when(activityMapper.toDTO(activity1)).thenReturn(dto1);
        when(activityMapper.toDTO(activity2)).thenReturn(dto2);

        Map<String, ActivityDTO> result = useCase.getActivities(null, null, null, "ACADEMIC");

        assertEquals(1, result.size());
        assertTrue(result.containsKey("Chess Club"));
    }

    @Test
    void shouldReturnAvailableDaysSorted() {
        List<String> days = Arrays.asList("Wednesday", "Monday", "Friday");
        List<String> expectedSorted = Arrays.asList("Friday", "Monday", "Wednesday");

        when(activityRepository.findAllUniqueDays()).thenReturn(days);

        List<String> result = useCase.getAvailableDays();

        assertEquals(expectedSorted, result);
    }

    @Test
    void shouldUseOverloadedMethodWithoutCategory() {
        String day = "Monday";
        String startTime = "15:00";
        String endTime = "17:00";
        LocalTime start = LocalTime.of(15, 0);
        LocalTime end = LocalTime.of(17, 0);

        Activity activity = createTestActivity("Chess Club", ActivityCategory.ACADEMIC);
        List<Activity> activities = Arrays.asList(activity);

        ActivityDTO dto = createTestActivityDTO("Chess Club", ActivityCategory.ACADEMIC);

        when(activityRepository.findByDayAndTimeRange(day, start, end)).thenReturn(activities);
        when(activityMapper.toDTO(activity)).thenReturn(dto);

        Map<String, ActivityDTO> result = useCase.getActivities(day, startTime, endTime);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("Chess Club"));
    }

    private Activity createTestActivity(String name, ActivityCategory category) {
        ScheduleDetails schedule = new ScheduleDetails(
                Arrays.asList("Monday"),
                LocalTime.of(15, 30),
                LocalTime.of(17, 0));

        return new Activity(name, "Test Description", schedule, 12, category);
    }

    private ActivityDTO createTestActivityDTO(String name, ActivityCategory category) {
        ActivityDTO.ScheduleDetailsDTO scheduleDTO = new ActivityDTO.ScheduleDetailsDTO(
                Arrays.asList("Monday"),
                "15:30",
                "17:00");

        return new ActivityDTO(
                name,
                "Test Description",
                "Monday 15:30-17:00",
                scheduleDTO,
                12,
                Arrays.asList(),
                0,
                category,
                true);
    }
}