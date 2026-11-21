package com.csit321G6.groufings.tudlo.repository;

import com.csit321G6.groufings.tudlo.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:tudlo_jpa_test;MODE=MariaDB;DATABASE_TO_LOWER=TRUE;NON_KEYWORDS=USER;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
class StudentSchemaAndAssociationJpaTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SubjectOfferingRepository offeringRepository;

    @Autowired
    private ClassListRepository classListRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ScoresRepository scoresRepository;

    @Autowired
    private LearningGapRepository learningGapRepository;

    @Autowired
    private MyProgressRepository myProgressRepository;

    @Test
    void studentTableHasOnlyThreeRequiredColumns() throws Exception {
        Set<String> columns = new HashSet<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getColumns(null, null, "student", null)) {
                while (rs.next()) {
                    columns.add(rs.getString("COLUMN_NAME").toLowerCase(Locale.ROOT));
                }
            }
            if (columns.isEmpty()) {
                try (ResultSet rs = meta.getColumns(null, null, "STUDENT", null)) {
                    while (rs.next()) {
                        columns.add(rs.getString("COLUMN_NAME").toLowerCase(Locale.ROOT));
                    }
                }
            }
        }

        assertEquals(3, columns.size(), "student table must have exactly 3 columns, found: " + columns);
        assertEquals(Set.of("student_id", "firstname", "lastname"), columns);
    }

    @Test
    void supportsMultiTeacherSameSubjectSectionAndMultiEnrollmentWithoutCollisions() {
        Teacher teacherA = persistTeacher("2026-1001", "Alice", "Reyes");
        Teacher teacherB = persistTeacher("2026-1002", "Bob", "Santos");

        Student student = new Student();
        student.setStudentId("2026-2001");
        student.setFirstName("Clara");
        student.setLastName("Cruz");
        student = studentRepository.saveAndFlush(student);

        Subject appDev = persistSubject("AppDev");
        Subject webDev = persistSubject("WebDev");
        Section secG6 = persistSection("G6");
        Section secG7 = persistSection("G7");

        SubjectOffering offeringA = persistOffering(teacherA, appDev, secG6);
        SubjectOffering offeringB = persistOffering(teacherB, appDev, secG6);
        SubjectOffering offeringC = persistOffering(teacherA, webDev, secG7);

        persistEnrollment(offeringA, student);
        persistEnrollment(offeringB, student);
        persistEnrollment(offeringC, student);

        assertEquals(3, classListRepository.findByStudentStudentId("2026-2001").size());
        assertEquals(2, offeringRepository.findByTeacherId(teacherA.getId()).size());
        assertEquals(1, offeringRepository.findByTeacherId(teacherB.getId()).size());

        Assessment quizA = persistAssessment(offeringA, "Quiz 1", "LO1", 20.0);
        Assessment quizB = persistAssessment(offeringB, "Quiz 1", "LO1", 25.0);

        Activity actA = persistActivity(quizA, student, 18.0);
        Activity actB = persistActivity(quizB, student, 10.0);

        assertNotEquals(actA.getId(), actB.getId());
        assertEquals(18.0, activityRepository.findByStudentStudentIdAndAssessmentOfferingId("2026-2001", offeringA.getId()).get(0).getScore());
        assertEquals(10.0, activityRepository.findByStudentStudentIdAndAssessmentOfferingId("2026-2001", offeringB.getId()).get(0).getScore());

        Scores scoreA = new Scores();
        scoreA.setOffering(offeringA);
        scoreA.setStudent(student);
        scoreA.setScore(18.0);
        scoresRepository.saveAndFlush(scoreA);

        Scores scoreB = new Scores();
        scoreB.setOffering(offeringB);
        scoreB.setStudent(student);
        scoreB.setScore(10.0);
        scoresRepository.saveAndFlush(scoreB);

        LearningGap gapA = persistGap(offeringA, student, 18.0, 20.0, 90.0);
        LearningGap gapB = persistGap(offeringB, student, 10.0, 25.0, 40.0);
        assertFalse(gapA.getIsAtRisk());
        assertTrue(gapB.getIsAtRisk());
        assertEquals(1, learningGapRepository.findAtRiskByTeacherId(teacherB.getId()).size());
        assertEquals(0, learningGapRepository.findAtRiskByTeacherId(teacherA.getId()).size());

        MyProgress progA = persistProgress(offeringA, student, 90.0);
        MyProgress progB = persistProgress(offeringB, student, 40.0);
        assertNotEquals(progA.getId(), progB.getId());
        assertEquals(2, myProgressRepository.findByStudentStudentIdOrderByOfferingSubjectSubjectNameAsc("2026-2001").size());
    }

    @Test
    void duplicateEnrollmentInSameOfferingViolatesUniqueConstraint() {
        Teacher teacher = persistTeacher("2026-1003", "Dan", "Lim");
        Student student = new Student();
        student.setStudentId("2026-2002");
        student.setFirstName("Eva");
        student.setLastName("Tan");
        student = studentRepository.saveAndFlush(student);

        Subject subj = persistSubject("Math 101");
        Section sect = persistSection("G1");
        SubjectOffering offering = persistOffering(teacher, subj, sect);

        persistEnrollment(offering, student);

        ClassList duplicate = new ClassList();
        duplicate.setOffering(offering);
        duplicate.setStudent(student);
        assertThrows(DataIntegrityViolationException.class, () -> classListRepository.saveAndFlush(duplicate));
    }

    @Test
    void learningGapCalculateGapSeverityHandlesNullAndBoundaryPercentages() {
        LearningGap gap = new LearningGap();
        gap.setPercentage(null);
        gap.calculateGapSeverity();
        assertFalse(gap.getIsAtRisk());
        assertEquals("UNKNOWN", gap.getGapSeverity());

        gap.setPercentage(59.99);
        gap.calculateGapSeverity();
        assertTrue(gap.getIsAtRisk());
        assertEquals("MODERATE", gap.getGapSeverity());

        gap.setPercentage(60.0);
        gap.calculateGapSeverity();
        assertFalse(gap.getIsAtRisk());
        assertEquals("LOW", gap.getGapSeverity());
    }

    private Teacher persistTeacher(String userIdNumber, String first, String last) {
        User u = new User();
        u.setUserIdNumber(userIdNumber);
        u.setFirstName(first);
        u.setLastName(last);
        u.setPassword("encoded");
        u.setUserType(UserType.TEACHER);
        u.setCreatedAt(LocalDateTime.now());
        u = userRepository.saveAndFlush(u);

        Teacher t = new Teacher();
        t.setUser(u);
        return teacherRepository.saveAndFlush(t);
    }

    private Subject persistSubject(String name) {
        Subject s = new Subject();
        s.setSubjectName(name);
        return subjectRepository.saveAndFlush(s);
    }

    private Section persistSection(String name) {
        Section s = new Section();
        s.setSectionName(name);
        return sectionRepository.saveAndFlush(s);
    }

    private SubjectOffering persistOffering(Teacher t, Subject s, Section sec) {
        SubjectOffering o = new SubjectOffering();
        o.setTeacher(t);
        o.setSubject(s);
        o.setSection(sec);
        return offeringRepository.saveAndFlush(o);
    }

    private ClassList persistEnrollment(SubjectOffering o, Student s) {
        ClassList cl = new ClassList();
        cl.setOffering(o);
        cl.setStudent(s);
        return classListRepository.saveAndFlush(cl);
    }

    private Assessment persistAssessment(SubjectOffering o, String item, String lo, double max) {
        Assessment a = new Assessment();
        a.setOffering(o);
        a.setAssessmentItem(item);
        a.setLearningOutcome(lo);
        a.setMaxScore(max);
        return assessmentRepository.saveAndFlush(a);
    }

    private Activity persistActivity(Assessment a, Student s, double score) {
        Activity act = new Activity();
        act.setAssessment(a);
        act.setStudent(s);
        act.setScore(score);
        return activityRepository.saveAndFlush(act);
    }

    private LearningGap persistGap(SubjectOffering o, Student s, double score, double max, Double pct) {
        LearningGap lg = new LearningGap();
        lg.setOffering(o);
        lg.setStudent(s);
        lg.setOverallScore(score);
        lg.setMaxPossibleScore(max);
        lg.setPercentage(pct);
        lg.calculateGapSeverity();
        lg.setLastCalculated(LocalDateTime.now());
        return learningGapRepository.saveAndFlush(lg);
    }

    private MyProgress persistProgress(SubjectOffering o, Student s, double avg) {
        MyProgress mp = new MyProgress();
        mp.setOffering(o);
        mp.setStudent(s);
        mp.setSubjectAverage(avg);
        mp.setOverallGrade(avg);
        mp.setTotalAssessments(1);
        mp.setCompletedAssessments(1);
        mp.setLastUpdated(LocalDateTime.now());
        return myProgressRepository.saveAndFlush(mp);
    }
}
