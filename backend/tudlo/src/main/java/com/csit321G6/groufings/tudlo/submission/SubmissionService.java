package com.csit321G6.groufings.tudlo.submission;

import com.csit321G6.groufings.tudlo.assessment.*;
import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.student.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class SubmissionService extends CrudService<Submission, SubmissionDto.Request, SubmissionDto.Response> {

    private final SubmissionRepository repository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssessmentRepository assessmentRepository;

    public SubmissionService(
            SubmissionRepository repository,
            EnrollmentRepository enrollmentRepository,
            AssessmentRepository assessmentRepository) {
        this.repository = repository;
        this.enrollmentRepository = enrollmentRepository;
        this.assessmentRepository = assessmentRepository;
    }

    @Override
    protected JpaRepository<Submission, Long> repository() {
        return repository;
    }

    @Override
    protected String entityName() {
        return "Submission";
    }

    @Override
    protected Submission newEntity() {
        return new Submission();
    }

    @Override
    protected void apply(Submission entity, SubmissionDto.Request request) {
        entity.setEnrollment(find(enrollmentRepository, request.enrollmentId(), "Enrollment"));
        entity.setAssessment(find(assessmentRepository, request.assessmentId(), "Assessment"));
        entity.setSubmittedAt(request.submittedAt());
        entity.setStatus(request.status());
    }

    @Override
    protected SubmissionDto.Response toResponse(Submission entity) {
        return new SubmissionDto.Response(
                entity.getId(),
                entity.getEnrollment().getId(),
                entity.getAssessment().getId(),
                entity.getSubmittedAt(),
                entity.getStatus());
    }
}
