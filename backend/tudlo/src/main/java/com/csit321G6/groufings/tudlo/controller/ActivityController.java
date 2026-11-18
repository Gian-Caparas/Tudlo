package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.common.CrudController;
import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.ActivityDto;
import com.csit321G6.groufings.tudlo.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController extends CrudController<ActivityDto.Request, ActivityDto.Response> {

    private final ActivityService service;

    public ActivityController(ActivityService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, ActivityDto.Request, ActivityDto.Response> service() {
        return service;
    }

    @GetMapping("/student/{studentId}")
    public List<ActivityDto.Response> getByStudentId(@PathVariable String studentId) {
        return service.getByStudentId(studentId);
    }

    @GetMapping("/subject/{subjectName}/section/{classSection}")
    public List<ActivityDto.Response> getBySubjectAndSection(
            @PathVariable String subjectName,
            @PathVariable String classSection) {
        return service.getBySubjectAndSection(subjectName, classSection);
    }

    @GetMapping("/student/{studentId}/subject/{subjectName}/section/{classSection}")
    public List<ActivityDto.Response> getByStudentSubjectAndSection(
            @PathVariable String studentId,
            @PathVariable String subjectName,
            @PathVariable String classSection) {
        return service.getByStudentSubjectAndSection(studentId, subjectName, classSection);
    }
}