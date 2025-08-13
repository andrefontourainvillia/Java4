package com.mergingtonhigh.schoolmanagement.infrastructure.migrations;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mergingtonhigh.schoolmanagement.domain.entities.Activity;
import com.mergingtonhigh.schoolmanagement.domain.entities.Teacher;
import com.mergingtonhigh.schoolmanagement.domain.enums.ActivityCategory;
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
                seedActivities(teachers);
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

        private void seedActivities(List<Teacher> teachers) {
                Activity chessClub = new Activity(
                                "Clube de Xadrez",
                                "Desenvolva estratégias de pensamento crítico através do xadrez",
                                new ScheduleDetails(Arrays.asList("Tuesday", "Thursday"), LocalTime.of(15, 30),
                                                LocalTime.of(17, 0)),
                                20,
                                ActivityCategory.ACADEMIC);
                chessClub.setCanTeachersRegisterStudents(true);
                chessClub.setParticipants(Arrays.asList("michael@mergington.edu", "daniel@mergington.edu"));
                mongoTemplate.save(chessClub);

                Activity programmingClass = new Activity(
                                "Clube de Programação",
                                "Aprenda fundamentos de programação com Java e Python",
                                new ScheduleDetails(Arrays.asList("Monday", "Wednesday", "Friday"), LocalTime.of(14, 0),
                                                LocalTime.of(15, 30)),
                                15,
                                ActivityCategory.TECHNOLOGY);
                programmingClass.setCanTeachersRegisterStudents(true);
                programmingClass.setParticipants(Arrays.asList("emma@mergington.edu", "sophia@mergington.edu"));
                mongoTemplate.save(programmingClass);

                Activity artClub = new Activity(
                                "Clube de Arte",
                                "Explore sua criatividade através de diversas formas de arte",
                                new ScheduleDetails(Arrays.asList("Tuesday", "Thursday"), LocalTime.of(16, 0),
                                                LocalTime.of(17, 30)),
                                25,
                                ActivityCategory.ARTS);
                artClub.setCanTeachersRegisterStudents(true);
                artClub.setParticipants(Arrays.asList("amelia@mergington.edu", "harper@mergington.edu"));
                mongoTemplate.save(artClub);

                Activity soccerTeam = new Activity(
                                "Time de Futebol",
                                "Equipe competitiva de futebol da escola",
                                new ScheduleDetails(Arrays.asList("Monday", "Wednesday", "Friday"), LocalTime.of(16, 0),
                                                LocalTime.of(18, 0)),
                                30,
                                ActivityCategory.SPORTS);
                soccerTeam.setCanTeachersRegisterStudents(false); // Only admins can register for sports team
                soccerTeam.setParticipants(Arrays.asList("liam@mergington.edu", "noah@mergington.edu"));
                mongoTemplate.save(soccerTeam);

                Activity musicBand = new Activity(
                                "Banda de Música",
                                "Banda escolar para estudantes com experiência musical",
                                new ScheduleDetails(Arrays.asList("Tuesday", "Thursday"), LocalTime.of(15, 0),
                                                LocalTime.of(16, 30)),
                                35,
                                ActivityCategory.ARTS);
                musicBand.setCanTeachersRegisterStudents(true);
                mongoTemplate.save(musicBand);

                Activity communityService = new Activity(
                                "Serviço Comunitário",
                                "Projetos de serviço comunitário e voluntariado",
                                new ScheduleDetails(Arrays.asList("Saturday"), LocalTime.of(9, 0), LocalTime.of(12, 0)),
                                40,
                                ActivityCategory.CLUBS);
                communityService.setCanTeachersRegisterStudents(true);
                mongoTemplate.save(communityService);
        }

        private Teacher createTeacher(String username, String displayName, Teacher.Role role, String rawPassword) {
                return new Teacher(username, displayName, passwordEncoder.encode(rawPassword), role);
        }

        @RollbackExecution
        public void rollback() {
                mongoTemplate.remove(new Query(), Activity.class);
                mongoTemplate.remove(new Query(), Teacher.class);

                mongoTemplate.indexOps(Activity.class).dropAllIndexes();
                mongoTemplate.indexOps(Teacher.class).dropAllIndexes();
        }
}
