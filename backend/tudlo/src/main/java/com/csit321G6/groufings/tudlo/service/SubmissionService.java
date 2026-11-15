package com.csit321G6.groufings.tudlo.service;

import com.csit321G6.groufings.tudlo.common.*;
import com.csit321G6.groufings.tudlo.common.exception.BadRequestException;
import com.csit321G6.groufings.tudlo.dto.SubmissionDto;
import com.csit321G6.groufings.tudlo.entity.Assessment;
import com.csit321G6.groufings.tudlo.entity.Enrollment;
import com.csit321G6.groufings.tudlo.entity.Submission;
import com.csit321G6.groufings.tudlo.repo.AssessmentRepository;
import com.csit321G6.groufings.tudlo.repo.EnrollmentRepository;
import com.csit321G6.groufings.tudlo.repo.SubmissionRepository;
import com.csit321G6.groufings.tudlo.security.DataOwnershipPolicy;
import com.csit321G6.groufings.tudlo.security.RolePolicy;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class SubmissionService extends CrudService<Submission, SubmissionDto.Request, SubmissionDto.Response> {

    private final SubmissionRepository repository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssessmentRepository assessmentRepository;
    private final DataOwnershipPolicy ownershipPolicy;
    private final RolePolicy rolePolicy;

    public SubmissionService(
            SubmissionRepository repository,
            EnrollmentRepository enrollmentRepository,
            AssessmentRepository assessmentRepository,
            DataOwnershipPolicy ownershipPolicy,
            RolePolicy rolePolicy) {
        this.repository = repository;
        this.enrollmentRepository = enrollmentRepository;
        this.assessmentRepository = assessmentRepository;
        this.ownershipPolicy = ownershipPolicy;
        this.rolePolicy = rolePolicy;
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
    protected List<Submission> filterForRead(List<Submission> submissions) {
        var actor = rolePolicy.currentActor();
        return submissions.stream()
                .filter(submission -> ownershipPolicy.canReadSubmission(actor, submission))
                .toList();
    }

    @Override
    protected void beforeRead(Submission submission) {
        ownershipPolicy.assertCanReadSubmission(rolePolicy.currentActor(), submission);
    }

    @Override
    protected void beforeCreate(Submission submission, SubmissionDto.Request request) {
        assertWritableReferences(request);
    }

    @Override
    protected void beforeUpdate(Submission existing, SubmissionDto.Request request) {
        var actor = rolePolicy.currentActor();
        ownershipPolicy.assertTeacherOwns(actor, existing);
        assertWritableReferences(request);
    }

    @Override
    protected void beforeDelete(Submission existing) {
        ownershipPolicy.assertTeacherOwns(rolePolicy.currentActor(), existing);
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

    private void assertWritableReferences(SubmissionDto.Request request) {
        var enrollment = find(enrollmentRepository, request.enrollmentId(), "Enrollment");
        Assessment assessment = find(assessmentRepository, request.assessmentId(), "Assessment");
        if (!enrollment.getClassRoom().getId().equals(assessment.getClassRoom().getId())) {
            throw new BadRequestException("The submission enrollment and assessment must belong to the same class");
        }
        ownershipPolicy.assertTeacherOwnsBoth(
                rolePolicy.currentActor(), enrollment.getClassRoom(), assessment.getClassRoom());
    }
}
