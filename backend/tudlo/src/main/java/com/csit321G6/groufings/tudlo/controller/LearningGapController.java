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

    @GetMapping("/at-risk/teacher/{teacherId}")
    public List<LearningGapDto.Response> getAtRiskByTeacherId(@PathVariable Long teacherId) {
        return service.getAtRiskByTeacherId(teacherId);
    }

    @GetMapping("/at-risk/offering/{offeringId}")
    public List<LearningGapDto.Response> getAtRiskByOfferingId(@PathVariable Long offeringId) {
        return service.getAtRiskByOfferingId(offeringId);
    }

    @GetMapping("/offering/{offeringId}")
    public List<LearningGapDto.Response> getByOfferingId(@PathVariable Long offeringId) {
        return service.getByOfferingId(offeringId);
    }

    @GetMapping("/heatmap/offering/{offeringId}")
    public List<LearningGapDto.HeatmapResponse> getHeatmapByOfferingId(@PathVariable Long offeringId) {
        return service.getHeatmapByOfferingId(offeringId);
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
            @RequestParam(required = false) Long offeringId,
            @RequestParam String studentId,
            @RequestParam(required = false) String subjectName,
            @RequestParam(required = false) String classSection,
            @RequestParam Double overallScore,
            @RequestParam Double maxPossibleScore) {
        return service.calculateAndSaveGap(
                offeringId, studentId, subjectName, classSection, overallScore, maxPossibleScore);
    }
}
