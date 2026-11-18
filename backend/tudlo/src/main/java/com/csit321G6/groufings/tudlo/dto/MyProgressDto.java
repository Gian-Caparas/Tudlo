package com.csit321G6.groufings.tudlo.dto;

import java.time.LocalDateTime;

public final class MyProgressDto {

    private MyProgressDto() {
    }

    public record Response(
            Long id,
            String studentId,
            String subjectName,
            String classSection,
            Double subjectAverage,
            Double overallGrade,
            Integer totalAssessments,
            Integer completedAssessments,
            Double completionRate,
            LocalDateTime lastUpdated
    ) {
    }

    public record SummaryResponse(
            String subjectName,
            String classSection,
            Double subjectAverage,
            Double overallGrade,
            Integer totalAssessments,
            Integer completedAssessments,
            Double completionRate
    ) {
    }
}