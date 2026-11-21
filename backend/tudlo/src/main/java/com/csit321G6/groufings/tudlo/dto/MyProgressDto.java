package com.csit321G6.groufings.tudlo.dto;

import java.time.LocalDateTime;

public final class MyProgressDto {

    private MyProgressDto() {
    }

    public record Response(
            Long id,
            Long offeringId,
            String studentId,
            String subjectName,
            String classSection,
            Long teacherId,
            String teacherName,
            Double subjectAverage,
            Double overallGrade,
            Integer totalAssessments,
            Integer completedAssessments,
            Double completionRate,
            LocalDateTime lastUpdated
    ) {
    }

    public record SummaryResponse(
            Long offeringId,
            String subjectName,
            String classSection,
            Long teacherId,
            String teacherName,
            Double subjectAverage,
            Double overallGrade,
            Integer totalAssessments,
            Integer completedAssessments,
            Double completionRate
    ) {
    }
}
