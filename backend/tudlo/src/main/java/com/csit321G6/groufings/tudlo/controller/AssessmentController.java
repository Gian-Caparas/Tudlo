package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.common.CrudController;
import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.AssessmentDto;
import com.csit321G6.groufings.tudlo.service.AssessmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController extends CrudController<AssessmentDto.Request, AssessmentDto.Response> {

    private final AssessmentService service;

    public AssessmentController(AssessmentService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, AssessmentDto.Request, AssessmentDto.Response> service() {
        return service;
    }

    @GetMapping("/offering/{offeringId}")
    public List<AssessmentDto.Response> getByOfferingId(@PathVariable Long offeringId) {
        return service.getByOfferingId(offeringId);
    }

    @GetMapping("/subject/{subjectName}/section/{classSection}")
    public List<AssessmentDto.Response> getBySubjectAndSection(
            @PathVariable String subjectName,
            @PathVariable String classSection) {
        return service.getBySubjectAndSection(subjectName, classSection);
    }
}
