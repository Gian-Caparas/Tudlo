package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.common.exception.BadRequestException;
import com.csit321G6.groufings.tudlo.common.exception.ConflictException;
import com.csit321G6.groufings.tudlo.common.exception.ResourceNotFoundException;
import com.csit321G6.groufings.tudlo.dto.ActivityDto;
import com.csit321G6.groufings.tudlo.entity.*;
import com.csit321G6.groufings.tudlo.repository.*;
import com.csit321G6.groufings.tudlo.security.AccessPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@SuppressWarnings("null")
public class ActivityService extends CrudService<Activity, ActivityDto.Request, ActivityDto.Response> {

    private final ActivityRepository activityRepository;
    private final AssessmentRepository assessmentRepository;
    private final StudentRepository studentRepository;
    private final ClassListRepository classListRepository;
    private final ScoresRepository scoresRepository;
    private final LearningGapRepository learningGapRepository;
    private final MyProgressRepository myProgressRepository;
    private final SubjectOfferingService offeringService;
    private final AccessPolicy accessPolicy;

    public ActivityService(
            ActivityRepository activityRepository,
            AssessmentRepository assessmentRepository,
            StudentRepository studentRepository,
            ClassListRepository classListRepository,
            ScoresRepository scoresRepository,
            LearningGapRepository learningGapRepository,
            MyProgressRepository myProgressRepository,
            SubjectOfferingService offeringService,
            AccessPolicy accessPolicy) {
        this.activityRepository = activityRepository;
        this.assessmentRepository = assessmentRepository;
        this.studentRepository = studentRepository;
        this.classListRepository = classListRepository;
        this.scoresRepository = scoresRepository;
        this.learningGapRepository = learningGapRepository;
        this.myProgressRepository = myProgressRepository;
        this.offeringService = offeringService;
        this.accessPolicy = accessPolicy;
    }

    @Override
    protected JpaRepository<Activity, Long> repository() {
        return activityRepository;
    }

    @Override
    protected String entityName() {
        return "Activity";
    }

    @Override
    protected Activity newEntity() {
        return new Activity();
    }

    @Override
    protected List<Activity> filterForRead(List<Activity> entities) {
        User current = accessPolicy.requireCurrentUser();
        if (current.getUserType() == UserType.TEACHER) {
            Teacher teacher = accessPolicy.requireCurrentTeacher();
            return entities.stream()
                    .filter(a -> a.getAssessment() != null
                            && a.getAssessment().getOffering() != null
                            && a.getAssessment().getOffering().getTeacher() != null
                            && teacher.getId().equals(a.getAssessment().getOffering().getTeacher().getId()))
                    .toList();
        }
        return entities;
    }

    @Override
    protected void beforeRead(Activity entity) {
        accessPolicy.requireOfferingReadAccess(entity.getAssessment().getOffering());
    }

    @Override
    protected void beforeUpdate(Activity existing, ActivityDto.Request request) {
        accessPolicy.requireOfferingWriteAccess(existing.getAssessment().getOffering());
    }

    @Override
    protected void beforeDelete(Activity existing) {
        accessPolicy.requireOfferingWriteAccess(existing.getAssessment().getOffering());
    }

    @Override
    protected void apply(Activity entity, ActivityDto.Request request) {
        Assessment assessment = resolveAssessment(request);
        SubjectOffering offering = assessment.getOffering();
        accessPolicy.requireOfferingWriteAccess(offering);

        String studentId = java.util.Objects.requireNonNull(request.studentId(), "Student ID is required").trim();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (!classListRepository.existsByOfferingIdAndStudentStudentId(offering.getId(), student.getStudentId())) {
            throw new BadRequestException("Student " + student.getStudentId()
                    + " is not enrolled in offering " + offering.getId());
        }

        if (request.score() < 0 || request.score() > assessment.getMaxScore()) {
            throw new BadRequestException("Score must be between 0 and maxScore (" + assessment.getMaxScore() + ")");
        }

        activityRepository.findByAssessmentIdAndStudentStudentId(assessment.getId(), student.getStudentId())
                .filter(existing -> entity.getId() == null || !existing.getId().equals(entity.getId()))
                .ifPresent(existing -> {
                    throw new ConflictException("Activity score already recorded for student "
                            + student.getStudentId() + " on assessment '" + assessment.getAssessmentItem() + "'");
                });

        entity.setAssessment(assessment);
        entity.setStudent(student);
        entity.setScore(request.score());
    }

