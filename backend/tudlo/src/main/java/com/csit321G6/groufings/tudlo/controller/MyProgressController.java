package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.dto.MyProgressDto;
import com.csit321G6.groufings.tudlo.service.MyProgressService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/my-progress")
public class MyProgressController {

    private final MyProgressService service;

    public MyProgressController(MyProgressService service) {
        this.service = service;
    }

    @GetMapping("/student/{studentId}")
    public List<MyProgressDto.Response> getByStudentId(@PathVariable String studentId) {
        return service.getByStudentId(studentId);
    }

    @GetMapping("/student/{studentId}/summary")
    public List<MyProgressDto.SummaryResponse> getSummaryByStudentId(@PathVariable String studentId) {
        return service.getSummaryByStudentId(studentId);
    }

    @GetMapping("/offering/{offeringId}")
    public List<MyProgressDto.Response> getByOfferingId(@PathVariable Long offeringId) {
        return service.getByOfferingId(offeringId);
    }

    @GetMapping("/subject/{subjectName}/section/{classSection}")
    public List<MyProgressDto.Response> getBySubjectAndSection(
            @PathVariable String subjectName,
            @PathVariable String classSection) {
        return service.getBySubjectAndSection(subjectName, classSection);
    }

    @PostMapping("/calculate")
    public MyProgressDto.Response calculateProgress(
            @RequestParam(required = false) Long offeringId,
            @RequestParam String studentId,
            @RequestParam(required = false) String subjectName,
            @RequestParam(required = false) String classSection,
            @RequestParam Double subjectAverage,
            @RequestParam Double overallGrade,
            @RequestParam Integer totalAssessments,
            @RequestParam Integer completedAssessments) {
        return service.calculateAndSaveProgress(
                offeringId, studentId, subjectName, classSection,
                subjectAverage, overallGrade, totalAssessments, completedAssessments);
    }
}
