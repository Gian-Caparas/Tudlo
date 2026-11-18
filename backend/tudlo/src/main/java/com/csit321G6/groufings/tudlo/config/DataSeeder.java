package com.csit321G6.groufings.tudlo.config;

import com.csit321G6.groufings.tudlo.entity.*;
import com.csit321G6.groufings.tudlo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            SubjectRepository subjectRepository,
            ClassListRepository classListRepository,
            AssessmentRepository assessmentRepository,
            ActivityRepository activityRepository,
            ScoresRepository scoresRepository,
            LearningGapRepository learningGapRepository,
            MyProgressRepository myProgressRepository,
            PlatformTransactionManager transactionManager,
            PasswordEncoder passwordEncoder
    ) {
        return args -> new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
                if (userRepository.count() > 0 && userRepository.findByUserIdNumber("2026-1001").isEmpty()) {
                    return;
                }

                // ============================================
                // 1. Create Users with their profiles
                // ============================================

                // Teacher 1
                User teacherUser1 = createTeacher("Jose", "Rizal", "2026-1001", userRepository, passwordEncoder);
                Teacher teacher1 = teacherUser1.getTeacher();

                // Teacher 2
                User teacherUser2 = createTeacher("Maria", "Clara", "2026-1002", userRepository, passwordEncoder);
                Teacher teacher2 = teacherUser2.getTeacher();

                // Students
                User studentUser1 = createStudent("Juan", "Dela Cruz", "2026-0001", userRepository, passwordEncoder);
                User studentUser2 = createStudent("Maria", "Santos", "2026-0002", userRepository, passwordEncoder);
                User studentUser3 = createStudent("Pedro", "Garcia", "2026-0003", userRepository, passwordEncoder);
                User studentUser4 = createStudent("Ana", "Reyes", "2026-0004", userRepository, passwordEncoder);
                User studentUser5 = createStudent("Luis", "Torres", "2026-0005", userRepository, passwordEncoder);

                // Admin
                createAdmin("Admin", "User", "2026-9999", userRepository, passwordEncoder);

                // ============================================
                // 2. Create Subjects (teacher teaches subject + section)
                // ============================================

                createSubject(subjectRepository, "AppDev", "G6", teacher1);
                createSubject(subjectRepository, "WebDev", "G6", teacher1);
                createSubject(subjectRepository, "MobileDev", "G7", teacher2);
                createSubject(subjectRepository, "Database", "G6", teacher2);

                // ============================================
                // 3. Create ClassList entries (students enrolled in subject/section)
                // ============================================

                // AppDev G6 - 5 students
                createClassList(classListRepository, "AppDev", "G6", studentUser1);
                createClassList(classListRepository, "AppDev", "G6", studentUser2);
                createClassList(classListRepository, "AppDev", "G6", studentUser3);
                createClassList(classListRepository, "AppDev", "G6", studentUser4);
                createClassList(classListRepository, "AppDev", "G6", studentUser5);

                // WebDev G6 - 3 students
                createClassList(classListRepository, "WebDev", "G6", studentUser1);
                createClassList(classListRepository, "WebDev", "G6", studentUser2);
                createClassList(classListRepository, "WebDev", "G6", studentUser3);

                // MobileDev G7 - 2 students
                createClassList(classListRepository, "MobileDev", "G7", studentUser4);
                createClassList(classListRepository, "MobileDev", "G7", studentUser5);

                // Database G6 - 4 students
                createClassList(classListRepository, "Database", "G6", studentUser1);
                createClassList(classListRepository, "Database", "G6", studentUser2);
                createClassList(classListRepository, "Database", "G6", studentUser3);
                createClassList(classListRepository, "Database", "G6", studentUser4);

                // ============================================
                // 4. Create Assessments (activities for each subject/section)
                // ============================================

                // AppDev G6 assessments
                createAssessment(assessmentRepository, "Quiz 1 - Variables", "Understand variables and data types", 20.0, "AppDev", "G6");
                createAssessment(assessmentRepository, "Quiz 2 - Control Flow", "Understand if/else and loops", 20.0, "AppDev", "G6");
                createAssessment(assessmentRepository, "Midterm Exam", "Comprehensive midterm covering weeks 1-8", 100.0, "AppDev", "G6");
                createAssessment(assessmentRepository, "Final Project", "Build a complete mobile app", 100.0, "AppDev", "G6");
                createAssessment(assessmentRepository, "Homework 1", "Practice exercises on variables", 10.0, "AppDev", "G6");
                createAssessment(assessmentRepository, "Homework 2", "Practice exercises on loops", 10.0, "AppDev", "G6");

                // WebDev G6 assessments
                createAssessment(assessmentRepository, "HTML/CSS Quiz", "Basic HTML and CSS", 30.0, "WebDev", "G6");
                createAssessment(assessmentRepository, "JavaScript Quiz", "JavaScript fundamentals", 30.0, "WebDev", "G6");
                createAssessment(assessmentRepository, "Final Project - Website", "Build a responsive website", 100.0, "WebDev", "G6");

                // MobileDev G7 assessments
                createAssessment(assessmentRepository, "Flutter Basics", "Introduction to Flutter", 50.0, "MobileDev", "G7");
                createAssessment(assessmentRepository, "Final App", "Complete mobile application", 100.0, "MobileDev", "G7");

                // Database G6 assessments
                createAssessment(assessmentRepository, "SQL Quiz 1", "SELECT, WHERE, ORDER BY", 25.0, "Database", "G6");
                createAssessment(assessmentRepository, "SQL Quiz 2", "JOINs and subqueries", 25.0, "Database", "G6");
                createAssessment(assessmentRepository, "Database Design Project", "Design and implement a database", 100.0, "Database", "G6");

                // ============================================
                // 5. Create Activities (student scores for each assessment)
                // ============================================

                // AppDev G6 - student scores (varied to test learning gaps)
                // Student 1 (Juan) - Good scores
                createActivity(activityRepository, "Quiz 1 - Variables", studentUser1, 18.0, "AppDev", "G6");
                createActivity(activityRepository, "Quiz 2 - Control Flow", studentUser1, 17.0, "AppDev", "G6");
                createActivity(activityRepository, "Midterm Exam", studentUser1, 85.0, "AppDev", "G6");
                createActivity(activityRepository, "Final Project", studentUser1, 92.0, "AppDev", "G6");
                createActivity(activityRepository, "Homework 1", studentUser1, 10.0, "AppDev", "G6");
                createActivity(activityRepository, "Homework 2", studentUser1, 9.0, "AppDev", "G6");

                // Student 2 (Maria) - Average scores
                createActivity(activityRepository, "Quiz 1 - Variables", studentUser2, 15.0, "AppDev", "G6");
                createActivity(activityRepository, "Quiz 2 - Control Flow", studentUser2, 14.0, "AppDev", "G6");
                createActivity(activityRepository, "Midterm Exam", studentUser2, 70.0, "AppDev", "G6");
                createActivity(activityRepository, "Final Project", studentUser2, 75.0, "AppDev", "G6");
                createActivity(activityRepository, "Homework 1", studentUser2, 8.0, "AppDev", "G6");
                createActivity(activityRepository, "Homework 2", studentUser2, 7.0, "AppDev", "G6");

                // Student 3 (Pedro) - LOW scores (at risk)
                createActivity(activityRepository, "Quiz 1 - Variables", studentUser3, 8.0, "AppDev", "G6");
                createActivity(activityRepository, "Quiz 2 - Control Flow", studentUser3, 7.0, "AppDev", "G6");
                createActivity(activityRepository, "Midterm Exam", studentUser3, 35.0, "AppDev", "G6");
                createActivity(activityRepository, "Final Project", studentUser3, 40.0, "AppDev", "G6");
                createActivity(activityRepository, "Homework 1", studentUser3, 5.0, "AppDev", "G6");
                createActivity(activityRepository, "Homework 2", studentUser3, 4.0, "AppDev", "G6");

                // Student 4 (Ana) - Good scores
                createActivity(activityRepository, "Quiz 1 - Variables", studentUser4, 19.0, "AppDev", "G6");
                createActivity(activityRepository, "Quiz 2 - Control Flow", studentUser4, 18.0, "AppDev", "G6");
                createActivity(activityRepository, "Midterm Exam", studentUser4, 90.0, "AppDev", "G6");
                createActivity(activityRepository, "Final Project", studentUser4, 95.0, "AppDev", "G6");
                createActivity(activityRepository, "Homework 1", studentUser4, 10.0, "AppDev", "G6");
                createActivity(activityRepository, "Homework 2", studentUser4, 10.0, "AppDev", "G6");

                // Student 5 (Luis) - Average
                createActivity(activityRepository, "Quiz 1 - Variables", studentUser5, 16.0, "AppDev", "G6");
                createActivity(activityRepository, "Quiz 2 - Control Flow", studentUser5, 15.0, "AppDev", "G6");
                createActivity(activityRepository, "Midterm Exam", studentUser5, 68.0, "AppDev", "G6");
                createActivity(activityRepository, "Final Project", studentUser5, 72.0, "AppDev", "G6");
                createActivity(activityRepository, "Homework 1", studentUser5, 9.0, "AppDev", "G6");
                createActivity(activityRepository, "Homework 2", studentUser5, 8.0, "AppDev", "G6");

                // WebDev G6 scores
                createActivity(activityRepository, "HTML/CSS Quiz", studentUser1, 28.0, "WebDev", "G6");
                createActivity(activityRepository, "JavaScript Quiz", studentUser1, 25.0, "WebDev", "G6");
                createActivity(activityRepository, "Final Project - Website", studentUser1, 88.0, "WebDev", "G6");

                createActivity(activityRepository, "HTML/CSS Quiz", studentUser2, 20.0, "WebDev", "G6");
                createActivity(activityRepository, "JavaScript Quiz", studentUser2, 18.0, "WebDev", "G6");
                createActivity(activityRepository, "Final Project - Website", studentUser2, 70.0, "WebDev", "G6");

                createActivity(activityRepository, "HTML/CSS Quiz", studentUser3, 12.0, "WebDev", "G6");
                createActivity(activityRepository, "JavaScript Quiz", studentUser3, 10.0, "WebDev", "G6");
                createActivity(activityRepository, "Final Project - Website", studentUser3, 45.0, "WebDev", "G6");

                // ============================================
                // 6. Create Scores (aggregated scores per student per subject/section)
                // ============================================

                // AppDev G6 total max = 20+20+100+100+10+10 = 260
                createScores(scoresRepository, studentUser1, "AppDev", "G6", 241.0); // 92.7%
                createScores(scoresRepository, studentUser2, "AppDev", "G6", 189.0); // 72.7%
                createScores(scoresRepository, studentUser3, "AppDev", "G6", 99.0);  // 38.1% - AT RISK
                createScores(scoresRepository, studentUser4, "AppDev", "G6", 250.0); // 96.2%
                createScores(scoresRepository, studentUser5, "AppDev", "G6", 186.0); // 71.5%

                // WebDev G6 total max = 30+30+100 = 160
                createScores(scoresRepository, studentUser1, "WebDev", "G6", 141.0); // 88.1%
                createScores(scoresRepository, studentUser2, "WebDev", "G6", 108.0); // 67.5%
                createScores(scoresRepository, studentUser3, "WebDev", "G6", 67.0);  // 41.9% - AT RISK

                // MobileDev G7 total max = 50+100 = 150
                createScores(scoresRepository, studentUser4, "MobileDev", "G7", 135.0); // 90%
                createScores(scoresRepository, studentUser5, "MobileDev", "G7", 120.0); // 80%

                // Database G6 total max = 25+25+100 = 150
                createScores(scoresRepository, studentUser1, "Database", "G6", 130.0); // 86.7%
                createScores(scoresRepository, studentUser2, "Database", "G6", 100.0); // 66.7%
                createScores(scoresRepository, studentUser3, "Database", "G6", 55.0);  // 36.7% - AT RISK
                createScores(scoresRepository, studentUser4, "Database", "G6", 125.0); // 83.3%

                // ============================================
                // 7. Calculate Learning Gaps (students below 60%)
                // ============================================

                // AppDev G6 max = 260
                calculateLearningGap(learningGapRepository, studentUser1, "AppDev", "G6", 241.0, 260.0);
                calculateLearningGap(learningGapRepository, studentUser2, "AppDev", "G6", 189.0, 260.0);
                calculateLearningGap(learningGapRepository, studentUser3, "AppDev", "G6", 99.0, 260.0);  // CRITICAL (38.1%)
                calculateLearningGap(learningGapRepository, studentUser4, "AppDev", "G6", 250.0, 260.0);
                calculateLearningGap(learningGapRepository, studentUser5, "AppDev", "G6", 186.0, 260.0);

                // WebDev G6 max = 160
                calculateLearningGap(learningGapRepository, studentUser1, "WebDev", "G6", 141.0, 160.0);
                calculateLearningGap(learningGapRepository, studentUser2, "WebDev", "G6", 108.0, 160.0);
                calculateLearningGap(learningGapRepository, studentUser3, "WebDev", "G6", 67.0, 160.0);  // HIGH (41.9%)

                // MobileDev G7 max = 150
                calculateLearningGap(learningGapRepository, studentUser4, "MobileDev", "G7", 135.0, 150.0);
                calculateLearningGap(learningGapRepository, studentUser5, "MobileDev", "G7", 120.0, 150.0);

                // Database G6 max = 150
                calculateLearningGap(learningGapRepository, studentUser1, "Database", "G6", 130.0, 150.0);
                calculateLearningGap(learningGapRepository, studentUser2, "Database", "G6", 100.0, 150.0);
                calculateLearningGap(learningGapRepository, studentUser3, "Database", "G6", 55.0, 150.0);  // CRITICAL (36.7%)
                calculateLearningGap(learningGapRepository, studentUser4, "Database", "G6", 125.0, 150.0);

                // ============================================
                // 8. Calculate MyProgress (student progress tracking)
                // ============================================

                // AppDev G6 - 6 assessments
                calculateMyProgress1(myProgressRepository, studentUser1, "AppDev", "G6", 92.7, 92.7, 6, 6);
                calculateMyProgress1(myProgressRepository, studentUser2, "AppDev", "G6", 72.7, 72.7, 6, 6);
                calculateMyProgress1(myProgressRepository, studentUser3, "AppDev", "G6", 38.1, 38.1, 6, 6);
                calculateMyProgress1(myProgressRepository, studentUser4, "AppDev", "G6", 96.2, 96.2, 6, 6);
                calculateMyProgress1(myProgressRepository, studentUser5, "AppDev", "G6", 71.5, 71.5, 6, 6);

                // WebDev G6 - 3 assessments
                calculateMyProgress1(myProgressRepository, studentUser1, "WebDev", "G6", 88.1, 88.1, 3, 3);
                calculateMyProgress1(myProgressRepository, studentUser2, "WebDev", "G6", 67.5, 67.5, 3, 3);
                calculateMyProgress1(myProgressRepository, studentUser3, "WebDev", "G6", 41.9, 41.9, 3, 3);

                // MobileDev G7 - 2 assessments
                calculateMyProgress1(myProgressRepository, studentUser4, "MobileDev", "G7", 90.0, 90.0, 2, 2);
                calculateMyProgress1(myProgressRepository, studentUser5, "MobileDev", "G7", 80.0, 80.0, 2, 2);

                // Database G6 - 3 assessments
                calculateMyProgress1(myProgressRepository, studentUser1, "Database", "G6", 86.7, 86.7, 3, 3);
                calculateMyProgress1(myProgressRepository, studentUser2, "Database", "G6", 66.7, 66.7, 3, 3);
                calculateMyProgress1(myProgressRepository, studentUser3, "Database", "G6", 36.7, 36.7, 3, 3);
                calculateMyProgress1(myProgressRepository, studentUser4, "Database", "G6", 83.3, 83.3, 3, 3);

                System.out.println("Seed data initialized; existing seed records were preserved.");
                System.out.println("   - 2 Teachers, 5 Students, 1 Admin");
                System.out.println("   - 4 Subjects (AppDev G6, WebDev G6, MobileDev G7, Database G6)");
                System.out.println("   - 14 ClassList entries");
                System.out.println("   - 14 Assessments");
                System.out.println("   - 39 Activities with scores");
                System.out.println("   - 14 Scores records");
                System.out.println("   - 14 LearningGap records (3 at risk: Pedro in AppDev, Pedro in WebDev, Pedro in Database)");
                System.out.println("   - 14 MyProgress records");
        });
    }

    // Helper methods

    private User createStudent(String firstName, String lastName, String idNumber, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        User studentUser = findOrCreateUser(firstName, lastName, idNumber, "pass1234", UserType.STUDENT, userRepository, passwordEncoder);
        if (studentUser.getStudent() == null) {
            Student student = new Student();
            student.setUser(studentUser);
            studentUser.setStudent(student);
            return userRepository.save(studentUser);
        }
        return studentUser;
    }

    private User createTeacher(String firstName, String lastName, String idNumber, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        User teacherUser = findOrCreateUser(firstName, lastName, idNumber, "admin123", UserType.TEACHER, userRepository, passwordEncoder);
        if (teacherUser.getTeacher() == null) {
            Teacher teacher = new Teacher();
            teacher.setUser(teacherUser);
            teacherUser.setTeacher(teacher);
            return userRepository.save(teacherUser);
        }
        return teacherUser;
    }

    private User createAdmin(String firstName, String lastName, String idNumber, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        User adminUser = findOrCreateUser(firstName, lastName, idNumber, "admin123", UserType.ADMIN, userRepository, passwordEncoder);
        if (adminUser.getAdmin() == null) {
            Admin admin = new Admin();
            admin.setUser(adminUser);
            adminUser.setAdmin(admin);
            return userRepository.save(adminUser);
        }
        return adminUser;
    }

    private User findOrCreateUser(String firstName, String lastName, String idNumber, String password, UserType userType, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        User user = userRepository.findByUserIdNumber(idNumber).orElseGet(() -> {
            User newUser = new User();
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setUserIdNumber(idNumber);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUserType(userType);
            return userRepository.save(newUser);
        });
        if (password.equals(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(password));
            user = userRepository.save(user);
        }
        return user;
    }

    private void createSubject(SubjectRepository repo, String subjectName, String classSection, Teacher teacher) {
        if (repo.existsBySubjectNameAndClassSectionAndTeacherId(subjectName, classSection, teacher.getId())) {
            return;
        }
        Subject subject = new Subject();
        subject.setSubjectName(subjectName);
        subject.setClassSection(classSection);
        subject.setTeacher(teacher);
        repo.save(subject);
    }

    private void createClassList(ClassListRepository repo, String subjectName, String classSection, User student) {
        if (repo.existsBySubjectNameAndClassSectionAndStudentId(subjectName, classSection, student.getUserIdNumber())) {
            return;
        }
        ClassList cl = new ClassList();
        cl.setSubjectName(subjectName);
        cl.setClassSection(classSection);
        cl.setStudentId(student.getUserIdNumber());
        cl.setFirstName(student.getFirstName());
        cl.setLastName(student.getLastName());
        repo.save(cl);
    }

    private Assessment createAssessment(AssessmentRepository repo, String item, String outcome, Double maxScore, String subjectName, String classSection) {
        return repo.findByAssessmentItemAndSubjectNameAndClassSection(item, subjectName, classSection)
                .orElseGet(() -> {
                    Assessment a = new Assessment();
                    a.setAssessmentItem(item);
                    a.setLearningOutcome(outcome);
                    a.setMaxScore(maxScore);
                    a.setSubjectName(subjectName);
                    a.setClassSection(classSection);
                    return repo.save(a);
                });
    }

    private void createActivity(ActivityRepository repo, String activityName, User student, Double score, String subjectName, String classSection) {
        if (repo.existsByActivityNameAndStudentIdAndSubjectNameAndClassSection(
                activityName, student.getUserIdNumber(), subjectName, classSection)) {
            return;
        }
        Activity a = new Activity();
        a.setActivityName(activityName);
        a.setStudentId(student.getUserIdNumber());
        a.setScore(score);
        a.setSubjectName(subjectName);
        a.setClassSection(classSection);
        repo.save(a);
    }

    private void createScores(ScoresRepository repo, User student, String subjectName, String classSection, Double score) {
        if (repo.existsByStudentIdAndSubjectNameAndClassSection(student.getUserIdNumber(), subjectName, classSection)) {
            return;
        }
        Scores s = new Scores();
        s.setStudentId(student.getUserIdNumber());
        s.setSubjectName(subjectName);
        s.setClassSection(classSection);
        s.setScore(score);
        repo.save(s);
    }

    private void calculateLearningGap(LearningGapRepository repo, User student, String subjectName, String classSection, Double overall, Double max) {
        if (repo.existsByStudentIdAndSubjectNameAndClassSection(student.getUserIdNumber(), subjectName, classSection)) {
            return;
        }
        LearningGap gap = new LearningGap();
        gap.setStudentId(student.getUserIdNumber());
        gap.setSubjectName(subjectName);
        gap.setClassSection(classSection);
        gap.setOverallScore(overall);
        gap.setMaxPossibleScore(max);
        gap.setPercentage((overall / max) * 100);
        gap.calculateGapSeverity();
        gap.setLastCalculated(LocalDateTime.now());
        repo.save(gap);
    }

    private void calculateMyProgress1(MyProgressRepository repo, User student, String subjectName, String classSection, Double avg, Double grade, Integer total, Integer completed) {
        if (repo.existsByStudentIdAndSubjectNameAndClassSection(student.getUserIdNumber(), subjectName, classSection)) {
            return;
        }
        MyProgress mp = new MyProgress();
        mp.setStudentId(student.getUserIdNumber());
        mp.setSubjectName(subjectName);
        mp.setClassSection(classSection);
        mp.setSubjectAverage(avg);
        mp.setOverallGrade(grade);
        mp.setTotalAssessments(total);
        mp.setCompletedAssessments(completed);
        mp.setLastUpdated(LocalDateTime.now());
        repo.save(mp);
    }
}