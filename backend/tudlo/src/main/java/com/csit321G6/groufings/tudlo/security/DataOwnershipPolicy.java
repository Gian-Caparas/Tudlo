package com.csit321G6.groufings.tudlo.security;

import com.csit321G6.groufings.tudlo.common.exception.ForbiddenException;
import com.csit321G6.groufings.tudlo.config.AuthenticatedUser;
import com.csit321G6.groufings.tudlo.entity.ClassRoom;
import com.csit321G6.groufings.tudlo.entity.ItemScore;
import com.csit321G6.groufings.tudlo.entity.Submission;
import com.csit321G6.groufings.tudlo.entity.Teacher;
import com.csit321G6.groufings.tudlo.entity.UserType;
import org.springframework.stereotype.Component;

@Component
public class DataOwnershipPolicy {

    public boolean canReadClass(AuthenticatedUser actor, ClassRoom classRoom) {
        return isDeveloper(actor) || ownsClass(actor, classRoom);
    }

    public boolean canReadSubmission(AuthenticatedUser actor, Submission submission) {
        if (isDeveloper(actor)) {
            return true;
        }
        if (actor.role() == UserType.TEACHER) {
            return ownsClass(actor, submission.getAssessment().getClassRoom())
                    && ownsClass(actor, submission.getEnrollment().getClassRoom());
        }
        return actor.role() == UserType.STUDENT
                && actor.userId().equals(submission.getEnrollment().getStudent().getUser().getId());
    }

    public boolean canReadItemScore(AuthenticatedUser actor, ItemScore score) {
        if (isDeveloper(actor)) {
            return true;
        }
        if (actor.role() == UserType.TEACHER) {
            return canReadSubmission(actor, score.getSubmission())
                    && ownsClass(actor, score.getItem().getAssessment().getClassRoom());
        }
        return actor.role() == UserType.STUDENT
                && actor.userId().equals(score.getSubmission().getEnrollment().getStudent().getUser().getId());
    }

    public void assertCanReadClass(AuthenticatedUser actor, ClassRoom classRoom) {
        if (!canReadClass(actor, classRoom)) {
            throw new ForbiddenException("You cannot access a class owned by another teacher.");
        }
    }

    public void assertCanReadSubmission(AuthenticatedUser actor, Submission submission) {
        if (!canReadSubmission(actor, submission)) {
            throw new ForbiddenException("You cannot access this submission.");
        }
    }

    public void assertCanReadItemScore(AuthenticatedUser actor, ItemScore score) {
        if (!canReadItemScore(actor, score)) {
            throw new ForbiddenException("You cannot access this item score.");
        }
    }

    public void assertTeacherOwns(AuthenticatedUser actor, Teacher teacher) {
        if (isDeveloper(actor)) {
            return;
        }
        if (actor.role() != UserType.TEACHER || !actor.userId().equals(teacher.getUser().getId())) {
            throw new ForbiddenException("Teachers may only manage their own classes and records.");
        }
    }

    public void assertTeacherOwns(AuthenticatedUser actor, ClassRoom classRoom) {
        if (!isDeveloper(actor) && !ownsClass(actor, classRoom)) {
            throw new ForbiddenException("Teachers may only manage their own classes and records.");
        }
    }

    public void assertTeacherOwns(AuthenticatedUser actor, Submission submission) {
        if (isDeveloper(actor)) {
            return;
        }
        if (actor.role() != UserType.TEACHER
                || !ownsClass(actor, submission.getAssessment().getClassRoom())
                || !ownsClass(actor, submission.getEnrollment().getClassRoom())) {
            throw new ForbiddenException("Teachers may only manage their own classes and records.");
        }
    }

    public void assertTeacherOwns(AuthenticatedUser actor, ItemScore score) {
        if (isDeveloper(actor)) {
            return;
        }
        if (actor.role() != UserType.TEACHER
                || !ownsClass(actor, score.getItem().getAssessment().getClassRoom())
                || !ownsClass(actor, score.getSubmission().getAssessment().getClassRoom())
                || !ownsClass(actor, score.getSubmission().getEnrollment().getClassRoom())) {
            throw new ForbiddenException("Teachers may only manage their own classes and records.");
        }
    }

    public void assertTeacherOwnsBoth(AuthenticatedUser actor, ClassRoom first, ClassRoom second) {
        if (isDeveloper(actor)) {
            return;
        }
        assertTeacherOwns(actor, first);
        assertTeacherOwns(actor, second);
        if (!first.getId().equals(second.getId())) {
            throw new ForbiddenException("The selected records must belong to the same class.");
        }
    }

    private boolean ownsClass(AuthenticatedUser actor, ClassRoom classRoom) {
        return actor.role() == UserType.TEACHER
                && actor.userId().equals(classRoom.getTeacher().getUser().getId());
    }

    private boolean isDeveloper(AuthenticatedUser actor) {
        return actor.role() == UserType.DEVELOPER;
    }
}
