package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.dto.LearningGapDto;
import com.csit321G6.groufings.tudlo.service.LearningGapService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-gaps")
public class LearningGapController {

    private final LearningGapService service;

    public LearningGapController(LearningGapService service) {
        this.service = service;
    }

    @GetMapping("/at-risk/subject/{subjectName}/section/{classSection}")
    public List<LearningGapDto.Response> getAtRiskStudents(
            @PathVariable String subjectName,
            @PathVariable String classSection) {
        return service.getAtRiskStudents(subjectName, classSection);
    }

    @GetMapping("/subject/{subjectName}/section/{classSection}")
    public List<LearningGapDto.Response> getAllGapsBySubjectAndSection(
            @PathVariable String subjectName,
            @PathVariable String classSection) {
        return service.getAllGapsBySubjectAndSection(subjectName, classSection);
    }

    @GetMapping("/student/{studentId}")
    public List<LearningGapDto.Response> getByStudentId(@PathVariable String studentId) {
        return service.getByStudentId(studentId);
    }

    @GetMapping("/heatmap/subject/{subjectName}/section/{classSection}")
    public List<LearningGapDto.HeatmapResponse> getHeatmapData(
            @PathVariable String subjectName,
            @PathVariable String classSection) {
        return service.getHeatmapData(subjectName, classSection);
    }

    @PostMapping("/calculate")
    public LearningGapDto.Response calculateGap(
            @RequestParam String studentId,
            @RequestParam String subjectName,
            @RequestParam String classSection,
            @RequestParam Double overallScore,
            @RequestParam Double maxPossibleScore) {
        return service.calculateAndSaveGap(studentId, subjectName, classSection, overallScore, maxPossibleScore);
    }
}