package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.common.CrudController;
import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.ScoresDto;
import com.csit321G6.groufings.tudlo.service.ScoresService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class ScoresController extends CrudController<ScoresDto.Request, ScoresDto.Response> {

    private final ScoresService service;

    public ScoresController(ScoresService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, ScoresDto.Request, ScoresDto.Response> service() {
        return service;
    }

    @GetMapping("/student/{studentId}")
    public List<ScoresDto.Response> getByStudentId(@PathVariable String studentId) {
        return service.getByStudentId(studentId);
    }

    @GetMapping("/subject/{subjectName}/section/{classSection}")
    public List<ScoresDto.Response> getBySubjectAndSection(
            @PathVariable String subjectName,
            @PathVariable String classSection) {
        return service.getBySubjectAndSection(subjectName, classSection);
    }

    @GetMapping("/student/{studentId}/subject/{subjectName}/section/{classSection}")
    public List<ScoresDto.Response> getByStudentSubjectAndSection(
            @PathVariable String studentId,
            @PathVariable String subjectName,
            @PathVariable String classSection) {
        return service.getByStudentSubjectAndSection(studentId, subjectName, classSection);
    }
}