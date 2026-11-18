package com.csit321G6.groufings.tudlo.dto;

import java.time.LocalDateTime;

public final class LearningGapDto {

    private LearningGapDto() {
    }

    public record Response(
            Long id,
            String studentId,
            String studentName,
            String subjectName,
            String classSection,
            Double overallScore,
            Double maxPossibleScore,
            Double percentage,
            Boolean isAtRisk,
            String gapSeverity,
            LocalDateTime lastCalculated
    ) {
    }

    public record HeatmapResponse(
            String studentId,
            String studentName,
            Double percentage,
            String gapSeverity,
            Boolean isAtRisk
    ) {
    }
}