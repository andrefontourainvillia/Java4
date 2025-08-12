package com.mergingtonhigh.schoolmanagement.infrastructure.migrations;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mergingtonhigh.schoolmanagement.domain.entities.Activity;
import com.mergingtonhigh.schoolmanagement.domain.entities.ActivityCategory;
import com.mergingtonhigh.schoolmanagement.domain.entities.Teacher;
import com.mergingtonhigh.schoolmanagement.domain.valueobjects.ScheduleDetails;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

@ChangeUnit(id = "initial-database-setup", order = "001", author = "Andre Fontoura")
public class V001_InitialDatabaseSetup {

        private final MongoTemplate mongoTemplate;
        private final PasswordEncoder passwordEncoder;
        private final Environment environment;

        public V001_InitialDatabaseSetup(MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder,
                        Environment environment) {
                this.mongoTemplate = mongoTemplate;
                this.passwordEncoder = passwordEncoder;
                this.environment = environment;
        }

        @Execution
        public void changeSet() {
                List<Teacher> teachers = seedTeachers();
                List<ActivityCategory> categories = seedCategories();
                seedActivities(teachers, categories);
        }

        private List<ActivityCategory> seedCategories() {
                List<ActivityCategory> categories = Arrays.asList(
                                new ActivityCategory("sports", "Esportes", "#28a745", "#ffffff",
                                                "Atividades físicas e esportivas"),
                                new ActivityCategory("arts", "Artes", "#6f42c1", "#ffffff",
                                                "Atividades artísticas e criativas"),
                                new ActivityCategory("academic", "Acadêmico", "#007bff", "#ffffff",
                                                "Atividades educacionais e acadêmicas"),
                                new ActivityCategory("technology", "Tecnologia", "#17a2b8", "#ffffff",
                                                "Atividades relacionadas à tecnologia"),
                                new ActivityCategory("community", "Comunidade", "#fd7e14", "#ffffff",
                                                "Atividades de serviço comunitário"));

                mongoTemplate.insertAll(categories);
                return categories;
        }

        private List<Teacher> seedTeachers() {
                List<Teacher> teachers = Arrays.asList(
                                createTeacher("maria", "Maria Rodriguez", Teacher.Role.TEACHER,
                                                environment.getProperty("TEACHER_MARIA_PASSWORD",
                                                                "123123")),
                                createTeacher("jose", "Prof. Jose Chen", Teacher.Role.TEACHER,
                                                environment.getProperty("TEACHER_JOSE_PASSWORD", "123123")),
                                createTeacher("paulo", "Paulo Silva", Teacher.Role.ADMIN,
                                                environment.getProperty("DIRECTOR_PAULO_PASSWORD",
                                                                "123123")));

                mongoTemplate.insertAll(teachers);
                return teachers;
        }

        private void seedActivities(List<Teacher> teachers, List<ActivityCategory> categories) {
                Activity chessClub = new Activity(
                                "Clube de Xadrez",
                                "Desenvolva estratégias de pensamento crítico através do xadrez",
                                new ScheduleDetails(Arrays.asList("Tuesday", "Thursday"), LocalTime.of(15, 30),
                                                LocalTime.of(17, 0)),
                                20,
                                findCategoryId(categories, "academic"));
                chessClub.assignTeacher("jose");
                chessClub.setParticipants(Arrays.asList("michael@mergington.edu", "daniel@mergington.edu"));
                mongoTemplate.save(chessClub);

                Activity programmingClass = new Activity(
                                "Clube de Programação",
                                "Aprenda fundamentos de programação com Java e Python",
                                new ScheduleDetails(Arrays.asList("Monday", "Wednesday", "Friday"), LocalTime.of(14, 0),
                                                LocalTime.of(15, 30)),
                                15,
                                findCategoryId(categories, "technology"));
                programmingClass.assignTeacher("jose");
                programmingClass.setParticipants(Arrays.asList("emma@mergington.edu", "sophia@mergington.edu"));
                mongoTemplate.save(programmingClass);

                Activity artClub = new Activity(
                                "Clube de Arte",
                                "Explore sua criatividade através de diversas formas de arte",
                                new ScheduleDetails(Arrays.asList("Tuesday", "Thursday"), LocalTime.of(16, 0),
                                                LocalTime.of(17, 30)),
                                25,
                                findCategoryId(categories, "arts"));
                artClub.assignTeacher("maria");
                artClub.setParticipants(Arrays.asList("amelia@mergington.edu", "harper@mergington.edu"));
                mongoTemplate.save(artClub);

                Activity soccerTeam = new Activity(
                                "Time de Futebol",
                                "Equipe competitiva de futebol da escola",
                                new ScheduleDetails(Arrays.asList("Monday", "Wednesday", "Friday"), LocalTime.of(16, 0),
                                                LocalTime.of(18, 0)),
                                30,
                                findCategoryId(categories, "sports"));
                soccerTeam.assignTeacher("maria");
                soccerTeam.setParticipants(Arrays.asList("liam@mergington.edu", "noah@mergington.edu"));
                mongoTemplate.save(soccerTeam);

                Activity musicBand = new Activity(
                                "Banda de Música",
                                "Banda escolar para estudantes com experiência musical",
                                new ScheduleDetails(Arrays.asList("Tuesday", "Thursday"), LocalTime.of(15, 0),
                                                LocalTime.of(16, 30)),
                                35,
                                findCategoryId(categories, "arts"));
                musicBand.assignTeacher("maria");
                musicBand.assignTeacher("jose");
                mongoTemplate.save(musicBand);

                Activity communityService = new Activity(
                                "Serviço Comunitário",
                                "Projetos de serviço comunitário e voluntariado",
                                new ScheduleDetails(Arrays.asList("Saturday"), LocalTime.of(9, 0), LocalTime.of(12, 0)),
                                40,
                                findCategoryId(categories, "community"));
                communityService.assignTeacher("maria");
                communityService.assignTeacher("jose");
                mongoTemplate.save(communityService);
        }

        private Teacher createTeacher(String username, String displayName, Teacher.Role role, String rawPassword) {
                return new Teacher(username, displayName, passwordEncoder.encode(rawPassword), role);
        }

        private String findCategoryId(List<ActivityCategory> categories, String typeCode) {
                return categories.stream()
                                .filter(cat -> cat.getType().equals(typeCode))
                                .findFirst()
                                .map(ActivityCategory::getId)
                                .orElseThrow(() -> new IllegalStateException("Category not found: " + typeCode));
        }

        @RollbackExecution
        public void rollback() {
                mongoTemplate.remove(new Query(), Activity.class);
                mongoTemplate.remove(new Query(), Teacher.class);
                mongoTemplate.remove(new Query(), ActivityCategory.class);

                mongoTemplate.indexOps(Activity.class).dropAllIndexes();
                mongoTemplate.indexOps(Teacher.class).dropAllIndexes();
                mongoTemplate.indexOps(ActivityCategory.class).dropAllIndexes();
        }
}
