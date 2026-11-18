package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.common.CrudController;
import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.ClassListDto;
import com.csit321G6.groufings.tudlo.service.ClassListService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class-lists")
public class ClassListController extends CrudController<ClassListDto.Request, ClassListDto.Response> {

    private final ClassListService service;

    public ClassListController(ClassListService service) {
        this.service = service;
    }

    @Override
    protected CrudService<?, ClassListDto.Request, ClassListDto.Response> service() {
        return service;
    }

    @PostMapping("/bulk-import")
    public List<ClassListDto.Response> bulkImport(@RequestBody ClassListDto.BulkImportRequest request) {
        return service.bulkImport(request);
    }

    @GetMapping("/subject/{subjectName}/section/{classSection}")
    public List<ClassListDto.Response> getBySubjectAndSection(
            @PathVariable String subjectName,
            @PathVariable String classSection) {
        return service.getBySubjectAndSection(subjectName, classSection);
    }

    @GetMapping("/student/{studentId}")
    public List<ClassListDto.Response> getByStudentId(@PathVariable String studentId) {
        return service.getByStudentId(studentId);
    }
}