    @Override
    @Transactional
    public ActivityDto.Response create(ActivityDto.Request request) {
        ActivityDto.Response response = super.create(request);
        Long assessmentId = java.util.Objects.requireNonNull(response.assessmentId(), "Assessment ID is required");
        String studentId = java.util.Objects.requireNonNull(response.studentId(), "Student ID is required");
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment", assessmentId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
        syncDerivedMetrics(assessment.getOffering(), student);
        return response;
    }

    @Override
    @Transactional
    public ActivityDto.Response update(Long id, ActivityDto.Request request) {
        ActivityDto.Response response = super.update(id, request);
        Long assessmentId = java.util.Objects.requireNonNull(response.assessmentId(), "Assessment ID is required");
        String studentId = java.util.Objects.requireNonNull(response.studentId(), "Student ID is required");
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment", assessmentId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
        syncDerivedMetrics(assessment.getOffering(), student);
        return response;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Activity existing = java.util.Objects.requireNonNull(getEntity(id));
        beforeDelete(existing);
        SubjectOffering offering = existing.getAssessment().getOffering();
        Student student = existing.getStudent();
        activityRepository.delete(existing);
        activityRepository.flush();
        syncDerivedMetrics(offering, student);
    }

    private Assessment resolveAssessment(ActivityDto.Request request) {
        if (request.assessmentId() != null) {
            return assessmentRepository.findById(request.assessmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assessment", request.assessmentId()));
        }
        String activityName = request.activityName() != null ? request.activityName().trim() : "";
        if (activityName.isEmpty()) {
            throw new BadRequestException("Either assessmentId or activityName is required");
        }
        SubjectOffering offering = offeringService.resolveOffering(
                request.offeringId(), request.subjectName(), request.classSection());
        return assessmentRepository.findByOfferingIdAndAssessmentItemIgnoreCase(offering.getId(), activityName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assessment '" + activityName + "' not found for offering " + offering.getId()));
    }

    private void syncDerivedMetrics(SubjectOffering offering, Student student) {
        if (offering == null || student == null) {
            return;
        }
        List<Activity> studentActivities = activityRepository
                .findByStudentStudentIdAndAssessmentOfferingId(student.getStudentId(), offering.getId());

        double overallScore = studentActivities.stream()
                .filter(a -> a.getScore() != null)
                .mapToDouble(a -> a.getScore())
                .sum();

        double maxPossibleScore = studentActivities.stream()
                .filter(a -> a.getScore() != null && a.getAssessment() != null && a.getAssessment().getMaxScore() != null)
                .mapToDouble(a -> a.getAssessment().getMaxScore())
                .sum();

        Double percentage = maxPossibleScore > 0 ? (overallScore / maxPossibleScore) * 100.0 : null;

        Scores scoreEntity = scoresRepository
                .findByOfferingIdAndStudentStudentId(offering.getId(), student.getStudentId())
                .orElseGet(Scores::new);
        scoreEntity.setOffering(offering);
        scoreEntity.setStudent(student);
        scoreEntity.setScore(overallScore);
        scoresRepository.save(scoreEntity);

        LearningGap gap = learningGapRepository
                .findByOfferingIdAndStudentStudentId(offering.getId(), student.getStudentId())
                .orElseGet(LearningGap::new);
        gap.setOffering(offering);
        gap.setStudent(student);
        gap.setOverallScore(overallScore);
        gap.setMaxPossibleScore(maxPossibleScore);
        gap.setPercentage(percentage);
        gap.calculateGapSeverity();
        gap.setLastCalculated(LocalDateTime.now());
        learningGapRepository.save(gap);

        int totalAssessments = (int) assessmentRepository.countByOfferingId(offering.getId());
        int completedAssessments = studentActivities.size();

        MyProgress progress = myProgressRepository
                .findByOfferingIdAndStudentStudentId(offering.getId(), student.getStudentId())
                .orElseGet(MyProgress::new);
        progress.setOffering(offering);
        progress.setStudent(student);
        progress.setSubjectAverage(percentage != null ? percentage : 0.0);
        // overallGrade should be null when no activities/assessments, 0.0 when graded
        progress.setOverallGrade(percentage);
        progress.setTotalAssessments(totalAssessments);
        progress.setCompletedAssessments(completedAssessments);
        progress.setLastUpdated(LocalDateTime.now());
        myProgressRepository.save(progress);
    }

    @Override
    protected ActivityDto.Response toResponse(Activity entity) {
        Assessment assessment = entity.getAssessment();
        SubjectOffering offering = assessment != null ? assessment.getOffering() : null;
        Student student = entity.getStudent();
        return new ActivityDto.Response(
                entity.getId(),
                assessment != null ? assessment.getId() : null,
                offering != null ? offering.getId() : null,
                assessment != null ? assessment.getAssessmentItem() : null,
                student != null ? student.getStudentId() : null,
                entity.getScore(),
                assessment != null ? assessment.getMaxScore() : null,
                offering != null ? offering.getSubjectName() : null,
                offering != null ? offering.getClassSection() : null,
                offering != null && offering.getTeacher() != null ? offering.getTeacher().getId() : null,
                offering != null ? offering.getTeacherName() : null,
                null
        );
    }

    @Transactional(readOnly = true)
    public List<ActivityDto.Response> getByOfferingId(Long offeringId) {
        SubjectOffering offering = offeringService.getOfferingEntity(offeringId);
        accessPolicy.requireOfferingReadAccess(offering);
        return activityRepository.findByAssessmentOfferingId(offeringId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDto.Response> getByStudentId(String studentId) {
        accessPolicy.requireStudentReadAccess(studentId);
        User current = accessPolicy.requireCurrentUser();
        List<Activity> activities = activityRepository.findByStudentStudentId(studentId);
        if (current.getUserType() == UserType.TEACHER) {
            Teacher teacher = accessPolicy.requireCurrentTeacher();
            activities = activities.stream()
                    .filter(a -> a.getAssessment() != null
                            && a.getAssessment().getOffering() != null
                            && a.getAssessment().getOffering().getTeacher() != null
                            && teacher.getId().equals(a.getAssessment().getOffering().getTeacher().getId()))
                    .toList();
        }
        return activities.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDto.Response> getByStudentAndOffering(String studentId, Long offeringId) {
        accessPolicy.requireStudentReadAccess(studentId);
        SubjectOffering offering = offeringService.getOfferingEntity(offeringId);
        accessPolicy.requireOfferingReadAccess(offering);
        return activityRepository.findByStudentStudentIdAndAssessmentOfferingId(studentId, offeringId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDto.Response> getBySubjectAndSection(String subjectName, String classSection) {
        return offeringService.resolveOfferingsForRead(subjectName, classSection).stream()
                .flatMap(offering -> activityRepository.findByAssessmentOfferingId(offering.getId()).stream())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDto.Response> getByStudentSubjectAndSection(
            String studentId, String subjectName, String classSection) {
        accessPolicy.requireStudentReadAccess(studentId);
        return offeringService.resolveOfferingsForRead(subjectName, classSection).stream()
                .flatMap(offering -> activityRepository
                        .findByStudentStudentIdAndAssessmentOfferingId(studentId, offering.getId()).stream())
                .map(this::toResponse)
                .toList();
    }
}
