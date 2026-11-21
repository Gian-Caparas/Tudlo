package com.csit321G6.groufings.tudlo.controller;

import com.csit321G6.groufings.tudlo.common.CrudController;
import com.csit321G6.groufings.tudlo.common.CrudService;
import com.csit321G6.groufings.tudlo.dto.ClassListDto;
import com.csit321G6.groufings.tudlo.service.ClassListService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public List<ClassListDto.Response> bulkImport(@Valid @RequestBody ClassListDto.BulkImportRequest request) {
        return service.bulkImport(request);
    }

    @GetMapping("/offering/{offeringId}")
    public List<ClassListDto.Response> getByOfferingId(@PathVariable Long offeringId) {
        return service.getByOfferingId(offeringId);
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

    @PostMapping(value = "/import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ClassListDto.ExcelImportResult importExcel(@RequestParam("file") MultipartFile file) {
        return service.importExcel(file);
    }
}
